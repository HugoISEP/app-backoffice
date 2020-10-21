package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Technology;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.repository.TechnologyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/mission")
public class MissionController {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final String ENTITY_NAME = "mission";

    private final MissionRepository repository;
    private final JobTypeRepository jobTypeRepository;
    private final TechnologyRepository technologyRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public MissionController(MissionRepository repository, JobTypeRepository jobTypeRepository, TechnologyRepository technologyRepository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.jobTypeRepository = jobTypeRepository;
        this.technologyRepository = technologyRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority(\"" +AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public Mission getById(@Valid @PathVariable Long id){
        return repository.findById(id).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
    }


    @GetMapping
    public List<Mission> getAllByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
        return repository.findAllByUserId(user.getId());
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
        if (mission.getId() != null) {
            throw new BadRequestAlertException("A new mission cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
        mission.setUser(userMapper.userDTOToUser(user));
        return repository.save(mission);
    }

    @PostMapping("/{id}/job-type")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public Mission addJobType(@PathVariable Long id, @Valid @RequestBody JobType jobType){
        if (jobType.getId() != null) {
            throw new BadRequestAlertException("A new jobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        Mission mission = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        Technology technology = technologyRepository.findById(jobType.getTechnology().getId()).orElseThrow(() -> new BadRequestAlertException("technology doesn't exist", ENTITY_NAME, "id doesn't exist"));
        jobType.setTechnology(technology);
        jobType.setMission(mission);
        technology.getJobTypes().add(jobType);
        mission.getJobTypes().add(jobType);

        return repository.save(mission);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || #mission.user.login == principal.username ")
    public Mission edit(@Valid @RequestBody Mission mission){
        if (mission.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        Mission oldMission = repository.findById(mission.getId()).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        oldMission.setName(mission.getName());
        return repository.save(oldMission);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new MissionController.AccountResourceException("User could not be found"));
        Mission missionToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Technologie doesn't exist", ENTITY_NAME, "id doesn't exist"));

        if(missionToDelete.getUser().getLogin() == user.getLogin() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            repository.delete(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Cannot delete ", ENTITY_NAME, " id doesn't exist")));
        } else {
            new MissionController.AccountResourceException("Access forbidden");
        }
    }
}
