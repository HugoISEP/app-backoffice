package com.juniorisep.backofficeJE.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.juniorisep.backofficeJE.config.Constants;
import com.juniorisep.backofficeJE.domain.Company;
import com.juniorisep.backofficeJE.domain.JobType;
import com.juniorisep.backofficeJE.domain.User;
import com.juniorisep.backofficeJE.repository.UserRepository;
import com.juniorisep.backofficeJE.security.AuthoritiesConstants;
import com.juniorisep.backofficeJE.repository.JobTypeRepository;
import com.juniorisep.backofficeJE.web.rest.errors.BadRequestAlertException;
import com.juniorisep.backofficeJE.web.rest.errors.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeviceService {

    private final JobTypeRepository jobTypeRepository;
    @Lazy
    private final UserService userService;
    private final UserRepository userRepository;

    public void subscribeUserToATopic(Long id) {
        User user = userService.getUserWithAuthorities()
            .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER", "id doesn't exist"));
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE);
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobTYpe not found", "JobType", "wrong id"));
        FirebaseMessaging.getInstance().subscribeToTopicAsync(user.getDevices(), userLangKey + id.toString());
        if (!user.getJobTypes().contains(jobType)){
            user.getJobTypes().add(jobType);
            userRepository.save(user);
        }
    }

    public void unsubscribeUserFromATopic(Long id) {
        User user = userService.getUserWithAuthorities()
            .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER", "id doesn't exist"));
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE);
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobType not found", "JobType", "wrong id"));
        FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(user.getDevices(), userLangKey + jobType.getId().toString());
        if (user.getJobTypes().contains(jobType)){
            user.getJobTypes().remove(jobType);
            userRepository.save(user);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void subscribeUserToAllTopics(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found", "USER", "id doesn't exist"));
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE);
        if (!user.getDevices().isEmpty()){
            user.getCompany().getJobTypes().forEach(jobType -> {
                FirebaseMessaging.getInstance().subscribeToTopicAsync(user.getDevices(), userLangKey + jobType.getId().toString());
            });
        }
    }

    public void unsubscribeUserFromAllTopics(User user){
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE);
        if (!user.getDevices().isEmpty()) {
            user.getCompany().getJobTypes().forEach(jobType -> {
                FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(user.getDevices(), userLangKey + jobType.getId().toString());
            });
        }
    }

    public void subscribeNewDeviceToTopics(String newDeviceToken) {
        User user = userService.getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "id doesn't exist"));
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE);
        user.getJobTypes().forEach(jobType -> FirebaseMessaging.getInstance().subscribeToTopicAsync(Collections.singletonList(newDeviceToken), userLangKey + jobType.getId().toString()));
    }

    public void changeUserLanguageNotifications(User user, String newLanguage){
        String userLangKey = Optional.ofNullable(user.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE);
        user.getJobTypes().forEach(jobType -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(user.getDevices(), userLangKey + jobType.getId().toString());
            FirebaseMessaging.getInstance().subscribeToTopicAsync(user.getDevices(), newLanguage + jobType.getId().toString());
        });
        user.setLangKey(newLanguage);
    }

    public void unsubscribeAllCompanyUsersToNotifications(Company company){
        company.getJobTypes().forEach(jobType -> {
            Map<String, List<String>> usersDevicesByLanguage = company.getUsers().stream()
                .filter(u -> u.getJobTypes().contains(jobType))
                .collect(Collectors.toMap(usr -> Optional.ofNullable(usr.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE), User::getDevices, (item, identicalItem) -> item));
            usersDevicesByLanguage.keySet().stream().filter(lang -> !usersDevicesByLanguage.get(lang).isEmpty()).forEach(lang -> FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(usersDevicesByLanguage.get(lang), lang + jobType.getId().toString()));
        });
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void subscribeAllUsersToNewTopic(Long id){
        JobType jobType = jobTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("jobType not found", "JobType", "wrong id"));

        Map<String, List<String>> usersDevicesByLanguage = jobType.getCompany().getUsers().stream()
            .filter(usr -> usr.getAuthorities().stream().noneMatch(authority -> authority.getName().equals(AuthoritiesConstants.ADMIN)))
            .peek(usr -> usr.getJobTypes().add(jobType))
            .collect(Collectors.toMap(usr -> Optional.ofNullable(usr.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE), User::getDevices, (item, identicalItem) -> item));
        usersDevicesByLanguage.keySet().stream().filter(lang -> !usersDevicesByLanguage.get(lang).isEmpty()).forEach(lang -> {
            FirebaseMessaging.getInstance().subscribeToTopicAsync(usersDevicesByLanguage.get(lang), lang + id.toString());
        });
    }

    public void unsubscribeAllUsersDeletedTopic(JobType jobType){
        Map<String, List<String>> usersDevicesByLanguage = jobType.getCompany().getUsers().stream()
            .filter(usr -> usr.getJobTypes().contains(jobType))
            .peek(usr -> usr.getJobTypes().remove(jobType))
            .collect(Collectors.toMap(usr -> Optional.ofNullable(usr.getLangKey()).orElse(Constants.DEFAULT_LANGUAGE), User::getDevices, (item, identicalItem) -> item));
        usersDevicesByLanguage.keySet().stream().filter(lang -> !usersDevicesByLanguage.get(lang).isEmpty()).forEach(lang -> FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(usersDevicesByLanguage.get(lang), lang + jobType.getId().toString()));
    }
}
