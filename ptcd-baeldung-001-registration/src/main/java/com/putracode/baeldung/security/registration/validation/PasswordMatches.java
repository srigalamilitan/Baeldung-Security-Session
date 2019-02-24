package com.putracode.baeldung.security.registration.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "Password don't Mathces";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};

}
