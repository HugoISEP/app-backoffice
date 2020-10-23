package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Technology;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.repository.TechnologyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.MissionService;
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
@RequestMapping("/mission")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
public class MissionController {

    private static final String ENTITY_NAME = "mission";

    private final MissionRepository repository;
    private final JobTypeRepository jobTypeRepository;
    private final MissionService service;

    public MissionController(MissionRepository repository, JobTypeRepository jobTypeRepository, MissionService service) {
        this.repository = repository;
        this.jobTypeRepository = jobTypeRepository;
        this.service = service;
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority(\"" +AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public Mission getById(@Valid @PathVariable Long id){
        return repository.findById(id).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
    }

    @GetMapping
    public List<Mission> getAllByUser(){
        return service.getAllMissionByUser();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<Mission> getAll(){
        return repository.findAll();
    }

    @GetMapping("/{id}/job-type")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") " +
        "|| returnObject.size() != 0 ? returnObject.get(0).mission.user.login == principal.username : true  ")
    public List<JobType> getJobTypes(@PathVariable Long id){
        return jobTypeRepository.findAllByMission_Id(id);
    }

    @PostMapping
    public Mission create(@Valid @RequestBody Mission mission){
        return service.createMission(mission);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || #mission.user.login == principal.username ")
    public Mission edit(@Valid @RequestBody Mission mission){
        return service.editMission(mission);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
       service.deleteMission(id);
    }
}
