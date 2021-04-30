package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.JobTypeService;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Job Type", description = "Endpoints for Job Type resource")
@RestController
@RequestMapping(value = "api/job-type", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
public class JobTypeController {

    private static final String ENTITY_NAME = "jobType";

    private final JobTypeService service;

    public JobTypeController(JobTypeService service) {
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
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public List<JobTypeView> getAllByUser(){
        return service.getAllJobTypeByUser();
    }

    @PostMapping
    public JobTypeView create(@Valid @RequestBody JobTypeDTO jobType){
        try {
            return service.createJobType(jobType);
        } catch (Exception e){
            throw new BadRequestAlertException("data is not valid ", ENTITY_NAME, e.getMessage());
        }
    }

    @PutMapping
    public JobTypeView edit(@Valid @RequestBody JobTypeDTO jobType){
        try {
            return service.editJobType(jobType);
        } catch (Exception e){
            throw new BadRequestAlertException("data is not valid ", ENTITY_NAME, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteJobType(id);
    }

}
