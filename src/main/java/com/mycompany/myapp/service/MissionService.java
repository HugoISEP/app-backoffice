package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.MissionDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.MissionMapper;
import com.mycompany.myapp.service.view.MissionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MissionService {

    private static final String ENTITY_NAME = "mission";

    private final MissionRepository repository;
    private final MissionMapper mapper;
    private final UserService userService;
    private final PositionRepository positionRepository;

    public MissionService(MissionRepository repository, MissionMapper mapper, UserService userService, PositionRepository positionRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
        this.positionRepository = positionRepository;
    }

    public void hasAuthorization(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        Mission mission = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "id doesn't exist"));
        if(!user.getCompany().getId().equals(mission.getCompany().getId()) && !user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            throw new BadRequestAlertException("User not authorize ", ENTITY_NAME, " no permission");
        }
    }

    public Mission createMission(MissionDTO mission){
        if (mission.getId() != null) {
            throw new BadRequestAlertException("A new mission cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", ENTITY_NAME, "id exists"));
        Mission newMission = mapper.fromDTO(mission);
        newMission.setCompany(user.getCompany());
        return repository.save(newMission);
    }

    public void deleteMission(Long id){
        Mission missionToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Technologie doesn't exist", ENTITY_NAME, "id doesn't exist"));
        missionToDelete.getPositions().forEach(position -> {
            //positionRepository.delete(position);
            positionRepository.delete(position.getId());
        });
        repository.delete(missionToDelete);
    }

    public Mission editMission(MissionDTO missionToEdit){
        if (missionToEdit.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        Mission mission = repository.findById(missionToEdit.getId()).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateMission(mapper.fromDTO(missionToEdit), mission);
        return repository.save(mission);
    }

    public Page<MissionView> getAllMissionByCompany(Pageable pageable){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", ENTITY_NAME, "id exists"));
        return repository.findAllByCompanyId(user.getCompany().getId(), pageable);
    }
}
