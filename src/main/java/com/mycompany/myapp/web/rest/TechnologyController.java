package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Technology;
import com.mycompany.myapp.repository.TechnologyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/technology")
public class TechnologyController {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final String ENTITY_NAME = "technology";

    private final TechnologyRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;

    public TechnologyController(TechnologyRepository repository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public Technology getById(@PathVariable Long id){
        return repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Technologie doesn't exist", ENTITY_NAME, "id doesn't exist"));
    }

    @GetMapping
    public List<Technology> getAllByUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new TechnologyController.AccountResourceException("User could not be found"));
        return repository.findTechnologiesByUserId(user.getId());
    }


    @GetMapping("/all")
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<Technology> getAll(){
        return repository.findAll();
    }

    @PostMapping
    public Technology create(@Valid @RequestBody Technology technology){
        if (technology.getId() != null) {
            throw new BadRequestAlertException("A new technologie cannot already have an ID", ENTITY_NAME, "id exists");
        }
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new TechnologyController.AccountResourceException("User could not be found"));
        technology.setUser(userMapper.userDTOToUser(user));
        return repository.save(technology);
    }

    @PutMapping
    @PostAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") || principal.username == returnObject.user.login ")
    public Technology edit(@Valid @RequestBody Technology technology){
        if (technology.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        Technology oldTechnology = repository.findById(technology.getId()).orElseThrow(() -> new BadRequestAlertException("technology doesn't exist", ENTITY_NAME, "id doesn't exist"));
        oldTechnology.setName(technology.getName());
        return repository.save(technology);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new TechnologyController.AccountResourceException("User could not be found"));
        Technology technologyToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Technologie doesn't exist", ENTITY_NAME, "id doesn't exist"));

        if(technologyToDelete.getUser().getLogin() == user.getLogin() || user.getAuthorities().contains(AuthoritiesConstants.ADMIN)){
            repository.delete(repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Cannot delete ", ENTITY_NAME, " id doesn't exist")));
        } else {
            new TechnologyController.AccountResourceException("Access Forbidden");
        }

    }
}
