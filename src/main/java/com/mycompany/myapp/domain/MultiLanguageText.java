package com.mycompany.myapp.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Embeddable
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
class MultiLanguageText implements Serializable {

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> texts = new HashMap<>();

    MultiLanguageText(Map<String, String> texts) {
        this.texts = texts;
    }

    @Override
    public String toString() {
        return texts.getOrDefault("FR", "");
    }

}
