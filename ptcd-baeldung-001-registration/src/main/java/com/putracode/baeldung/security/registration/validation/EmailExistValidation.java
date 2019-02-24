package com.putracode.baeldung.security.registration.validation;

@SuppressWarnings("serial")
public class EmailExistValidation extends Throwable {
    public EmailExistValidation(final String message) {
        super(message);
    }
}
