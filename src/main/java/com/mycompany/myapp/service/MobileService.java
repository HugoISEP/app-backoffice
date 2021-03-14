package com.mycompany.myapp.service;

import com.google.common.collect.Streams;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class MobileService {

    private final JobTypeRepository jobTypeRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public MobileService(JobTypeRepository jobTypeRepository, UserService userService, UserRepository userRepository) {
        this.jobTypeRepository = jobTypeRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public void subscribeToATopic(List<String> deviceTokens, String topic){
        FirebaseMessaging.getInstance().subscribeToTopicAsync(deviceTokens, topic);
    }

    public void unsubscribeToATopic(List<String> deviceTokens, String topic){
        FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(deviceTokens, topic);
    }

    public void subscribeUserToATopic(Long id) {
            User currentUser = userService.getUserWithAuthorities()
                .orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "wrong id"));
            JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobTYpe not found", "JobType", "wrong id"));
            subscribeToATopic(currentUser.getDevices(), jobType.getId().toString());
            currentUser.getJobTypes().add(jobType);
            userRepository.save(currentUser);
    }

    public void unsubscribeUserToATopic(Long id) {
        User currentUser = userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "wrong id"));
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobType not found", "JobType", "wrong id"));
        unsubscribeToATopic(currentUser.getDevices(), jobType.getId().toString());
        currentUser.getJobTypes().remove(jobType);
        userRepository.save(currentUser);
    }

    public void subscribeUserToAllTopics(User user){
        user.getCompany().getJobTypes().forEach(jobType -> {
            subscribeToATopic(user.getDevices(), jobType.getId().toString());
            user.getJobTypes().add(jobType);
        });
    }

    @Async
    public void subscribeAllUsersToNewTopic(Long id){
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("jobType not found", "JobType", "wrong id"));
        List<String> deviceTokens = new ArrayList<>();
        jobType.getCompany().getUsers().stream()
            .filter(usr -> usr.getAuthorities().stream().noneMatch(authority -> authority.getName().equals(AuthoritiesConstants.ADMIN)))
            .forEach(usr -> {
                usr.setJobTypes(Streams.concat(usr.getJobTypes().stream(), Stream.of(jobType)).collect(Collectors.toList()));
                deviceTokens.addAll(usr.getDevices());
            });
        subscribeToATopic(deviceTokens, id.toString());
    }

    @Async
    public void unsubscribeAllUsersDeletedTopic(JobType jobType){
        List<String> deviceTokens = new ArrayList<>();
        jobType.getCompany().getUsers().forEach(user -> {
            if(user.getJobTypes().contains(jobType)){
                deviceTokens.addAll(user.getDevices());
                user.getJobTypes().remove(jobType);
            }
        });
        unsubscribeToATopic(deviceTokens, jobType.getId().toString());
    }
}
