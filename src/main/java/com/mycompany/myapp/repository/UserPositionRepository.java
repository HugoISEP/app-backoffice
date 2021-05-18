package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.UserPosition;
import com.mycompany.myapp.service.view.UserPositionView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {

    Optional<UserPositionView> findUserPositionViewById(Long id);

    Page<UserPositionView> findByUserId(Pageable pageable, Long id);
    Page<UserPositionView> findByPositionId(Pageable pageable, Long id);
    Page<UserPositionView> findByPositionMissionId(Pageable pageable, Long id);
}
