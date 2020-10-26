package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findAllByMissionId(Long id);
}
