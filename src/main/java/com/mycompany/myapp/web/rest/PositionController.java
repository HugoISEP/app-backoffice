package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.PositionService;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.mapper.MissionMapper;
import com.mycompany.myapp.service.mapper.PositionMapper;
import com.mycompany.myapp.service.view.MissionView;
import com.mycompany.myapp.service.view.PositionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/position")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
public class PositionController {

    private static final String ENTITY_NAME = "position";

    private final PositionRepository repository;
    private final PositionService service;
    private final PositionMapper mapper;
    private final MissionMapper missionMapper;

    public PositionController(PositionRepository repository, PositionService service, PositionMapper mapper, MissionMapper missionMapper) {
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
        this.missionMapper = missionMapper;
    }

    @GetMapping("/{id}")
    //@PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.mission.user.login ")
    public PositionView getById(@PathVariable Long id){
        return mapper.asDto(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("position doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    @GetMapping("/mission/{id}")
    //@PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") " +
    //    "|| returnObject.size() != 0 ? returnObject.get(0).mission.user.login == principal.username : true  ")
    public List<PositionView> getPositionsByMission(@PathVariable Long id){
        return repository.findAllByMissionId(id);
    }

    @GetMapping("/active")
    //@PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") " +
    //    "|| returnObject.size() != 0 ? returnObject.get(0).mission.user.login == principal.username : true  ")
    public List<PositionView> getPositionsByMission(){
        return service.getActivePositionsByUser();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends PositionView> getAll(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping("/mission/{missionId}")
    //@PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public MissionView addPosition(@PathVariable Long missionId, @Valid @RequestBody PositionDTO position){
        return missionMapper.asDTO(service.addPosition(missionId, position));
    }

    @PutMapping()
    //@PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.mission.user.login")
    public PositionView edit(@Valid @RequestBody PositionDTO position){
        return mapper.asDto(service.editPosition(position));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deletePosition(id);
    }
}
