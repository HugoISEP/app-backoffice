package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.PositionService;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.view.PositionView;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Position", description = "Endpoints for Position resource. Positions are entities which will be visible in the application.")
@RestController
@RequestMapping(value = "api/position", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
@RequiredArgsConstructor
public class PositionController {

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PositionService service;

    @Operation(summary = "Get position by id", description = "")
    @GetMapping("/{id}")
    public PositionView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @Operation(summary = "Get all positions for a mission", description = "Get all positions resources for a given mission, identified by his id property.")
    @GetMapping("/mission/{id}")
    public List<PositionView> getPositionsByMission(@PathVariable Long id){
        return service.getByMissionId(id);
    }

    @Operation(summary = "Get all active positions", description = "Get all active positions for active user's company. This corresponds to positions displayed in mobile app.")
    @GetMapping("/active")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<List<PositionView>> getActivePositionsByUser(@PageableDefault(size = 50) Pageable pageable,
                                                                       @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page<PositionView> page = service.getActivePositionsByUser(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @Operation(summary = "Create position", description = "Create a position for a given mission.")
    @PostMapping("/mission/{missionId}")
    public PositionView addPosition(@PathVariable Long missionId, @RequestBody PositionDTO position){
        return service.addPosition(missionId, position);
    }

    @Operation(summary = "Update position", description = "Update a position by id property.")
    @PutMapping
    public PositionView edit(@Valid @RequestBody PositionDTO position){
        return service.editPosition(position);
    }

    @Operation(summary = "Trigger push notification", description = "Allows to trigger a push notification. You cannot send notification for a same position within 2 hours.")
    @PostMapping("/{id}/notification")
    public ResponseEntity<String> sendNotification(@PathVariable("id") Long id) {
        try {
            service.sendNotification(id);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(applicationName,  "Notification envoyée", id.toString())).build();
        } catch (Exception e){
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(applicationName, false, "position", "404", e.getMessage()))
                .body(e.getMessage());
        }
    }

    @Operation(summary = "Delete position", description = "Delete a position.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Position position){
        service.deletePosition(position);
    }
}
