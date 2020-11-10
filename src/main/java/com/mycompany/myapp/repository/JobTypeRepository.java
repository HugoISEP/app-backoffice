package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.service.view.JobTypeView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobTypeRepository extends JpaRepository<JobType, Long> {
    Page<JobTypeView> findAllByCompanyId(Long id, Pageable pageable);
}
