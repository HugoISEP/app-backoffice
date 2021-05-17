package com.mycompany.myapp.service.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserPositionDto {
    Long id;

    @Size(max = 2048)
    String comment;

    Long remuneration;
    UserDTO user;
    PositionDTO position;
}
