package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.service.view.PositionView;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    String POSITIONS_AVAILABLE_CACHE = "positionsAvailable";

    List<PositionView> findAllByMissionIdAndDeletedAtIsNull(Long id);

    @Cacheable(cacheNames = POSITIONS_AVAILABLE_CACHE, key = "#p0", condition = "#p1 == '%%' && #p2.pageSize == 50")
    @Query("select p from Position p " +
        "where p.mission.company.id = :id and p.status = true and p.deletedAt is null and " +
        "lower(p.mission.name) like concat('%',lower(:searchTerm),'%') " +
        "order by p.createdAt")
    Page<PositionView> findAllByMissionCompanyIdAndStatusIsTrue(@Param("id") Long id, @Param("searchTerm") String searchTerm, Pageable pageable);

    @Cacheable(cacheNames = POSITIONS_AVAILABLE_CACHE, key = "#p0", condition = "#p1 == '%%' && #p2.pageSize == 50")
    @Query("select p from Position p " +
        "where p.mission.company.id = :id and p.deletedAt is null and " +
        "lower(p.mission.name) like concat('%',lower(:searchTerm),'%') " +
        "order by p.createdAt")
    Page<PositionView> findAllByMissionCompanyId(@Param("id") Long id, @Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("select p from Position p where p.deletedAt is null and p.id = :id")
    Optional<Position> findById(@Param("id") Long id);

    Optional<Position> findByIdAndDeletedAtIsNotNull(Long id);
}
