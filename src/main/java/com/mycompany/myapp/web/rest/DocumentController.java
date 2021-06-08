package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.DocumentService;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.DocumentDTO;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;
    private final UserService userService;

    @GetMapping("/{id}")
    public DocumentDTO getById(@PathVariable Long id) throws MinioException {
        return service.getById(id);
    }

    @GetMapping("/{id}/url")
    public String getUrlById(@PathVariable Long id) throws MinioException {
        return service.getByUrl(id);
    }

    @GetMapping
    public List<DocumentDTO> getDocuments() {
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new ResourceNotFoundException("User not found", "user", "id doesn't exist"));
        return service.getDocuments(user.getId());
    }

    @PostMapping()
    public DocumentDTO create(@RequestBody MultipartFile file,
                              @RequestParam Long userId,
                              @RequestParam Long typeId) throws MinioException {
        return service.create(file, userId, typeId);
    }

    @PutMapping("/{id}")
    public DocumentDTO edit(@RequestBody MultipartFile file, @PathVariable Long id) throws MinioException {
        return service.edit(file, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws MinioException {
        service.delete(id);
    }
}
