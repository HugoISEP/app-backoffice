package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.MissionView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionDTO implements MissionView {
    private Long id;
    private String name;
    private String projectManagerEmail;
    private LocalDateTime createdAt;
    private List<PositionDTO> positions;
    private Company company;

}
