package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Technology;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    List<Technology> findTechnologiesByUserId(Long id);
}
