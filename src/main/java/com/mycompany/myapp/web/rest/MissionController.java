package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.MissionService;
import com.mycompany.myapp.service.dto.MissionDTO;
import com.mycompany.myapp.service.mapper.MissionMapper;
import com.mycompany.myapp.service.view.MissionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("api/mission")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
public class MissionController {

    private static final String ENTITY_NAME = "mission";

    private final MissionRepository repository;
    private final MissionMapper mapper;
    private final MissionService service;

    public MissionController(MissionRepository repository, MissionMapper mapper, PositionRepository positionRepository, MissionService service) {
        this.repository = repository;
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping("/{id}")
    //@PostAuthorize("hasAuthority(\"" +AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public MissionView getById(@PathVariable Long id){
        return mapper.asDTO(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    @GetMapping
    public ResponseEntity<List<MissionView>> getAllByCompany(Pageable pageable){
        Page<MissionView> page = service.getAllMissionByCompany(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends MissionView> getAll(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping
    public MissionView create(@Valid @RequestBody MissionDTO mission){
        return mapper.asDTO(service.createMission(mission));
    }

    @PutMapping
    //@PostAuthorize("hasAuthority(\"" +AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public MissionView edit(@Valid @RequestBody MissionDTO mission){
        return mapper.asDTO(service.editMission(mission));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
       service.deleteMission(id);
    }
}
