package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.config.annotations.AdminSecured;
import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.CompanyService;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.view.BasicCompanyView;
import com.mycompany.myapp.service.view.CompanyDetailsView;
import com.mycompany.myapp.service.view.CompanyView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
import io.minio.errors.MinioException;
import io.swagger.v3.oas.annotations.Hidden;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Tag(name = "Job Type", description = "Endpoints for Job Type resource")
@RestController
@RequestMapping(value = "api/company", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CompanyController {

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository repository;
    private final CompanyService service;
    private final CompanyMapper mapper;

    @GetMapping
    @AdminSecured
    public List<? extends BasicCompanyView> getAllCompanies(){
        return mapper.asListDTO(repository.findAll());
    }

    @GetMapping("/user")
    @Operation(summary = "Get current company", description = "Retrieve own complete company resource")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public CompanyDetailsView getCurrentUserCompany(){
        return service.getCompanyFromCurrentUser();
    }

    @GetMapping("/all")
    @AdminSecured
    public ResponseEntity<List<CompanyDetailsView>> getAllCompaniesPaginated(Pageable pageable, @RequestParam Optional<String> searchTerm){
        Page page = service.getAllPaginated(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @AdminSecured
    public CompanyView getCompanyById(@PathVariable("id") Long id){
        return mapper.asDTO(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    @PostMapping
    @AdminSecured
    public CompanyView createCompany(@RequestParam("company") String companyJson, @RequestParam("file") MultipartFile file) throws Exception {
        return service.create(companyJson, file);
    }

    @Operation(summary = "Edit company")
    @PutMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public CompanyView editCompany(@RequestParam("company") String companyJson, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, MinioException {
        return service.edit(companyJson, file);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @AdminSecured
    public void deleteCompany(@PathVariable Long id){
        try {
            service.delete(id);
        } catch (Exception e) {
            throw new BadRequestAlertException("Could not delete company", ENTITY_NAME, e.toString());
        }
    }

    @GetMapping("/{id}/file")
    @Hidden
    public String getFileUrl(@PathVariable Long id) {
        try {
            return service.getFileUrl(id);
        } catch (Exception e) {
            throw new BadRequestAlertException("Could not delete the file", ENTITY_NAME, e.toString());
        }
    }

}
