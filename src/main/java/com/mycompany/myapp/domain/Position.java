package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
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
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = "positions")
    private Mission mission;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Column(name = "status", nullable = false)
    private boolean status = false;

    @ManyToOne
    @JsonIgnoreProperties(value = "positions")
    private JobType jobType;
}
