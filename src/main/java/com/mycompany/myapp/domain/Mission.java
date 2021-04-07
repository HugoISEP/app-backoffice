package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"positions"})
@Where(clause = "deleted_at is null ")
@Table(name = "mission")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    private String projectManagerEmail;

    @OrderBy("id")
    @Fetch(FetchMode.JOIN)  //TODO: a modifier, pb de fetch dans la query du repo
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    private List<Position> positions = new ArrayList<>();

    @NotNull
    @JsonIgnore
    @ManyToOne(optional = false)
    private Company company;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

}
