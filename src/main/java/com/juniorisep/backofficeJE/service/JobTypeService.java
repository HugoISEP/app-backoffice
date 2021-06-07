package com.juniorisep.backofficeJE.service;

import com.juniorisep.backofficeJE.domain.Company;
import com.juniorisep.backofficeJE.repository.CompanyRepository;
import com.juniorisep.backofficeJE.security.AuthoritiesConstants;
import com.juniorisep.backofficeJE.service.dto.JobTypeDTO;
import com.juniorisep.backofficeJE.service.dto.UserDTO;
import com.juniorisep.backofficeJE.service.view.JobTypeView;
import com.juniorisep.backofficeJE.domain.JobType;
import com.juniorisep.backofficeJE.repository.JobTypeRepository;
import com.juniorisep.backofficeJE.service.mapper.JobTypeMapper;
import com.juniorisep.backofficeJE.web.rest.errors.BadRequestAlertException;
import com.juniorisep.backofficeJE.web.rest.errors.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
@RequiredArgsConstructor
public class JobTypeService {
    private static final String ENTITY_NAME = "jobType";

    private final JobTypeRepository repository;
    private final JobTypeMapper mapper;
    private final UserService userService;
    private final DeviceService deviceService;
    private final CompanyRepository companyRepository;
    private final PositionService positionService;
    private final CacheManager cacheManager;

    public void hasAuthorization(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User not found", ENTITY_NAME, "id doesn't exist"));
        JobType jobType = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found", ENTITY_NAME, "id doesn't exist"));
        if(!user.getAuthorities().contains(AuthoritiesConstants.ADMIN) && !user.getCompany().getId().equals(jobType.getCompany().getId())){
            throw new AccessDeniedException("user not authorize");
        }
    }

    public JobTypeDTO getById(Long id){
        hasAuthorization(id);
        return mapper.asDTO(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("position doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    public Page<JobTypeView> getAllJobTypeByCompanyPaginated(Pageable pageable, String searchTerm){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User not found", ENTITY_NAME, "id doesn't exist"));

        return repository.findAllByCompanyIdPaginated(user.getCompany().getId(), searchTerm, pageable);
    }

    public List<JobTypeView> getAllJobTypeByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User not found", ENTITY_NAME, "id doesn't exist"));
        return repository.findAllByCompany_IdAndDeletedAtIsNull(user.getCompany().getId());
    }

    public JobTypeDTO createJobType(JobTypeDTO jobTypeDTO){
        JobType newJobType = mapper.fromDTO(jobTypeDTO);
        if (newJobType.getId() != null) {
            throw new BadRequestAlertException("A new JobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User not found", ENTITY_NAME, "id doesn't exist"));
        Company company = companyRepository.findById(user.getCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("Company not found", ENTITY_NAME, "id doesn't exist"));
        newJobType.setCompany(company);
        this.clearJobTypeCacheByCompany(newJobType.getCompany());
        //We need to get its id
        JobType jobTypeSaved = repository.save(newJobType);
        deviceService.subscribeAllUsersToNewTopic(jobTypeSaved.getId());
        return mapper.asDTO(jobTypeSaved);
    }

    public JobTypeDTO editJobType(JobTypeDTO updatedJobType){
        if (updatedJobType.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        hasAuthorization(updatedJobType.getId());

        JobType jobType = repository.findById(updatedJobType.getId()).orElseThrow(() -> new ResourceNotFoundException("jopType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateJobtype(mapper.fromDTO(updatedJobType), jobType);
        positionService.clearPositionCacheByCompany(jobType.getCompany());
        this.clearJobTypeCacheByCompany(jobType.getCompany());
        return mapper.asDTO(repository.save(jobType));
    }

    public void deleteJobType(Long id){
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        hasAuthorization(id);
        deviceService.unsubscribeAllUsersDeletedTopic(jobTypeToDelete);
        jobTypeToDelete.setDeletedAt(LocalDateTime.now());
        jobTypeToDelete.getPositions().forEach(position -> position.setDeletedAt(LocalDateTime.now()));
        positionService.clearPositionCacheByCompany(jobTypeToDelete.getCompany());
        this.clearJobTypeCacheByCompany(jobTypeToDelete.getCompany());
        repository.save(jobTypeToDelete);
    }

    public void clearJobTypeCacheByCompany(Company company) {
        Objects.requireNonNull(cacheManager.getCache(JobTypeRepository.JOB_TYPE_FROM_COMPANY_IN_CACHE)).evict(company.getId());
    }

}
