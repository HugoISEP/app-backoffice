package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional
public class JobTypeService {
    private static final String ENTITY_NAME = "jobType";

    private final JobTypeRepository repository;
    private final JobTypeMapper mapper;
    private final UserService userService;
    private final MobileService mobileService;
    private final CompanyRepository companyRepository;
    private final PositionService positionService;
    private final CacheManager cacheManager;


    public JobTypeService(JobTypeRepository repository, JobTypeMapper mapper, UserService userService, MobileService mobileService, CompanyRepository companyRepository, PositionService positionService, CacheManager cacheManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
        this.mobileService = mobileService;
        this.companyRepository = companyRepository;
        this.positionService = positionService;
        this.cacheManager = cacheManager;
    }

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

    public Page<JobTypeView> getAllJobTypeByUserPaginated(Pageable pageable, String searchTerm){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User not found", ENTITY_NAME, "id doesn't exist"));

        return repository.findAllByCompanyIdPaginated(user.getCompany().getId(), searchTerm, pageable);
    }

    public List<JobTypeView> getAllJobTypeByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User not found", ENTITY_NAME, "id doesn't exist"));
        return repository.findAllByCompany_Id(user.getCompany().getId());
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
        mobileService.subscribeAllUsersToNewTopic(jobTypeSaved.getId());
        return mapper.asDTO(jobTypeSaved);
    }

    public JobTypeDTO editJobType(JobTypeDTO updatedJobType){
        if (updatedJobType.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        hasAuthorization(updatedJobType.getId());

        JobType jobType = repository.findById(updatedJobType.getId()).orElseThrow(() -> new BadRequestAlertException("jopType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateJobtype(mapper.fromDTO(updatedJobType), jobType);
        positionService.clearPositionCacheByCompany(jobType.getCompany());
        this.clearJobTypeCacheByCompany(jobType.getCompany());
        return mapper.asDTO(repository.save(jobType));
    }

    public void deleteJobType(Long id){
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        hasAuthorization(id);
        //Remove user's Notifications
        jobTypeToDelete.getCompany().getUsers().forEach(user ->
            user.setJobTypes(user.getJobTypes().stream().filter(jobType -> !jobType.getId().equals(jobTypeToDelete.getId())).collect(Collectors.toList())));
        jobTypeToDelete.setDeletedAt(LocalDateTime.now());
        jobTypeToDelete.getPositions().forEach(position -> position.setDeletedAt(LocalDateTime.now()));
        positionService.clearPositionCacheByCompany(jobTypeToDelete.getCompany());
        this.clearJobTypeCacheByCompany(jobTypeToDelete.getCompany());
        mobileService.unsubscribeAllUsersDeletedTopic(jobTypeToDelete);
        repository.save(jobTypeToDelete);
    }

    public void clearJobTypeCacheByCompany(Company company) {
        Objects.requireNonNull(cacheManager.getCache(JobTypeRepository.JOB_TYPE_FROM_COMPANY_IN_CACHE)).evict(company.getId());
    }

}
