package com.putracode.baeldung.security.registration.web.util;


import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class GenericResponse {
    private String message;
    private String error;

    public GenericResponse(final String message){
        super();
        this.message=message;
    }
    public GenericResponse(final String message, final String error){
        super();
        this.error=error;
        this.message=message;
    }
    public GenericResponse(List<ObjectError> allError, String error){
        this.error=error;
        String temp=allError.stream().map(e->{
            if(e instanceof FieldError){
                return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            } else {
                return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            }
        }).collect(Collectors.joining(","));
        this.message=temp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}
