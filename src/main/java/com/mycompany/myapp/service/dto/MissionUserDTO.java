package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.MissionUserView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionUserDTO implements MissionUserView {
    private Long id;
    private String name;
    private String projectManagerEmail;
    private CompanyDTO company;
    private LocalDateTime endedAt;

}
