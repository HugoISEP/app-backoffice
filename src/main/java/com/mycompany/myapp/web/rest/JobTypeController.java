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
@RequestMapping("api/job-type")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
public class JobTypeController {

    private static final String ENTITY_NAME = "jobType";

    private final JobTypeRepository repository;
    private final JobTypeMapper mapper;
    private final JobTypeService service;

    public JobTypeController(JobTypeRepository repository, JobTypeMapper mapper, JobTypeService service) {
        this.repository = repository;
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping("/{id}")
    //@PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
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
    //@PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal. == returnObject.user.login ")
    public JobTypeView edit(@Valid @RequestBody JobTypeDTO jobType){
        return service.editJobType(jobType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteJobType(id);
    }
}
