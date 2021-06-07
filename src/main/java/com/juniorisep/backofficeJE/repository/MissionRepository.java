package com.juniorisep.backofficeJE.repository;

import com.juniorisep.backofficeJE.domain.Mission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAll();

    @Query("select m from Mission m " +
        "where m.company.id = :id and m.deletedAt is null " +
        "and (lower(m.name) like concat('%',lower(:searchTerm),'%') or lower(m.projectManagerEmail) like concat('%',lower(:searchTerm),'%')) " +
        "group by m.id")
    Page<Mission> findAllByCompanyId(@Param("id") Long id, @Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("select m from Mission m where m.deletedAt is null and m.id = :id")
    Optional<Mission> findById(@Param("id") Long id);

    Optional<Mission> findByIdAndDeletedAtIsNotNull(Long id);
}
