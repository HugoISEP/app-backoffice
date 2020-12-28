package com.mycompany.myapp.service.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IconValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IconValid {
    String message() default "Invalid icon name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
