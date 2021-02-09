package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.CompanyDetailsView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("select c from Company c where lower(:email) like lower(concat('%',c.emailTemplate))")
    Optional<Company> findCompanyByUserEmail(@Param("email") String email);

    @Query("select c from Company c join User u on u.company.id = c.id where u.id = :id")
    CompanyDetailsView findCompanyFromCurrentUser(@Param("id") Long id);

    @Query("select c from Company c " +
        "where (lower(c.name) like concat('%',lower(:searchTerm),'%') or lower(c.emailTemplate) like concat('%',lower(:searchTerm),'%'))")
    Page<CompanyDetailsView> findAllPaginated(Pageable pageable, @Param("searchTerm") String searchTerm);

    @Query("select c from Company c " +
        "join Position p on p.id = :id " +
        "join Mission m on m.id = p.mission.id " +
        "where m.company.id = c.id")
    Optional<Company> findCompanyByPositionId(@Param("id") Long id);
}
