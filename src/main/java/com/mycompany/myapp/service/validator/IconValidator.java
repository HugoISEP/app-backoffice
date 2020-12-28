package com.mycompany.myapp.service.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.mycompany.myapp.config.Constants;

import java.util.Arrays;

public class IconValidator implements
    ConstraintValidator<IconValid, String> {

    @Override
    public void initialize(IconValid icon) {
    }

    @Override
    public boolean isValid(String iconField,
                           ConstraintValidatorContext cxt) {
        return Arrays.asList(Constants.LIST_ICONS).contains(iconField);
    }

}
