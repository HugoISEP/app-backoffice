package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.MissionService;
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
    private final PositionRepository positionRepository;
    private final MissionService service;

    public MissionController(MissionRepository repository, PositionRepository positionRepository, MissionService service) {
        this.repository = repository;
        this.positionRepository = positionRepository;
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
