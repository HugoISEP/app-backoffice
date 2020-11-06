package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/entreprise")
public class CompanyController {

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository repository;
    private final CompanyMapper mapper;

    public CompanyController(CompanyRepository repository, CompanyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @GetMapping("/all")
    public List<Company> getAllCompanies(){
        return repository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Company createCompany(@Valid @RequestBody Company company){
        return repository.save(company);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Company editCompany(@Valid @RequestBody Company updatedCompany){
        if (updatedCompany.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        Company company = repository.findById(updatedCompany.getId()).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateCompany(updatedCompany, company);
        return repository.save(company);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void deleteCompany(@PathVariable Long id){
        Company company = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        repository.delete(company);
    }

}
