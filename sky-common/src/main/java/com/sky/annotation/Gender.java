package com.sky.annotation;

import com.sky.validator.GenderValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {GenderValidator.class})
@Target(FIELD)
@Retention(RUNTIME)
public @interface Gender {
    String message() default "{gender.illegal.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
