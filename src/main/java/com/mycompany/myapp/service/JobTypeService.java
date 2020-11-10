package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobTypeService {
    private static final String ENTITY_NAME = "mission";

    private final JobTypeRepository repository;
    private final JobTypeMapper mapper;
    private final PositionRepository positionRepository;
    private final UserService userService;

    public JobTypeService(JobTypeRepository repository, JobTypeMapper mapper, PositionRepository positionRepository, UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.positionRepository = positionRepository;
        this.userService = userService;
    }

    public Page<JobTypeView> getAllJobTypeByUser(Pageable pageable){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));

        return repository.findAllByCompanyId(user.getCompany().getId(), pageable);
    }

    public JobTypeDTO createJobType(JobTypeDTO jobTypeDTO){
        JobType newJobType = mapper.fromDTO(jobTypeDTO);
        if (newJobType.getId() != null) {
            throw new BadRequestAlertException("A new JobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        newJobType.setCompany(user.getCompany());
        return mapper.asDTO(repository.save(newJobType));
    }

    public JobTypeDTO editJobType(JobTypeDTO updatedJobType){
        if (updatedJobType.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        JobType jobType = repository.findById(updatedJobType.getId()).orElseThrow(() -> new BadRequestAlertException("jopType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateJobtype(mapper.fromDTO(updatedJobType), jobType);
        return mapper.asDTO(repository.save(jobType));
    }

    public void deleteJobType(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        if(jobTypeToDelete.getCompany().getId() == user.getCompany().getId() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            jobTypeToDelete.getPositions().forEach(position -> {
                //positionRepository.delete(position);
                positionRepository.delete(position.getId());
            });
            repository.delete(jobTypeToDelete);
        } else {
            throw new BadRequestAlertException("no permission to delete", ENTITY_NAME, "wrong user ");
        }
    }

}
