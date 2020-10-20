package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobTypeRepository extends JpaRepository<JobType, Long> {
    List<JobType> findAllByMission_Id(Long id);
}
