package com.putracode.baeldung.security.registration.validation;

import com.putracode.baeldung.security.registration.web.dto.UserDto;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches,Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        UserDto userDto=(UserDto)o;

        return userDto.getPassword().equals(userDto.getMatchingPassword());
    }
}
