package com.mycompany.myapp.config.annotations;


import com.mycompany.myapp.security.AuthoritiesConstants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * For endpoints which needs to be hidden in public API Documentation
 */
@Hidden
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminSecured { }
