package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"mission", "jobType"})
@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "duration", nullable = false)
    @Min(value = 1)
    private int duration;

    @NotNull
    @Column(name = "places_number", nullable = false)
    @Min(value = 1)
    private int placesNumber;

    @Column(name = "remuneration")
    private Float remuneration;

    @NotNull
    @Column(name = "description", length = 500, nullable = false)
    @Size(min = 1, max = 500)
    private String description;

    @JsonIgnore
    @ManyToOne
    private Mission mission;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Column(name = "status", nullable = false)
    private boolean status = true;

    @ManyToOne
    private JobType jobType;

    @JsonIgnore
    private LocalDateTime lastNotificationAt;

}
