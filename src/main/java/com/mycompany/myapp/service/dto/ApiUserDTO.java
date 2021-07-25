package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.ApiUserView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiUserDTO implements ApiUserView {
    private String apiKey;
    private UserDTO user;
}
