package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
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

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> texts = new HashMap<>();

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
