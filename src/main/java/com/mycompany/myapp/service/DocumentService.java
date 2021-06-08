package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Document;
import com.mycompany.myapp.domain.DocumentType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.DocumentRepository;
import com.mycompany.myapp.repository.DocumentTypeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.DocumentDTO;
import com.mycompany.myapp.service.mapper.DocumentMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentService {
    private static final String ENTITY_NAME = "document";

    private final DocumentRepository repository;
    private final DocumentMapper mapper;
    private final MinioService minioService;
    private final DocumentTypeRepository documentTypeRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public DocumentDTO getById(Long id) throws MinioException {
        Document document = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("document doesn't exist", ENTITY_NAME, "id doesn't exist"));
        return mapper.asDTO(document);
    }

    public String getByUrl(Long id) throws MinioException {
        Document document = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("document doesn't exist", ENTITY_NAME, "id doesn't exist"));
        return minioService.getFileUrl(document.getFilePath(),Document.getBucket());
    }

    public List<DocumentDTO> getDocuments(Long userId) {
        List<Document> documents = repository.findAllByUserId(userId);
        return mapper.asListDTO(documents);
    }

    public DocumentDTO create(MultipartFile file, Long typeId) throws MinioException {
        DocumentType documentType = documentTypeRepository.findById(typeId).orElseThrow(() -> new BadRequestAlertException("document type doesn't exist", ENTITY_NAME, "id doesn't exist"));
        User user = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist")))
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "id doesn't exist"));
        String filePath = String.format("%s/%s-%s", user.getId(), documentType.getName(), LocalDateTime.now());
        Document document = new Document(documentType, filePath, user);
        minioService.uploadFile(file, filePath, Document.getBucket());
        Document returnedDocument = repository.save(document);
        System.out.println(returnedDocument);
        DocumentDTO returnedDocumentDTO = mapper.asDTO(returnedDocument);
        System.out.println(returnedDocumentDTO);
        return returnedDocumentDTO;
    }

    public DocumentDTO edit(MultipartFile file, Long id) throws MinioException {
        Document document = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("document doesn't exist", ENTITY_NAME, "id doesn't exist"));
        minioService.deleteFile(document.getFilePath(), Document.getBucket());
        String filePath = String.format("%s/%s-%s", document.getId(), document.getType().getName(), LocalDateTime.now());
        minioService.uploadFile(file, filePath, Document.getBucket());
        document.setFilePath(filePath);
        return mapper.asDTO(repository.save(document));
    }

    public void delete(Long id) throws MinioException {
        Document document = repository.findById(id).orElseThrow(() -> new BadRequestAlertException("document doesn't exist", ENTITY_NAME, "id doesn't exist"));
        minioService.deleteFile(document.getFilePath(), Document.getBucket());
        repository.delete(document);
    }


}
