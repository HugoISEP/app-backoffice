package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.UserPositionRepository;
import com.mycompany.myapp.service.dto.UserPositionDto;
import com.mycompany.myapp.service.mapper.UserPositionMapper;
import com.mycompany.myapp.service.view.UserPositionView;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserPositionService {

    private final UserPositionRepository repository;
    private final UserPositionMapper mapper;

    private final String ENTITY_NAME = "USER_POSITION";

    public UserPositionDto update(UserPositionDto dto) {
        return mapper.asDto(repository.save(mapper.fromDto(dto)));
    }

    public UserPositionView getViewById(Long id) {
        return repository.findUserPositionViewById(id).orElseThrow(()-> new ResourceNotFoundException("UserPosition not found", ENTITY_NAME, "id not found"));
    }

    public Page<UserPositionView> getViewsPaginatedByUserId(Pageable pageable, Long id) {
        return repository.findByUserId(pageable, id);
    }

    public Page<UserPositionView> getViewsPaginatedByPositionId(Pageable pageable, Long id) {
        return repository.findByPositionId(pageable, id);
    }

    public Page<UserPositionView> getViewsPaginatedByMissionId(Pageable pageable, Long id) {
        return repository.findByPositionMissionId(pageable, id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }



}
