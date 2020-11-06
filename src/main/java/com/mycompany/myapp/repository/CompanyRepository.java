package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
