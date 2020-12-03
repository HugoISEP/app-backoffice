package com.mycompany.myapp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  Language {
    ENGLISH("EN"),
    FRENCH("FR");

    private final String value;
}
