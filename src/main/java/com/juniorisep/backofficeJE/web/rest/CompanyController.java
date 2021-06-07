package com.juniorisep.backofficeJE.web.rest;

import com.juniorisep.backofficeJE.repository.CompanyRepository;
import com.juniorisep.backofficeJE.security.AuthoritiesConstants;
import com.juniorisep.backofficeJE.service.mapper.CompanyMapper;
import com.juniorisep.backofficeJE.service.view.BasicCompanyView;
import com.juniorisep.backofficeJE.service.view.CompanyDetailsView;
import com.juniorisep.backofficeJE.service.view.CompanyView;
import com.juniorisep.backofficeJE.service.CompanyService;
import com.juniorisep.backofficeJE.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CompanyController {

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository repository;
    private final CompanyService service;
    private final CompanyMapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends BasicCompanyView> getAllCompanies(){
        return mapper.asListDTO(repository.findAll());
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public CompanyDetailsView getCurrentUserCompany(){
        return service.getCompanyFromCurrentUser();
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
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public CompanyView editCompany(@RequestParam("company") String companyJson, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, MinioException {
        return service.edit(companyJson, file);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void deleteCompany(@PathVariable Long id){
        try {
            service.delete(id);
        } catch (Exception e) {
            throw new BadRequestAlertException("Could not delete company", ENTITY_NAME, e.toString());
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
