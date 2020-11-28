package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.dto.CompanyDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.CompanyMapper;
import com.mycompany.myapp.service.view.CompanyDetailsView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@Transactional
public class CompanyService {

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository repository;
    private final CompanyMapper mapper;
    private final UserService userService;

    public CompanyService(CompanyRepository repository, CompanyMapper mapper, UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
    }

    public void hasAuthorization(Long id){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        Company company = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "id doesn't exist"));
        if(!user.getAuthorities().contains(AuthoritiesConstants.ADMIN) && !user.getCompany().getId().equals(company.getId())){
            throw new AccessDeniedException("user not authorize");
        }
    }

    public CompanyDetailsView getCompanyFromCurrentUser(){
        UserDTO user = userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new BadRequestAlertException("user not found", ENTITY_NAME, " id exists"));
        return repository.findCompanyFromCurrentUser(user.getId());
    }

    public Page<CompanyDetailsView> getAllPaginated(Pageable pageable, String searchTerm){
        return repository.findAllPaginated(pageable, searchTerm);
    }

    public CompanyDTO create(CompanyDTO company){
        return mapper.asDTO(repository.save(mapper.fromDTO(company)));
    }

    public CompanyDTO edit(CompanyDTO updatedCompany){
        if (updatedCompany.getId() == null) {
            throw new BadRequestAlertException("Cannot edit ", ENTITY_NAME, " id doesn't exist");
        }
        Company company = repository.findById(updatedCompany.getId()).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        mapper.updateCompany(updatedCompany, company);
        return mapper.asDTO(company);
    }

    public void delete(Long id){
        Company companyToDelete = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("company doesn't exist", ENTITY_NAME, "id doesn't exist"));
        repository.delete(companyToDelete);
    }

    public void storeInFileSystem(MultipartFile image, Long companyId) throws IOException {
        if (!image.getContentType().equals("image/png")) {
            throw new BadRequestAlertException("file type isn't a png ", "IMAGE", " wrong image type");
        }

        Company company = repository.findById(companyId).orElseThrow(() -> new BadRequestAlertException("company not found" , "COMPANY", " id doesn't exist"));
        String currentPath = Paths.get("").toAbsolutePath().toString() + "/src/main/resources/images/company/";

        byte[] file = image.getBytes();
        Path path = Paths.get(currentPath + company.getId().toString() + ".png");
        Files.write(path, file);
        company.setImagePath(path.toString());
    }

    public Path getFile(Long companyId) {
        Company company = repository.findById(companyId).orElseThrow(() -> new BadRequestAlertException("company not found" , "COMPANY", " id doesn't exist"));
        return Paths.get(company.getImagePath());
    }
}
