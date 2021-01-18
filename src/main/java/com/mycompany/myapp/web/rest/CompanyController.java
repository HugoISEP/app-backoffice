package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.CompanyService;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.view.CompanyDetailsView;
import com.mycompany.myapp.service.view.CompanyView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
import io.minio.errors.MinioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/company")
public class CompanyController {

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository repository;
    private final CompanyService service;
    private final CompanyMapper mapper;

    public CompanyController(CompanyRepository repository, CompanyService service, CompanyMapper mapper) {
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\") || hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public CompanyDetailsView getCurrentUserCompany(){
        return service.getCompanyFromCurrentUser();
    }

    @GetMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public List<? extends CompanyView> getAllCompanies(){
        return mapper.asListDTO(repository.findAll());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<CompanyDetailsView>> getAllCompaniesPaginated(Pageable pageable, @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page page = service.getAllPaginated(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public CompanyView getCompanyById(@PathVariable("id") Long id){
        return mapper.asDTO(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    @PostMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public CompanyView createCompany(@RequestParam("company") String companyJson, @RequestParam("file") MultipartFile file) throws Exception {
        return service.create(companyJson, file);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public CompanyView editCompany(@RequestParam("company") String companyJson, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, MinioException {
        return service.edit(companyJson, file);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void deleteCompany(@PathVariable Long id){
        try {
            service.delete(id);
        } catch (Exception e) {
            throw new BadRequestAlertException("Could not delete the file", ENTITY_NAME, e.toString());
        }
    }

    @GetMapping("/{id}/file")
    public String getFileUrl(@PathVariable Long id) {
        try {
            return service.getFileUrl(id);
        } catch (Exception e) {
            throw new BadRequestAlertException("Could not delete the file", ENTITY_NAME, e.toString());
        }
    }

}
