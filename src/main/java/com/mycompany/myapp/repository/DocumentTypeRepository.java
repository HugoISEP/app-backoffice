package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.DocumentType;
import com.mycompany.myapp.service.view.DocumentTypeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocumentTypeRepository extends CrudRepository<DocumentType, Long> {
    List<DocumentTypeView> findAllBy();
}
