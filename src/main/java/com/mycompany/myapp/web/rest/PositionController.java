package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.PositionService;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.mapper.PositionMapper;
import com.mycompany.myapp.service.view.PositionView;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") || hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class PositionController {

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

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
    public ResponseEntity<List<PositionView>> getActivePositionsByUser(Pageable pageable, @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page<PositionView> page = service.getActivePositionsByUser(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends PositionView> getAll(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping("/mission/{missionId}")
    public PositionView addPosition(@PathVariable Long missionId, @RequestBody PositionDTO position){
        return service.addPosition(missionId, position);
    }

    @PutMapping()
    public PositionView edit(@Valid @RequestBody PositionDTO position){
        return service.editPosition(position);
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
    public void delete(@PathVariable("id") Position position){
        service.deletePosition(position);
    }
}
