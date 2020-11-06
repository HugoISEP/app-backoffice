package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
}
