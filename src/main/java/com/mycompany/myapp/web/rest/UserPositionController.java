package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.UserPositionService;
import com.mycompany.myapp.service.dto.UserPositionDto;
import com.mycompany.myapp.service.view.UserPositionView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/user-position")
@RequiredArgsConstructor
public class UserPositionController {

    private final UserPositionService service;

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    @PutMapping("/{id}")
    public UserPositionDto update(@Valid @RequestBody UserPositionDto dto) {
        return service.update(dto);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    @GetMapping("/{id}")
    public UserPositionView getById(@PathVariable Long id) {
        return service.getViewById(id);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    @GetMapping("/user/{id}")
    public Page<UserPositionView> getByUser(Pageable page, @PathVariable Long id) {
        return service.getViewsPaginatedByUserId(page, id);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    @GetMapping("/position/{id}")
    public Page<UserPositionView> getByPosition(Pageable page, @PathVariable Long id) {
        return service.getViewsPaginatedByPositionId(page, id);
    }

    @GetMapping("/mission/{id}")
    public Page<UserPositionView> getByMission(Pageable page, @PathVariable Long id) {
        return service.getViewsPaginatedByMissionId(page, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }


}
