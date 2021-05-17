package com.mycompany.myapp.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user_position")

public class UserPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment", length = 2048)
    @Size(max = 2048)
    private String comment;

    private Long mark;

    private Float remuneration;

    @ManyToOne
    private User user;

    @ManyToOne
    private Position position;

}
