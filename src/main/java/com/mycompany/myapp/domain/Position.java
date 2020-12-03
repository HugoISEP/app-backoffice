package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.mycompany.myapp.config.Constants.DEFAULT_LANGUAGE_TRANSLATION;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Data
@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "duration", nullable = false)
    private int duration;

    @NotNull
    @Column(name = "places_number", nullable = false)
    private int placesNumber;

    @NotNull
    @Column(name = "remuneration", nullable = false)
    private float remuneration;

    @Transient
    private String description;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> descriptionTranslations = new HashMap<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "positions")
    private Mission mission;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Column(name = "status", nullable = false)
    private boolean status = false;

    @ManyToOne
    @JsonIgnoreProperties(value = "positions")
    private JobType jobType;

    public String getDescription() {
        if (Arrays.asList(Language.values()).toString().contains(LocaleContextHolder.getLocale().getLanguage().toUpperCase())){
            return descriptionTranslations.get(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        } else {
            return descriptionTranslations.get(DEFAULT_LANGUAGE_TRANSLATION);
        }
    }
}
