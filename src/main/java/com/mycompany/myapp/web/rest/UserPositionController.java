package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.UserPositionService;
import com.mycompany.myapp.service.dto.UserPositionDto;
import com.mycompany.myapp.service.view.UserPositionView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/user-position")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
@RequiredArgsConstructor
public class UserPositionController {

    private final UserPositionService service;

    @PutMapping("/{id}")
    public UserPositionDto update(@Valid @RequestBody UserPositionDto dto) {
        return service.update(dto);
    }

    @GetMapping("/{id}")
    public UserPositionView getById(@PathVariable Long id) {
        return service.getViewById(id);
    }

}
