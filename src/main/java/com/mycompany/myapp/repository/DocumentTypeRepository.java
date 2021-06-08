package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
}
