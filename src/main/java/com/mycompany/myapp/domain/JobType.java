package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Data
@Entity
@Table(name = "job_type")
public class JobType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> nameTranslations = new HashMap<>();

    @OneToMany(mappedBy = "jobType", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Position> positions = new ArrayList<>();

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createDateTime;
    private LocalDateTime deletedAt;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Company company;
}
