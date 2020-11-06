package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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

    public List<JobTypeView> getAllJobTypeByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        return repository.findAllByEntrepriseId(user.getEntreprise().getId());
    }

    public JobTypeDTO createJobType(JobTypeDTO jobTypeDTO){
        JobType newJobType = mapper.fromDTO(jobTypeDTO);
        if (newJobType.getId() != null) {
            throw new BadRequestAlertException("A new JobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        newJobType.setEntreprise(user.getEntreprise());
        return mapper.asDTO(repository.save(newJobType));
    }

    public JobTypeDTO editJobType(JobTypeDTO jobType){
        if (jobType.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        JobType oldJobType = repository.findById(jobType.getId()).orElseThrow(() -> new BadRequestAlertException("technology doesn't exist", ENTITY_NAME, "id doesn't exist"));
        oldJobType.setName(jobType.getName());
        return mapper.asDTO(repository.save(oldJobType));
    }

    public void deleteJobType(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("JobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        if(jobTypeToDelete.getEntreprise().getId() == user.getEntreprise().getId() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
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
