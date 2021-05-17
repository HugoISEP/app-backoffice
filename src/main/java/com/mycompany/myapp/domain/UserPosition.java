package com.mycompany.myapp.domain;

import lombok.*;

import javax.persistence.*;

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

    private String comment;

    private Long mark;

    private Float remuneration;

    @ManyToOne
    private User user;

    @ManyToOne
    private Position position;

}
