package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.JobTypeService;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.view.JobTypeView;
import io.github.jhipster.web.util.PaginationUtil;
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
@RequestMapping("api/job-type")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") || hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class JobTypeController {

    private final JobTypeRepository repository;
    private final JobTypeMapper mapper;
    private final JobTypeService service;

    public JobTypeController(JobTypeRepository repository, JobTypeMapper mapper, JobTypeService service) {
        this.repository = repository;
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping("/{id}")
    public JobTypeView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<JobTypeView>> getAllByUserPaginated(Pageable pageable, @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page page = service.getAllJobTypeByUserPaginated(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public List<JobTypeView> getAllByUser(){
        return service.getAllJobTypeByUser();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<? extends JobTypeView> getAll(){
        return mapper.asListDTO(repository.findAll());
    }

    @PostMapping
    public JobTypeView create(@Valid @RequestBody JobTypeDTO jobType){
        return service.createJobType(jobType);
    }

    @PutMapping
    public JobTypeView edit(@Valid @RequestBody JobTypeDTO jobType){
        return service.editJobType(jobType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteJobType(id);
    }

}
