package com.juniorisep.backofficeJE.service.dto;

import com.juniorisep.backofficeJE.domain.Company;
import com.juniorisep.backofficeJE.service.view.MissionView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionDTO implements MissionView {
    private Long id;
    private String name;
    private String projectManagerEmail;
    private LocalDateTime createdAt;
    private List<PositionDTO> positions = new ArrayList<>();
    private Company company;

}
