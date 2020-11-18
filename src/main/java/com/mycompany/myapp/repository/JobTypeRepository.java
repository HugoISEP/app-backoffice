package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.service.view.JobTypeView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobTypeRepository extends JpaRepository<JobType, Long> {

    @Query("select j from JobType j join Company c on c.id = j.company.id " +
        "where c.id = :id and lower(j.name) like concat('%',lower(:searchTerm),'%')")
    Page<JobTypeView> findAllByCompanyId(@Param("id") Long id, @Param("searchTerm") String searchTerm, Pageable pageable);
}
