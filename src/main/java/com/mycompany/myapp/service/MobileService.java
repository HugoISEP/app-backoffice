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

    public void subscribeFirebaseTopic(Long id) throws FirebaseMessagingException {
        try {
            User currentUser = userService.getUserWithAuthorities()
                .orElseThrow(() -> new BadRequestAlertException("user not found", "USER", "id exists"));
            JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found", "JobType", "id doesn't exist"));
            FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getDevices(), jobType.getId().toString());
            currentUser.getJobTypes().add(jobType);
            userRepository.save(currentUser);
        } catch (FirebaseMessagingException e){
            log.error(String.format("Can't subscribe to the topic %d : %s", id, e.getMessage()));
        }
    }

    public void unsubscribeFirebaseTopic(Long id) throws FirebaseMessagingException {
        log.warn("REST request to unsubscribeFirebaseTopic : {}", id);
        try {
            User currentUser = userService.getUserWithAuthorities()
                .orElseThrow(() -> new BadRequestAlertException("user not found", "USER", "id exists"));
            JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found", "JobType", "id doesn't exist"));
            FirebaseMessaging.getInstance().unsubscribeFromTopic(currentUser.getDevices(), jobType.getId().toString());
            currentUser.getJobTypes().remove(jobType);
            userRepository.save(currentUser);
        } catch (FirebaseMessagingException e){
            log.error(String.format("Can't unsubscribe to the topic %d : %s", id, e.getMessage()));
        }
    }

    public void subscribeUserToAllTopics(User user){
        user.getCompany().getJobTypes().forEach(jobType -> {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(user.getDevices(), jobType.getId().toString());
            } catch (FirebaseMessagingException e) {
                log.error(String.format("Can't subscribe to the topic %d : %s", jobType.getId(), e.getMessage()));
            }
        });
    }

    @Async
    public void subscribeAllUsersToNewTopic(Long id){
        JobType jobType = jobTypeRepository.findById(id).get();
        jobType.getCompany().getUsers().stream()
            .filter(usr -> usr.getAuthorities().stream().noneMatch(authority -> authority.getName().equals(AuthoritiesConstants.ADMIN)))
            .forEach(usr -> {
                usr.setJobTypes(Streams.concat(usr.getJobTypes().stream(), Stream.of(jobType)).collect(Collectors.toList()));
                if(!usr.getDevices().isEmpty()){
                    try {
                        FirebaseMessaging.getInstance().subscribeToTopic(usr.getDevices(), jobType.getId().toString());
                    } catch (FirebaseMessagingException e) {
                        log.error(String.format("Can't subscribe to the topic %d : %s", jobType.getId(), e.getMessage()));
                    }
                }
            });
    }

    @Async
    public void unsubscribeAllUsersDeletedTopic(JobType jobType){
        jobType.getCompany().getUsers().forEach(user -> {
            if(user.getJobTypes().contains(jobType) && !user.getDevices().isEmpty()){
                try {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getDevices(), jobType.getId().toString());
                } catch (FirebaseMessagingException e) {
                    log.error(String.format("Can't unsubscribe to the topic %d : %s", jobType.getId(), e.getMessage()));
                }
            }
        });
    }
}
