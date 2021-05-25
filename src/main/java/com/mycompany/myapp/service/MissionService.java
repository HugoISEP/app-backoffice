package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.MissionDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.mapper.MissionMapper;
import com.mycompany.myapp.service.view.MissionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class MissionService {

    private static final String ENTITY_NAME = "mission";

    private final MissionRepository repository;
    private final MissionMapper mapper;
    private final UserService userService;
    private final CompanyMapper companyMapper;
    @Lazy
    private final PositionService positionService;

    public void hasAuthorization(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User not found", ENTITY_NAME, "id doesn't exist"));
        Mission mission = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found", ENTITY_NAME, "id doesn't exist"));
        if(!user.getAuthorities().contains(AuthoritiesConstants.ADMIN) && !user.getCompany().getId().equals(mission.getCompany().getId())){
            throw new AccessDeniedException("user not authorize");
        }
    }

    public MissionDTO getById(Long id){
        hasAuthorization(id);
        return mapper.asDTO(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("position doesn't exist", ENTITY_NAME, "id doesn't exist")));
    }

    public MissionDTO createMission(MissionDTO mission){
        if (mission.getId() != null) {
            throw new ResourceNotFoundException("A new mission cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("user not found", ENTITY_NAME, "id exists"));
        Mission newMission = mapper.fromDTO(mission);
        newMission.setCompany(companyMapper.fromDTO(user.getCompany()));
        return mapper.asDTO(repository.save(newMission));
    }

    public void deleteMission(Long id){
        hasAuthorization(id);
        Mission mission = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mission.setDeletedAt(LocalDateTime.now());
        mission.getPositions().forEach(position -> position.setDeletedAt(LocalDateTime.now()));
        positionService.clearPositionCacheByCompany(mission.getCompany());
        repository.save(mission);
    }

    public MissionDTO editMission(MissionDTO missionToEdit){
        if (missionToEdit.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        hasAuthorization(missionToEdit.getId());

        Mission mission = repository.findById(missionToEdit.getId()).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateMission(mapper.fromDTO(missionToEdit), mission);
        return mapper.asDTO(repository.save(mission));
    }

    public Page<MissionView> getAllMissionByCompany(Pageable pageable, Optional<String> searchTerm){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("user not found", ENTITY_NAME, "id exists"));
        return repository.findAllByCompanyId(user.getCompany().getId(), searchTerm.orElse("%%"), pageable);
    }
}
