package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.CompanyService;
import com.mycompany.myapp.service.dto.CompanyDTO;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.view.CompanyView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final CompanyRepository repository;
    private final CompanyService service;
    private final CompanyMapper mapper;

    public CompanyController(CompanyRepository repository, CompanyService service, CompanyMapper mapper) {
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/all")
    public List<? extends CompanyView> getAllCompanies(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public CompanyView createCompany(@Valid @RequestBody CompanyDTO company){
        return service.create(company);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public CompanyView editCompany(@Valid @RequestBody CompanyDTO updatedCompany){
        return service.edit(updatedCompany);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void deleteCompany(@PathVariable Long id){
        service.delete(id);
    }

}
