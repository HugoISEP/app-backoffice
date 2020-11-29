package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.config.Constants;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.view.UserView;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
@NoArgsConstructor
public class UserDTO implements UserView {

    private Long id;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String imageUrl;

    private boolean activated = false;

    @Size(min = 2, max = 10)
    private String langKey;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<String> authorities;

    private CompanyDTO company;

    private List<JobTypeDTO> jobTypes;

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.getActivated();
        this.imageUrl = user.getImageUrl();
        this.langKey = user.getLangKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.authorities = user.getAuthorities().stream()
            .map(Authority::getName)
            .collect(Collectors.toSet());
        if (user.getCompany() != null){
            this.company = new CompanyDTO(user.getCompany().getId(), user.getCompany().getName(), user.getCompany().getEmailTemplate(), user.getCompany().getColor(), user.getCompany().getImagePath());
        }
        this.jobTypes = user.getJobTypes().stream()
            .map(j -> new JobTypeDTO(j.getId(), j.getName(), this.company)).collect(Collectors.toList());
    }

}
