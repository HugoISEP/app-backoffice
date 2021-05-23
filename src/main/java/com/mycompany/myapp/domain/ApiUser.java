package com.mycompany.myapp.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_user")
public class ApiUser {

    @Id
    @NotNull
    @Column(name= "api_key", length = 25, unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String apiKey;

    @OneToOne
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDateTime;


}
