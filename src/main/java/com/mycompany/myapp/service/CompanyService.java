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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class CompanyService {

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository repository;
    private final CompanyMapper mapper;
    private final UserService userService;

    private final String directoryPath = "/images/";
    private final String absolutePath = Paths.get("").toAbsolutePath().toString();

    public CompanyService(CompanyRepository repository, CompanyMapper mapper, UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
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

    public CompanyDTO create(String companyJson, MultipartFile file) throws IOException{
        String timestamp = LocalDateTime.now().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        CompanyDTO company = objectMapper.readValue(companyJson, CompanyDTO.class);

        File image = storeFile(file, company.getName() + "-" + timestamp);
        company.setImagePath(image.getPath().split("/")[image.getPath().split("/").length -1]);
        return mapper.asDTO(repository.save(mapper.fromDTO(company)));
    }

    public CompanyDTO edit(CompanyDTO updatedCompany, MultipartFile file) throws IOException {
        if (updatedCompany.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        hasAuthorization(updatedCompany.getId());

        Company company = repository.findById(updatedCompany.getId()).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateCompany(updatedCompany, company);
        if (file != null){
            editFile(file, company);
        }
        return mapper.asDTO(repository.save(company));
    }

    public void delete(Long id) throws IOException {
        hasAuthorization(id);
        Company companyToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        try {
            Files.delete(Paths.get(absolutePath + companyToDelete.getImagePath()));
        } catch ( NoSuchFileException e){
            log.warn("Picture not found while trying to deleting it");
        }
        repository.delete(companyToDelete);
    }

    public void editFile(MultipartFile image, Company company) throws IOException {
        String timestamp = LocalDateTime.now().toString();

        Files.delete(Paths.get(absolutePath + company.getImagePath()));

        Path path = storeFile(image, company.getName() + "-" +timestamp).toPath();
        company.setImagePath(path.toString().split(absolutePath)[1]);
    }

    public Path getFile(Long companyId) {
        Company company = repository.findById(companyId).orElseThrow(() -> new BadRequestAlertException("company not found" , "COMPANY", " id doesn't exist"));
        return Paths.get(company.getImagePath());
    }

    private File storeFile(MultipartFile image, String timestamp) throws IOException{
        if (!Objects.equals(image.getContentType(), "image/png")) {
            throw new BadRequestAlertException("file type isn't a png ", "IMAGE", " wrong image type");
        }
        try {
            byte[] file = image.getBytes();
            Path path = Paths.get(absolutePath + directoryPath + timestamp + ".png");
            return Files.write(path, file).toFile();
        } catch (IOException e){
            throw new CompanyPictureNotFoundException("temp picture not found", "");
        }
    }
}
