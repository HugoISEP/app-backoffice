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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mycompany.myapp.config.Constants.DEFAULT_LANGUAGE;

@Slf4j
@Service
@Transactional
public class DeviceService {

    private final JobTypeRepository jobTypeRepository;
    private final UserRepository userRepository;

    public DeviceService(JobTypeRepository jobTypeRepository, UserRepository userRepository) {
        this.jobTypeRepository = jobTypeRepository;
        this.userRepository = userRepository;
    }

    public void subscribeUserToATopic(User currentUser, Long id) {
        String userLangKey = Optional.ofNullable(currentUser.getLangKey()).orElse(DEFAULT_LANGUAGE);
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobTYpe not found", "JobType", "wrong id"));
        FirebaseMessaging.getInstance().subscribeToTopicAsync(currentUser.getDevices(), userLangKey + id.toString());
        currentUser.getJobTypes().add(jobType);
        userRepository.save(currentUser);
    }

    public void unsubscribeUserToATopic(User currentUser, Long id) {
        String userLangKey = Optional.ofNullable(currentUser.getLangKey()).orElse(DEFAULT_LANGUAGE);
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobType not found", "JobType", "wrong id"));
        FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(currentUser.getDevices(), userLangKey + jobType.getId().toString());
        currentUser.getJobTypes().remove(jobType);
        userRepository.save(currentUser);
    }

    public void subscribeUserToAllTopics(User user){
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(DEFAULT_LANGUAGE);
        user.getCompany().getJobTypes().forEach(jobType -> {
            FirebaseMessaging.getInstance().subscribeToTopicAsync(user.getDevices(), userLangKey + jobType.getId().toString());
        });
    }

    public void subscribeNewDeviceToTopics(User user, String newDeviceToken) {
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(DEFAULT_LANGUAGE);
        user.getJobTypes().forEach(jobType -> FirebaseMessaging.getInstance().subscribeToTopicAsync(Collections.singletonList(newDeviceToken), userLangKey + jobType.getId().toString()));
    }

    public void changeUserLanguageNotifications(User user, String newLanguage){
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(DEFAULT_LANGUAGE);
        user.getJobTypes().forEach(jobType -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(user.getDevices(), userLangKey + jobType.getId().toString());
            FirebaseMessaging.getInstance().subscribeToTopicAsync(user.getDevices(), newLanguage + jobType.getId().toString());
        });
    }

    @Async
    public void subscribeAllUsersToNewTopic(Long id){
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("jobType not found", "JobType", "wrong id"));

        Map<String, List<String>> usersDevicesByLanguage = jobType.getCompany().getUsers().stream()
            .filter(usr -> usr.getAuthorities().stream().noneMatch(authority -> authority.getName().equals(AuthoritiesConstants.ADMIN)))
            .peek(usr -> usr.setJobTypes(Streams.concat(usr.getJobTypes().stream(), Stream.of(jobType)).collect(Collectors.toList())))
            .collect(Collectors.toMap(usr -> Optional.ofNullable(usr.getLangKey()).orElse(DEFAULT_LANGUAGE), User::getDevices, (item, identicalItem) -> item));

        usersDevicesByLanguage.keySet().forEach(lang -> {
            FirebaseMessaging.getInstance().subscribeToTopicAsync(usersDevicesByLanguage.get(lang), lang + id.toString());
        });
    }

    @Async
    public void unsubscribeAllUsersDeletedTopic(JobType jobType){
        Map<String, List<String>> usersDevicesByLanguage = jobType.getCompany().getUsers().stream()
            .filter(usr -> usr.getJobTypes().contains(jobType))
            .peek(usr -> usr.getJobTypes().remove(jobType))
            .collect(Collectors.toMap(usr -> Optional.ofNullable(usr.getLangKey()).orElse(DEFAULT_LANGUAGE), User::getDevices, (item, identicalItem) -> item));

        usersDevicesByLanguage.keySet().forEach(lang -> FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(usersDevicesByLanguage.get(lang), lang + jobType.getId().toString()));
    }
}
