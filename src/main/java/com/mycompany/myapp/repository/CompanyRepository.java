package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.CompanyView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("select c from Company c")
    Page<CompanyView> findAllPaginated(Pageable pageable);
}
