package com.mycompany.myapp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PositionWithUserDTO extends PositionDTO {

    public PositionWithUserDTO(String comment, UserDTO user, Long mark, PositionDTO positionDTO) {
        super(positionDTO.getId(), positionDTO.getDuration(), positionDTO.getDescription(), positionDTO.getPlacesNumber(), positionDTO.getRemuneration(),
            positionDTO.getCreatedAt(),  positionDTO.isStatus(), positionDTO.getJobType(), positionDTO.getMission(), positionDTO.getDeletedAt());
        this.comment = comment;
        this.user = user;
        this.mark = mark;
    }

    private String comment;
    private UserDTO user;
    private Long mark;
}
