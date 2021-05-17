package com.mycompany.myapp.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import static com.mycompany.myapp.config.Constants.DOCUMENT_BUCKET;

@Data
@Entity
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@Table(name = "document")
public class Document {
    private static String bucket = DOCUMENT_BUCKET;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private DocumentType type;

    @NotBlank
    private String filePath;

    @ManyToOne
    private User user;

    public Document(DocumentType type, String filePath, User user) {
        this.type = type;
        this.filePath = filePath;
        this.user = user;
    }

    public static String getBucket() {
        return bucket;
    }

    public DocumentType getType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
