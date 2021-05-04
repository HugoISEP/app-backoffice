package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.JobTypeService;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Job Type", description = "Endpoints for Job Type resource")
@RestController
@RequestMapping(value = "api/job-type", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
@RequiredArgsConstructor
public class JobTypeController {

    private static final String ENTITY_NAME = "jobType";

    private final JobTypeService service;

    @Operation(summary = "Get job-type by id", description = "Retrieve Job-Type resource by his id property.")
    @GetMapping("/{id}")
    public JobTypeView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get all job-types paged", description = "Get all job-types of his own company with pagination.")
    public ResponseEntity<List<JobTypeView>> getAllByUserPaginated(Pageable pageable, @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page page = service.getAllJobTypeByUserPaginated(pageable, searchTerm);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/user")
    @Operation(summary = "Get all job-types", description = "Get all job-types of his own company.")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public List<JobTypeView> getAllByUser(){
        return service.getAllJobTypeByUser();
    }

    @PostMapping
    @Operation(summary = "Create job-type", description = "Create a job-type for own company. All users will be subscribed by default.")
    public JobTypeView create(@Valid @RequestBody JobTypeDTO jobType){
        try {
            return service.createJobType(jobType);
        } catch (Exception e){
            throw new BadRequestAlertException("data is not valid ", ENTITY_NAME, e.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "Update job-type", description = "Update name or icon of a job-type.")
    public JobTypeView edit(@Valid @RequestBody JobTypeDTO jobType){
        try {
            return service.editJobType(jobType);
        } catch (Exception e){
            throw new BadRequestAlertException("data is not valid ", ENTITY_NAME, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a job-type", description = "Delete a job-type by his id property. All associated missions and positions will be deleted.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteJobType(id);
    }

}
