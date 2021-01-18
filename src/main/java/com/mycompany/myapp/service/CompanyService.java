package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.CompanyDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.view.CompanyDetailsView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import io.minio.errors.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static com.mycompany.myapp.config.Constants.COMPANY_BUCKET;


@Service
@Transactional
public class CompanyService {

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository repository;
    private final CompanyMapper mapper;
    private final UserService userService;
    private final MinioService minioService;

    public CompanyService(CompanyRepository repository, CompanyMapper mapper, UserService userService, MinioService minioService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
        this.minioService = minioService;
    }

    public void hasAuthorization(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        Company company = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found", ENTITY_NAME, "id doesn't exist"));
        if(!user.getAuthorities().contains(AuthoritiesConstants.ADMIN) && !user.getCompany().getId().equals(company.getId())){
            throw new AccessDeniedException("user not authorize");
        }
    }

    public CompanyDetailsView getCompanyFromCurrentUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("user not found", ENTITY_NAME, " id exists"));
        return repository.findCompanyFromCurrentUser(user.getId());
    }

    public Page<CompanyDetailsView> getAllPaginated(Pageable pageable, String searchTerm){
        return repository.findAllPaginated(pageable, searchTerm);
    }

    public CompanyDTO create(String companyJson, MultipartFile file) throws Exception {
        String timestamp = LocalDateTime.now().toString();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CompanyDTO company = objectMapper.readValue(companyJson, CompanyDTO.class);
            minioService.uploadFile(file, company.getName() + "-" + timestamp + ".png", COMPANY_BUCKET);
            company.setImagePath(company.getName() + "-" + timestamp + ".png");
            return mapper.asDTO(repository.save(mapper.fromDTO(company)));
        } catch (Exception e){
            throw new Exception("Error when creating new company: " + e.toString());
        }
    }

    public CompanyDTO edit(CompanyDTO updatedCompany, MultipartFile file) throws MinioException {
        if (updatedCompany.getId() == null) {
            throw new ResourceNotFoundException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        String timestamp = LocalDateTime.now().toString();
        hasAuthorization(updatedCompany.getId());

        Company company = repository.findById(updatedCompany.getId()).orElseThrow(() -> new ResourceNotFoundException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        if(file != null){
            minioService.uploadFile(file, updatedCompany.getName() + "-" + timestamp + ".png", COMPANY_BUCKET);
            updatedCompany.setImagePath(updatedCompany.getName() + "-" + timestamp + ".png");
            minioService.deleteFile(company.getImagePath(), COMPANY_BUCKET);
        }
        mapper.updateCompany(updatedCompany, company);
        return mapper.asDTO(company);
    }

    public void delete(Long id) throws MinioException {
        hasAuthorization(id);
        Company companyToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        minioService.deleteFile(companyToDelete.getImagePath(), COMPANY_BUCKET);
        repository.delete(companyToDelete);
    }

    public String getFileUrl(Long companyId) throws MinioException {
        Company company = repository.findById(companyId).orElseThrow(() -> new BadRequestAlertException("company not found" , "COMPANY", " id doesn't exist"));
        return minioService.getFileUrl(company.getImagePath(), COMPANY_BUCKET);
    }

}
