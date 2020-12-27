package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Transactional
public class JobTypeService {
    private static final String ENTITY_NAME = "mission";
    private static final String IMAGES_PATH = "/images/jobType";

    private final JobTypeRepository repository;
    private final JobTypeMapper mapper;
    private final PositionRepository positionRepository;
    private final UserService userService;
    private final CompanyMapper companyMapper;

    public JobTypeService(JobTypeRepository repository, JobTypeMapper mapper, PositionRepository positionRepository, UserService userService, CompanyMapper companyMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.positionRepository = positionRepository;
        this.userService = userService;
        this.companyMapper = companyMapper;
    }

    public void hasAuthorization(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        JobType jobType = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "id doesn't exist"));
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
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));

        return repository.findAllByCompanyIdPaginated(user.getCompany().getId(), searchTerm, pageable);
    }

    public List<JobTypeView> getAllJobTypeByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        return repository.findAllByCompany_Id(user.getCompany().getId());
    }

    public JobTypeDTO createJobType(JobTypeDTO jobTypeDTO){
        JobType newJobType = mapper.fromDTO(jobTypeDTO);
        if (newJobType.getId() != null) {
            throw new BadRequestAlertException("A new JobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        newJobType.setCompany(companyMapper.fromDTO(user.getCompany()));
        return mapper.asDTO(repository.save(newJobType));
    }

    public JobTypeDTO editJobType(JobTypeDTO updatedJobType){
        if (updatedJobType.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        hasAuthorization(updatedJobType.getId());

        JobType jobType = repository.findById(updatedJobType.getId()).orElseThrow(() -> new BadRequestAlertException("jopType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateJobtype(mapper.fromDTO(updatedJobType), jobType);
        return mapper.asDTO(repository.save(jobType));
    }

    public void deleteJobType(Long id){
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        hasAuthorization(id);
        repository.delete(jobTypeToDelete);
    }

    public List<String> getImages() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(System.getProperty("user.dir") + IMAGES_PATH))) {
            return paths
                .filter(Files::isRegularFile)
                .map(Path::toString).collect(Collectors.toList());
        }
    }

}
