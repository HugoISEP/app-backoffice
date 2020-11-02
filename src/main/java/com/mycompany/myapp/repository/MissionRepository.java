package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.service.view.MissionView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findAll();
    List<Mission> findAllByUserId(Long id);
}
