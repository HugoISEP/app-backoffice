package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.repository.PositionRepository;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.dto.PositionWithUserDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.PositionMapper;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.service.notification.NotificationService;
import com.mycompany.myapp.service.notification.NotificationStatus;
import com.mycompany.myapp.service.view.PositionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@RequiredArgsConstructor
public class PositionService {

    private static final String ENTITY_NAME = "position";

    private final Logger log = LoggerFactory.getLogger(PositionService.class);


    private final PositionRepository repository;
    private final PositionMapper mapper;
    private final UserMapper userMapper;
    private final MissionRepository missionRepository;
    private final CompanyRepository companyRepository;
    private final MissionService missionService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final CacheManager cacheManager;
    private final UserPositionRepository userPositionRepository;

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

    public PositionWithUserDTO getWithUserById(Long id) {
        hasAuthorization(id);
        Position p = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        List<UserPosition> positions = userPositionRepository.findByPosition(p);
        if (positions.size() > 0){
            return new PositionWithUserDTO(positions.get(0).getComment(), userMapper.userToUserDTO(positions.get(0).getUser()), positions.get(0).getMark(),mapper.asDto(p));
        }
        return new PositionWithUserDTO(null, null, null, mapper.asDto(p));

    }

    public List<PositionView> getByMissionId(Long id){
        missionService.hasAuthorization(id);
        return repository.findAllByMissionIdAndDeletedAtIsNull(id);
    }

    public Page<PositionView> getActivePositionsByUser(Pageable pageable, String searchTerm){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("user not found", ENTITY_NAME, "id exists"));
        return repository.findAllByMissionCompanyIdAndStatusIsTrue(user.getCompany().getId(), searchTerm, pageable);
    }

    public Page<PositionView> getPositionsByUser(Pageable pageable, String searchTerm){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("user not found", ENTITY_NAME, "id exists"));
        Page<PositionView> response = repository.findAllByMissionCompanyIdAndStatusIsTrue(user.getCompany().getId(), searchTerm, pageable);
        log.debug(String.valueOf(response.getContent()));
        return response;
    }


    public PositionDTO addPosition(Long missionId, PositionWithUserDTO position){
        missionService.hasAuthorization(missionId);
        Position newPosition = mapper.fromDTO(position);
        if (newPosition.getId() != null) {
            throw new BadRequestAlertException("A new Position cannot already have an ID", ENTITY_NAME, "id exists");
        }
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new ResourceNotFoundException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        newPosition.setMission(mission);
        mission.getPositions().add(newPosition);
        try {
            if (newPosition.isStatus()){
                notificationService.sendMessage(newPosition, NotificationStatus.NEW);
                newPosition.setLastNotificationAt(LocalDateTime.now());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Error when sending notification: " + e.toString());
        }
        Mission missionSaved = missionRepository.save(mission);
        Position savedPosition = missionSaved.getPositions().stream().sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())).findFirst().get();
        this.clearPositionCacheByPosition(mission.getCompany().getId());
        UserPosition newUserPosition = new UserPosition();
        newUserPosition.setPosition(savedPosition);
        newUserPosition.setUser(userService.getUserById(position.getUser().getId()));
        newUserPosition.setMark(position.getMark());
        newUserPosition.setComment(position.getComment());
        userPositionRepository.save(newUserPosition);
        PositionDTO positionDTO = mapper.asDto(savedPosition);
        return positionDTO;
    }

    public PositionDTO editPosition(PositionWithUserDTO updatedPosition){
        if (updatedPosition.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        hasAuthorization(updatedPosition.getId());
        UserPosition newUserPosition = new UserPosition();

        Position position = repository.findById(updatedPosition.getId()).orElseThrow(() -> new ResourceNotFoundException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        newUserPosition.setPosition(position);
        newUserPosition.setUser(userService.getUserById(updatedPosition.getUser().getId()));
        newUserPosition.setComment(updatedPosition.getComment());
        newUserPosition.setMark(updatedPosition.getMark());
        mapper.updatePosition(mapper.fromDTO(updatedPosition), position);
        this.clearPositionCacheByPosition(position.getMission().getCompany().getId());
        userPositionRepository.save(newUserPosition);
        return mapper.asDto(repository.save(position));
    }

    public void sendNotification(Long id) throws Exception {
        hasAuthorization(id);
        Position position = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        if(!position.isStatus()){
            throw new Exception("Position status incorrect");
        }
        if(position.getLastNotificationAt() != null && (ChronoUnit.SECONDS.between(LocalDateTime.now(), position.getLastNotificationAt().plusHours(2)) > 0)) {
            long timeDiff = ChronoUnit.SECONDS.between(LocalDateTime.now(), position.getLastNotificationAt().plusHours(2));
            throw new Exception("You must wait " + timeDiff / 3600 + " h " + timeDiff % 3600 / 60 + " min before sending a new notification");
        }
        try {
            notificationService.sendMessage(position, NotificationStatus.OLD);
            position.setLastNotificationAt(LocalDateTime.now());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Error when sending notification: " + e.toString());
        }
    }

    public void deletePosition(Long id){
        hasAuthorization(id);
        Position position = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("position doesn't exist", ENTITY_NAME, "id doesn't exist"));
        position.setDeletedAt(LocalDateTime.now());
        repository.save(position);
        this.clearPositionCacheByPosition(position.getMission().getCompany().getId());
    }

    public void clearPositionCacheByPosition(Long companyId) {
        try {
            Objects.requireNonNull(cacheManager.getCache(PositionRepository.POSITIONS_AVAILABLE_CACHE)).evict(companyRepository.findById(companyId).get().getId());
        } catch (Exception e) {
            log.warn("can't clear positions available cache: " + e.getMessage());
        }
    }

    public void clearPositionCacheByCompany(Company company) {
        Objects.requireNonNull(cacheManager.getCache(PositionRepository.POSITIONS_AVAILABLE_CACHE)).evict(company.getId());
    }

    public PositionView bindUserToPosition(Long userId, Long positionId) {
        UserPosition userPosition = UserPosition.builder()
            .position(repository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Position not found", ENTITY_NAME, "id doesn't exist")))
            .user(userService.getUserById(userId))
            .build();
        userPositionRepository.save(userPosition);
        return mapper.asDto(repository.findById(positionId).orElseThrow(()-> new ResourceNotFoundException("Position not found", ENTITY_NAME, "id doesn't exist")));
    }
}

