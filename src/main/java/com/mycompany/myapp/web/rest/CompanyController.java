package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.CompanyService;
import com.mycompany.myapp.service.dto.CompanyDTO;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.view.CompanyDetailsView;
import com.mycompany.myapp.service.view.CompanyView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public CompanyView createCompany(@Valid @RequestBody CompanyDTO company){
        return service.create(company);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    public CompanyView editCompany(@Valid @RequestBody CompanyDTO updatedCompany){
        service.hasAuthorization(updatedCompany.getId());
        return service.edit(updatedCompany);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void deleteCompany(@PathVariable Long id){
        service.delete(id);
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            service.hasAuthorization(id);
            service.storeInFileSystem(file, id);
        } catch (Exception e) {
            throw new BadRequestAlertException("Could not upload the file ", ENTITY_NAME, file.getName());
        }
    }

    @GetMapping("/{id}/image")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") || hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable("id") Long companyId) {
        try {
            Path path = service.getFile(companyId);
            byte[] data = Files.readAllBytes(path);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                // Content-Lengh
                .contentLength(data.length)
                .body(resource);

        } catch (Exception e) {
            throw new BadRequestAlertException("can't get image ", "IMAGE", " image not found");
        }
    }


}
