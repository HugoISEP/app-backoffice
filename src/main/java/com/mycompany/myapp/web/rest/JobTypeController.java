package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/job-type")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
public class JobTypeController {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final String ENTITY_NAME = "jobType";

    private final JobTypeRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;

    public JobTypeController(JobTypeRepository repository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public JobType getById(@PathVariable Long id){
        return repository.findById(id).orElseThrow(() -> new BadRequestAlertException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
    }

    @GetMapping
    public List<JobType> getAllByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new JobTypeController.AccountResourceException("User could not be found"));
        return repository.findTechnologiesByUserId(user.getId());
    }


    @GetMapping("/all")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<JobType> getAll(){
        return repository.findAll();
    }

    @PostMapping
    public JobType create(@Valid @RequestBody JobType jobType){
        if (jobType.getId() != null) {
            throw new BadRequestAlertException("A new JobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new JobTypeController.AccountResourceException("User could not be found"));
        jobType.setUser(userMapper.userDTOToUser(user));
        return repository.save(jobType);
    }

    @PutMapping
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public JobType edit(@Valid @RequestBody JobType jobType){
        if (jobType.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        JobType oldJobType = repository.findById(jobType.getId()).orElseThrow(() -> new BadRequestAlertException("technology doesn't exist", ENTITY_NAME, "id doesn't exist"));
        oldJobType.setName(jobType.getName());
        return repository.save(jobType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new JobTypeController.AccountResourceException("User could not be found"));
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));

        if(jobTypeToDelete.getUser().getLogin() == user.getLogin() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            repository.delete(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Cannot delete ", ENTITY_NAME, " id doesn't exist")));
        } else {
            new JobTypeController.AccountResourceException("Access Forbidden");
        }

    }
}
