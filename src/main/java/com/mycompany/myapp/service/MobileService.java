package com.mycompany.myapp.service;

import com.google.common.collect.Streams;
import com.google.firebase.messaging.FirebaseMessaging;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class MobileService {

    private final JobTypeRepository jobTypeRepository;
    private final UserRepository userRepository;

    public MobileService(JobTypeRepository jobTypeRepository, UserRepository userRepository) {
        this.jobTypeRepository = jobTypeRepository;
        this.userRepository = userRepository;
    }

    public void subscribeUserToATopic(User currentUser, Long id) {
            JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobTYpe not found", "JobType", "wrong id"));
            FirebaseMessaging.getInstance().subscribeToTopicAsync(currentUser.getDevices(), id.toString());
            currentUser.getJobTypes().add(jobType);
            userRepository.save(currentUser);
    }

    public void unsubscribeUserToATopic(User currentUser, Long id) {
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobType not found", "JobType", "wrong id"));
        FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(currentUser.getDevices(), jobType.getId().toString());
        currentUser.getJobTypes().remove(jobType);
        userRepository.save(currentUser);
    }

    public void subscribeUserToAllTopics(User user){
        user.getCompany().getJobTypes().forEach(jobType -> {
            FirebaseMessaging.getInstance().subscribeToTopicAsync(user.getDevices(), jobType.getId().toString());
        });
    }

    public void subscribeNewDeviceToTopics(List<JobType> jobTypes, String newDeviceToken) {
        jobTypes.forEach(jobType -> FirebaseMessaging.getInstance().subscribeToTopicAsync(Collections.singletonList(newDeviceToken), jobType.getId().toString()));
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
        FirebaseMessaging.getInstance().subscribeToTopicAsync(deviceTokens, id.toString());
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
        FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(deviceTokens, jobType.getId().toString());
    }
}
