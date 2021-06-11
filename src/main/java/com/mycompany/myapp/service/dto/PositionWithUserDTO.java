package com.mycompany.myapp.service.dto;

import lombok.Data;

@Data
public class PositionWithUserDTO extends PositionDTO {
    private String comment;
    private UserDTO user;
    private Long mark;
}
