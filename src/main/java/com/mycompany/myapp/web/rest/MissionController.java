package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.MissionService;
import com.mycompany.myapp.service.dto.MissionDTO;
import com.mycompany.myapp.service.view.MissionView;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;


@RestController
@Tag(name = "Mission", description = "Endpoints for Mission resource")
@RequestMapping(value = "api/mission", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService service;

    @Operation(summary = "Get mission by id", description = "Retrieve Mission resource by his id property.")
    @GetMapping("/{id}")
    public MissionView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @Operation(summary = "Get all missions paged", description = "Get all own company's missions with pagination.")
    @GetMapping
    public ResponseEntity<List<MissionView>> getAllByUserCompany(Pageable pageable, @RequestParam Optional<String> searchTerm){
        Page<MissionView> page = service.getAllMissionByCompany(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @Operation(summary = "Create a new mission", description = "Create a new mission resource to user's own company.")
    @PostMapping
    public MissionView create(@Valid @RequestBody MissionDTO mission){
        return service.createMission(mission);
    }

    @PutMapping
    @Operation(summary = "Update mission", description = "Update mission's properties by his id property.")
    public MissionView edit(@Valid @RequestBody MissionDTO mission){
        return service.editMission(mission);
    }

    @Operation(summary = "Delete a mission", description = "Delete a mission by his id property. All associated positions will be deleted.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        service.deleteMission(id);
    }
}
