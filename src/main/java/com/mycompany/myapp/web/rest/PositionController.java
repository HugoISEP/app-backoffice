package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.PositionService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/position")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
public class PositionController {

    private static final String ENTITY_NAME = "position";

    private final PositionRepository repository;
    private final PositionService service;

    public PositionController(PositionRepository repository, PositionService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.mission.user.login ")
    public Position getById(@PathVariable Long id){
        return repository.findById(id).orElseThrow(() -> new BadRequestAlertException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
    }

    @GetMapping("/mission/{id}")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") " +
        "|| returnObject.size() != 0 ? returnObject.get(0).mission.user.login == principal.username : true  ")
    public List<Position> getPositionsByMission(@PathVariable Long id){
        return repository.findAllByMissionId(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<Position> getAll(){
        return repository.findAll();
    }

    @PostMapping("/mission/{missionId}")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public Mission addPosition(@PathVariable Long missionId, @Valid @RequestBody Position position){
        return service.addPosition(missionId, position);
    }

    @PutMapping()
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.mission.user.login ")
    public Position edit(@Valid @RequestBody Position position){
        return repository.save(service.editPosition(position));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deletePosition(id);
    }
}
