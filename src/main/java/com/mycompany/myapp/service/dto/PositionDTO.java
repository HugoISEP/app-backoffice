package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.PositionView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionDTO implements PositionView {

    private Long id;
    private int duration;
    private String description;
    private Map<String, String> texts;
    private int placesNumber;
    private float remuneration;
    private LocalDateTime createdAt;
    private boolean status;
    private JobTypeDTO jobType;
    private MissionUserDTO mission;

}
