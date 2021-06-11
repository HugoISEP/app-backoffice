package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.PositionService;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.dto.PositionWithUserDTO;
import com.mycompany.myapp.service.view.PositionView;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/position")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
@RequiredArgsConstructor
public class PositionController {

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PositionService service;

    @GetMapping("/{id}")
    public PositionView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping("/full/{id}")
    public PositionWithUserDTO getFullById(@PathVariable Long id){
        return service.getWithUserById(id);
    }



    @GetMapping("/mission/{id}")
    public List<PositionView> getPositionsByMission(@PathVariable Long id){
        return service.getByMissionId(id);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<List<PositionView>> getActivePositionsByUser(@PageableDefault(size = 50) Pageable pageable,
                                                                       @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page<PositionView> page = service.getActivePositionsByUser(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Page<PositionView>> getPositionsByUser(@PageableDefault(size = 50) Pageable pageable,
                                                                       @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page<PositionView> page = service.getPositionsByUser(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }


    @PostMapping("/mission/{missionId}")
    public PositionView addPosition(@PathVariable Long missionId, @RequestBody PositionWithUserDTO position){
        return service.addPosition(missionId, position);
    }

    @PutMapping
    public PositionView edit(@Valid @RequestBody PositionWithUserDTO position){
        return service.editPosition(position);
    }

    @PutMapping("/{positionId}/bind/{userId}")
    public PositionView bindUser(@PathVariable("positionId") Long positionId, @PathVariable("userId") Long userId) {
        return service.bindUserToPosition(userId, positionId);
    }

    @PostMapping("/{id}/notification")
    public ResponseEntity<Void> sendNotification(@PathVariable("id") Long id) {
        try {
            service.sendNotification(id);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(applicationName,  "Notification envoyée", id.toString())).build();
        } catch (Exception e){
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(applicationName, false, "position", "404", e.getMessage())).build();
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        service.deletePosition(id);
    }
}
