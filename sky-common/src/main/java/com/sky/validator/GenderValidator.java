package com.sky.validator;

import com.sky.annotation.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return "1".equals(value) || "0".equals(value);
    }
}
