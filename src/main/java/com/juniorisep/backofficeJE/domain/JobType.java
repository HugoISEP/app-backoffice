package com.juniorisep.backofficeJE.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juniorisep.backofficeJE.service.validator.IconConstraint;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = {"positions"})
@EqualsAndHashCode(of = {"id"})
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
