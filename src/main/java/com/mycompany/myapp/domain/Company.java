package com.mycompany.myapp.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "email_template", nullable = false, unique = true)
    private String emailTemplate;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createDateTime;
    private LocalDateTime deletedAt;

    @Formula("(select count(c.id) from company c join jhi_user u on c.id = u.company_id where c.id = id)")
    private int totalUsers;

}
