package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.JobTypeService;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
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
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MANAGER + "\")")
@RequiredArgsConstructor
public class JobTypeController {

    private static final String ENTITY_NAME = "jobType";

    private final JobTypeService service;

    @GetMapping("/{id}")
    public JobTypeView getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<JobTypeView>> getAllByCompanyPaginated(Pageable pageable, @RequestParam(value = "searchTerm", defaultValue = "%%") String searchTerm){
        Page<JobTypeView> page = service.getAllJobTypeByCompanyPaginated(pageable, searchTerm);
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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteJobType(id);
    }

}
