package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.repository.MissionRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MissionService {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final String ENTITY_NAME = "mission";

    private final MissionRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;

    public MissionService(MissionRepository repository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public Mission createMission(Mission newMission){
        if (newMission.getId() != null) {
            throw new BadRequestAlertException("A new mission cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new MissionService.AccountResourceException("User could not be found"));
        newMission.setUser(userMapper.userDTOToUser(user));
        return repository.save(newMission);
    }

    public void deleteMission(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new MissionService.AccountResourceException("User could not be found"));
        Mission missionToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Technologie doesn't exist", ENTITY_NAME, "id doesn't exist"));

        if(missionToDelete.getUser().getLogin() == user.getLogin() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            repository.delete(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Cannot delete ", ENTITY_NAME, " id doesn't exist")));
        } else {
            new MissionService.AccountResourceException("Access forbidden");
        }
    }

    public Mission editMission(Mission missionToEdit){
        if (missionToEdit.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        Mission oldMission = repository.findById(missionToEdit.getId()).orElseThrow(() -> new BadRequestAlertException("mission doesn't exist", ENTITY_NAME, "id doesn't exist"));
        oldMission.setName(missionToEdit.getName());
        return repository.save(oldMission);
    }

    public List<Mission> getAllMissionByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new MissionService.AccountResourceException("User could not be found"));
        return repository.findAllByUserId(user.getId());
    }
}
