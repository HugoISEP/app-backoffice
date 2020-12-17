package com.mycompany.myapp.web.rest;

import com.google.common.collect.Iterables;
import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.PositionService;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.mapper.PositionMapper;
import com.mycompany.myapp.service.view.MissionView;
import com.mycompany.myapp.service.view.PositionView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/position")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") || hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class PositionController {


    private final PositionRepository repository;
    private final PositionService service;
    private final PositionMapper mapper;

    public PositionController(PositionRepository repository, PositionService service, PositionMapper mapper) {
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public PositionView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping("/mission/{id}")
    public List<PositionView> getPositionsByMission(@PathVariable Long id){
        return service.getByMissionId(id);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\") || hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public List<PositionView> getActivePositionsByUser(){
        return service.getActivePositionsByUser();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends PositionView> getAll(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping("/mission/{missionId}")
    public MissionView addPosition(@PathVariable Long missionId, @Valid @RequestBody PositionDTO position){
        return service.addPosition(missionId, position);
    }

    @PutMapping()
    public PositionView edit(@Valid @RequestBody PositionDTO position){
        return service.editPosition(position);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Position position){
        service.deletePosition(position);
    }
}
