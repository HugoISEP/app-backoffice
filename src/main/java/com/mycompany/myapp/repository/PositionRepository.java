package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.service.view.PositionView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<PositionView> findAllByMissionId(Long id);
    List<PositionView> findAllByMissionCompanyIdAndStatusIsTrue(Long id);
}
