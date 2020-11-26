package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "mission")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    private String projectManagerEmail;


    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("id")
    @JsonIgnoreProperties(value = "mission")
    private List<Position> positions = new ArrayList<>();

    @NotNull
    @ManyToOne(optional = false)
    @JsonIgnore
    private Company company;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createDateTime;
    private LocalDateTime deletedAt;
}
