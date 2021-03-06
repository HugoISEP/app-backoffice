package com.mycompany.myapp.domain;

import com.mycompany.myapp.config.Constants;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString(exclude = {"users", "jobTypes", "missions"})
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

    @NotNull
    @Pattern(regexp = Constants.HEX_COLOR_REGEX)
    private String color;

    @NotNull
    @Column(name = "image_path", nullable = false, unique = true)
    private String imagePath;

    @URL
    @Column(name="website_url")
    private String websiteUrl;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<JobType> jobTypes;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Mission> missions;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Formula("(select count(c.id) from company c join jhi_user u on c.id = u.company_id where c.id = id)")
    private int totalUsers;

}
