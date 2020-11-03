package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.PositionMapper;
import com.mycompany.myapp.service.view.PositionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    private static final String ENTITY_NAME = "position";

    private final PositionRepository repository;
    private final PositionMapper mapper;
    private final MissionRepository missionRepository;
    private final JobTypeRepository jobTypeRepository;
    private final UserService userService;

    public PositionService(PositionRepository repository, PositionMapper mapper, MissionRepository missionRepository, JobTypeRepository jobTypeRepository, UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.missionRepository = missionRepository;
        this.jobTypeRepository = jobTypeRepository;
        this.userService = userService;
    }

    public List<PositionView> getActivePositionsByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", ENTITY_NAME, "id exists"));

        return repository.findAllByMission_User_IdAndStatusIsTrue(user.getId());
    }


    public Mission addPosition(Long missionId, PositionDTO position){
        Position newPosition = mapper.fromDTO(position);
        if (newPosition.getId() != null) {
            throw new BadRequestAlertException("A new Position cannot already have an ID", ENTITY_NAME, "id exists");
        }
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        JobType jobType = jobTypeRepository.findById(newPosition.getJobType().getId()).orElseThrow(() -> new BadRequestAlertException("jobType doesn't exist", ENTITY_NAME, "id doesn't exist"));
        newPosition.setJobType(jobType);
        newPosition.setMission(mission);
        jobType.getPositions().add(newPosition);
        mission.getPositions().add(newPosition);

        return missionRepository.save(mission);
    }

    public Position editPosition(PositionDTO position){
        Position newPosition = mapper.fromDTO(position);
        if (newPosition.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        Position oldPosition = repository.findById(newPosition.getId()).orElseThrow(() -> new BadRequestAlertException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        oldPosition.setStatus(newPosition.isStatus());
        oldPosition.setJobType(newPosition.getJobType());
        oldPosition.setDescription(newPosition.getDescription());
        oldPosition.setDuration(newPosition.getDuration());
        return oldPosition;
    }

    public void deletePosition(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", ENTITY_NAME, "id exists"));
        Position positionToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        if(positionToDelete.getMission().getUser().getId() == user.getId() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            repository.delete(positionToDelete.getId());
        } else {
            throw new BadRequestAlertException("no permission to delete", ENTITY_NAME, "wrong user");
        }
    }
}
