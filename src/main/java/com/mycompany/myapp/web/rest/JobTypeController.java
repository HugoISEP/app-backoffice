package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.aspectj.weaver.AjcMemberMaker;
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
    private final PositionRepository positionRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public JobTypeController(JobTypeRepository repository, JobTypeMapper mapper, PositionRepository positionRepository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.mapper = mapper;
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
    public List<? extends JobTypeView> getAllByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        return mapper.asListDTO(repository.findJobTypesByUser_Id(user.getId()));
    }

    @GetMapping("/all")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends JobTypeView> getAll(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping
    public JobTypeView create(@Valid @RequestBody JobTypeDTO jobType){
        JobType newJobType = mapper.fromDTO(jobType);
        if (newJobType.getId() != null) {
            throw new BadRequestAlertException("A new JobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        newJobType.setUser(userMapper.userDTOToUser(user));
        return mapper.asDTO(repository.save(newJobType));
    }

    @PutMapping
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public JobTypeView edit(@Valid @RequestBody JobTypeDTO jobType){
        if (jobType.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        JobType oldJobType = repository.findById(jobType.getId()).orElseThrow(() -> new BadRequestAlertException("technology doesn't exist", ENTITY_NAME, "id doesn't exist"));
        oldJobType.setName(jobType.getName());
        return mapper.asDTO(repository.save(oldJobType));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        if(jobTypeToDelete.getUser().getId() == user.getId() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            jobTypeToDelete.getPositions().forEach(position -> {
                //positionRepository.delete(position);
                positionRepository.delete(position.getId());
            });
            repository.delete(jobTypeToDelete);
        } else {
            throw new BadRequestAlertException("no permission to delete", ENTITY_NAME, "wrong user ");
        }

    }
}
