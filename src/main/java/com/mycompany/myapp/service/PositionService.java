package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.MissionDTO;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.MissionMapper;
import com.mycompany.myapp.service.mapper.PositionMapper;
import com.mycompany.myapp.service.notification.NotificationService;
import com.mycompany.myapp.service.view.PositionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class PositionService {

    private static final String ENTITY_NAME = "position";

    private final Logger log = LoggerFactory.getLogger(PositionService.class);


    private final PositionRepository repository;
    private final PositionMapper mapper;
    private final MissionRepository missionRepository;
    private final MissionMapper missionMapper;
    private final MissionService missionService;
    private final JobTypeRepository jobTypeRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public PositionService(PositionRepository repository, PositionMapper mapper, MissionRepository missionRepository, MissionMapper missionMapper, MissionService missionService, JobTypeRepository jobTypeRepository, UserService userService, NotificationService notificationService) {
        this.repository = repository;
        this.mapper = mapper;
        this.missionRepository = missionRepository;
        this.missionMapper = missionMapper;
        this.missionService = missionService;
        this.jobTypeRepository = jobTypeRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public void hasAuthorization(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        Position position = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found", ENTITY_NAME, "id doesn't exist"));
        if(!user.getAuthorities().contains(AuthoritiesConstants.ADMIN) && !user.getCompany().getId().equals(position.getMission().getCompany().getId())){
            throw new AccessDeniedException("user not authorize");
        }
    }

    public PositionDTO getById(Long id){
        hasAuthorization(id);
        return mapper.asDto(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("position doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    public List<PositionView> getByMissionId(Long id){
        missionService.hasAuthorization(id);
        return repository.findAllByMissionId(id);
    }

    public Page<PositionView> getActivePositionsByUser(Pageable pageable, String searchTerm){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("user not found", ENTITY_NAME, "id exists"));
        return repository.findAllByMissionCompanyIdAndStatusIsTrue(user.getCompany().getId(), searchTerm, pageable);
    }


    public MissionDTO addPosition(Long missionId, PositionDTO position){
        missionService.hasAuthorization(missionId);
        Position newPosition = mapper.fromDTO(position);
        if (newPosition.getId() != null) {
            throw new BadRequestAlertException("A new Position cannot already have an ID", ENTITY_NAME, "id exists");
        }
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new ResourceNotFoundException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        newPosition.setMission(mission);
        mission.getPositions().add(newPosition);
        try {
            notificationService.sendMessage(newPosition);
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Error when sending notification: " + e.toString());
        }
        return missionMapper.asDTO(missionRepository.save(mission));
    }

    public PositionDTO editPosition(PositionDTO updatedPosition){
        if (updatedPosition.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        hasAuthorization(updatedPosition.getId());

        Position position = repository.findById(updatedPosition.getId()).orElseThrow(() -> new ResourceNotFoundException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updatePosition(mapper.fromDTO(updatedPosition), position);
        return mapper.asDto(repository.save(position));
    }

    public void deletePosition(Position position){
        hasAuthorization(position.getId());
        repository.delete(position);
    }
}
