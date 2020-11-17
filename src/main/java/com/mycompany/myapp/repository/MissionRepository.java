package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.service.view.MissionView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAll();

    @Query("select m from Mission m join Company c on c.id = m.company.id " +
        "where c.id = :id and lower(m.name) like concat('%',:searchTerm,'%')")
    Page<MissionView> findAllByCompanyId(@Param("id") Long id, @Param("searchTerm") String searchTerm, Pageable pageable);
}
