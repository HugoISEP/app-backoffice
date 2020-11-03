package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.JobTypeService;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.service.view.JobTypeView;
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

    private static final String ENTITY_NAME = "jobType";

    private final JobTypeRepository repository;
    private final JobTypeMapper mapper;
    private final JobTypeService service;
    private final PositionRepository positionRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public JobTypeController(JobTypeRepository repository, JobTypeMapper mapper, JobTypeService service, PositionRepository positionRepository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.service = service;
        this.positionRepository = positionRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public JobTypeView getById(@PathVariable Long id){
        return mapper.asDTO(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    @GetMapping
    public List<JobTypeView> getAllByUser(){
        return service.getAllJobTypeByUser();
    }

    @GetMapping("/all")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends JobTypeView> getAll(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping
    public JobTypeView create(@Valid @RequestBody JobTypeDTO jobType){
        return service.createJobType(jobType);
    }

    @PutMapping
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public JobTypeView edit(@Valid @RequestBody JobTypeDTO jobType){
        return service.editJobType(jobType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteJobType(id);
    }
}
