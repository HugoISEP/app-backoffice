package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.MissionService;
import com.mycompany.myapp.service.dto.MissionDTO;
import com.mycompany.myapp.service.view.MissionView;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;


@RestController
@Tag(name = "Mission", description = "Endpoints for Mission resource")
@RequestMapping(value = "api/mission", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
public class MissionController {

    private final MissionService service;

    public MissionController(MissionService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public MissionView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<MissionView>> getAllByUserCompany(Pageable pageable, @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page<MissionView> page = service.getAllMissionByCompany(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping
    public MissionView create(@Valid @RequestBody MissionDTO mission){
        return service.createMission(mission);
    }

    @PutMapping
    public MissionView edit(@Valid @RequestBody MissionDTO mission){
        return service.editMission(mission);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        service.deleteMission(id);
    }
}
