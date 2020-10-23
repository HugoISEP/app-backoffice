package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Technology;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.repository.TechnologyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;

@Service
public class JobTypeService {
    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final String ENTITY_NAME = "jobType";

    private final JobTypeRepository repository;
    private final MissionRepository missionRepository;
    private final TechnologyRepository technologyRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public JobTypeService(JobTypeRepository repository, MissionRepository missionRepository, TechnologyRepository technologyRepository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.missionRepository = missionRepository;
        this.technologyRepository = technologyRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }


    public Mission addJobType(Long missionId, JobType jobType){
        if (jobType.getId() != null) {
            throw new BadRequestAlertException("A new jobType cannot already have an ID", ENTITY_NAME, "id exists");
        }
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        Technology technology = technologyRepository.findById(jobType.getTechnology().getId()).orElseThrow(() -> new BadRequestAlertException("technology doesn't exist", ENTITY_NAME, "id doesn't exist"));
        jobType.setTechnology(technology);
        jobType.setMission(mission);
        technology.getJobTypes().add(jobType);
        mission.getJobTypes().add(jobType);

        return missionRepository.save(mission);
    }

    public void deleteJobType(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new JobTypeService.AccountResourceException("User could not be found"));
        JobType jobTypeToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Technologie doesn't exist", ENTITY_NAME, "id doesn't exist"));

        if(jobTypeToDelete.getMission().getUser().getLogin() == user.getLogin() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            repository.delete(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Cannot delete ", ENTITY_NAME, " id doesn't exist")));
        } else {
            new JobTypeService.AccountResourceException("Access Forbidden");
        }
    }
}
