package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mycompany.myapp.service.validator.IconConstraint;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"positions"})
@Table(name = "job_type")
public class JobType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @IconConstraint
    @Column(name = "icon", nullable = false)
    private String icon;

    @JsonIgnore
    @OneToMany(mappedBy = "jobType", cascade = CascadeType.ALL)
    private List<Position> positions = new ArrayList<>();

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Company company;

}
