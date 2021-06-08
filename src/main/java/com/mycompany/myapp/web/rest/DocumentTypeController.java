package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DocumentTypeRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.DocumentTypeDto;
import com.mycompany.myapp.service.view.DocumentTypeView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/document-types")
@RequiredArgsConstructor

public class DocumentTypeController {

    private final DocumentTypeRepository repository;


    @GetMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public List<DocumentTypeView> getAll() {
        return repository.findAllBy();
    }


}
