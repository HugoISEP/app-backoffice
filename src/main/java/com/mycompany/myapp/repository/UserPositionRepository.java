package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
}
