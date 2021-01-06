package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ToString(exclude = {"mission", "jobType"})
@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "duration", nullable = false)
    private int duration;

    @NotNull
    @Column(name = "places_number", nullable = false)
    private int placesNumber;

    @NotNull
    @Column(name = "remuneration", nullable = false)
    private float remuneration;

    @NotNull
    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @JsonIgnore
    @ManyToOne
    private Mission mission;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Column(name = "status", nullable = false)
    private boolean status = false;

    @ManyToOne
    private JobType jobType;

}
