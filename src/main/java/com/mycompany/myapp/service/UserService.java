package com.mycompany.myapp.service;

import com.mycompany.myapp.config.Constants;
import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.AuthorityRepository;
import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.dto.UserDTO;

import com.mycompany.myapp.web.rest.errors.InvalidEmailSuffixException;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import io.github.jhipster.security.RandomUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Slf4j
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CompanyRepository companyRepository;

    private final JobTypeRepository jobTypeRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, CompanyRepository companyRepository, JobTypeRepository jobTypeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.companyRepository = companyRepository;
        this.jobTypeRepository = jobTypeRepository;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }

    public User registerUser(UserDTO userDTO, String password) throws Exception {
        userRepository.findOneByEmail(userDTO.getEmail().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getEmail().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            Company company = this.isEmailValid(userDTO.getEmail());
            newUser.setEmail(userDTO.getEmail().toLowerCase());
            newUser.setCompany(company);
            newUser.setJobTypes(jobTypeRepository.findAllByCompany(company));
        } else {
            throw new Exception("Email is required");
        }
        userDTO.setLogin(userDTO.getLogin());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.getActivated()) {
             return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    public User createUser(UserDTO userDTO) throws Exception{
        UserDTO currentUser = getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", "USER", "id exists"));

        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        Company company;
        if (userDTO.getEmail() != null) {
            if (!userDTO.getAuthorities().contains(AuthoritiesConstants.USER)) {
                company = companyRepository.findById(userDTO.getCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("Company not found", "company", "id doesn't exist"));
            } else {
                company = this.isEmailValid(userDTO.getEmail());
            }
            if (!currentUser.getAuthorities().contains(AuthoritiesConstants.ADMIN)) {
                if (!currentUser.getCompany().getId().equals(company.getId())) {
                    throw new InvalidEmailSuffixException("Email not match with the company");
                }
            }
            user.setEmail(userDTO.getEmail().toLowerCase());
            user.setCompany(company);
        } else {
            throw new Exception("Email is required");
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null && currentUser.getAuthorities().contains(AuthoritiesConstants.ADMIN)) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        } else {
            user.setAuthorities(Collections.singleton(authorityRepository.findAuthorityByName(AuthoritiesConstants.USER)));
        }
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    private Company isEmailValid(String emailToTest){
        return companyRepository.findCompanyByUserEmail(emailToTest).orElseThrow(() -> new InvalidEmailSuffixException("Email doesn't match with a company"));
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) throws Exception{
        UserDTO currentUser = getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", "USER", "id exists"));
        if (!currentUser.getAuthorities().contains(AuthoritiesConstants.ADMIN) && !currentUser.getCompany().getId().equals(userDTO.getCompany().getId())){
            throw new Exception("Not authorize to edit this user");
        }

        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                /*if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }*/
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                if (currentUser.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO.getAuthorities().stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                }
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(Long id) {
        UserDTO currentUser = getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("current user not found", "USER", "id exists"));

        User user = userRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "login doesn't exist"));
        if (currentUser.getAuthorities().contains(AuthoritiesConstants.ADMIN) || currentUser.getCompany().getId().equals(user.getCompany().getId())){
            userRepository.delete(user);
            log.debug("Deleted User: {}", user);
        }
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                log.debug("Changed Information for User: {}", user);
            });
    }


    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsersByManager(Pageable pageable, String searchTerm) {
        UserDTO user = this.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", "USER", "id exists"));
        return userRepository.findAllUsersByManager(pageable, user.getCompany().getId(), searchTerm).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public List<JobTypeDTO> updateNotificationPreferences(Long id, List<JobTypeDTO> jobTypes){
        User user = userRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("user not found ", "USER", " id exists"));
        user.getJobTypes().clear();
        List<JobType> newJobTypes = jobTypes.stream()
            .map(jobType -> jobTypeRepository.findById(jobType.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        user.setJobTypes(newJobTypes);
        return new UserDTO(user).getJobTypes();
    }

    public UserDTO getUser(String login){
        UserDTO currentUser = this.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", "USER", "id exists"));
        UserDTO user = getUserWithAuthoritiesByLogin(login).map(UserDTO::new).orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "login doesn't exist"));

        if(!currentUser.getAuthorities().contains(AuthoritiesConstants.ADMIN) && !currentUser.getCompany().getId().equals(user.getCompany().getId())){
            throw new AccessDeniedException("Not authorize");
        } else {
            return user;
        }
    }

    public List<User> getUsersByCompany(Company company) {
        return userRepository.findAllByCompany(company);
    }

}
