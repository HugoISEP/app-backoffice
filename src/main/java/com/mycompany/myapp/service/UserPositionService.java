package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.UserPositionRepository;
import com.mycompany.myapp.service.dto.UserPositionDto;
import com.mycompany.myapp.service.mapper.UserPositionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserPositionService {

    private final UserPositionRepository repository;
    private final UserPositionMapper mapper;

    public UserPositionDto update(UserPositionDto dto) {
        return mapper.asDto(repository.save(mapper.fromDto(dto)));
    }
}
