package com.juniorisep.backofficeJE.web.rest;

import com.juniorisep.backofficeJE.domain.Mission;
import com.juniorisep.backofficeJE.security.AuthoritiesConstants;
import com.juniorisep.backofficeJE.service.MissionService;
import com.juniorisep.backofficeJE.service.dto.MissionDTO;
import com.juniorisep.backofficeJE.service.mapper.MissionMapper;
import com.juniorisep.backofficeJE.service.view.MissionView;
import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("api/mission")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService service;
    private final MissionMapper mapper;

    @GetMapping("/{id}")
    public MissionView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<MissionDTO>> getAllByUserCompany(Pageable pageable, @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page<Mission> page = service.getAllMissionByCompany(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(mapper.asListDTO(page.getContent()), headers, HttpStatus.OK);
    }

    @PostMapping
    public MissionView create(@Valid @RequestBody MissionDTO mission){
        return service.createMission(mission);
    }

    @PutMapping
    public MissionView edit(@Valid @RequestBody MissionDTO mission){
        return service.editMission(mission);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        service.deleteMission(id);
    }
}
