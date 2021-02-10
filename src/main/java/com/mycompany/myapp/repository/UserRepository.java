package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneByEmail(String email);

    @EntityGraph(attributePaths= {"authorities", "jobTypes", "company"})
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    @Query("select u from User u join u.authorities a " +
        "where u.login not like :login " +
        "and (lower(u.firstName) like concat('%',lower(:searchTerm),'%') or lower(u.lastName) like concat('%',lower(:searchTerm),'%') or lower(u.email) like concat('%',lower(:searchTerm),'%'))")
    Page<User> findAllWithSearchTerm(Pageable pageable, @Param("login") String login, @Param("searchTerm") String searchTerm);

    List<User> findAllByCompany(Company company);

    @Query("select u from User u join u.authorities a " +
        "where a.name like 'ROLE_USER' and size(u.authorities)=1 and u.company.id = :id " +
        "and (lower(u.firstName) like concat('%',lower(:searchTerm),'%') or lower(u.lastName) like concat('%',lower(:searchTerm),'%') or lower(u.email) like concat('%',lower(:searchTerm),'%'))")
    Page<User> findAllUsersByManager(Pageable pageable, @Param("id") Long id, @Param("searchTerm") String searchTerm);
}
