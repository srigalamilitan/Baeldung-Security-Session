package com.putracode.baeldung.security.registration.web.error;

import com.putracode.baeldung.security.registration.web.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sun.net.www.content.text.Generic;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    private MessageSource message;
    public RestResponseEntityExceptionHandler() {
        super();
    }
    // 400
    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final BindingResult result = ex.getBindingResult();
        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("400 Status Code",ex);
        final BindingResult result=ex.getBindingResult();
        final GenericResponse bodyOfResponse=new GenericResponse(result.getAllErrors(),"Invalid "+result.getObjectName());
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.BAD_REQUEST,request);
    }
    @ExceptionHandler({InvalidOldPasswordException.class})
    public ResponseEntity<Object> handleInvalidOldPassword(final RuntimeException ex,final WebRequest webRequest){
        logger.error("400 Status Code",ex);
        final GenericResponse bodyOfResponse=new GenericResponse(message.getMessage("message.invalidOldPassword",null,webRequest.getLocale()),"InvalidOldPassword");
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.BAD_REQUEST,webRequest);
    }
    @ExceptionHandler({ReCaptchaInvalidException.class})
    public ResponseEntity<Object> handleReCaptchaInvalid(final RuntimeException ex,final WebRequest request){
        logger.error("400 Status Code",ex);
        final GenericResponse bodyOfResponse=new GenericResponse(message.getMessage("message.invalidReCaptcha",null,request.getLocale()),"InvalidReCaptcha");
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.BAD_REQUEST,request);
    }
    //404
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException ex, final WebRequest request){
        logger.error("404 Status Code",ex);
        final GenericResponse bodyOfResponse=new GenericResponse(message.getMessage("message.userNotFound",null,request.getLocale()),"UserNotFound");
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.NOT_FOUND,request);
    }
    //409
    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<Object> handleUserAlreadyExists(final RuntimeException ex, final WebRequest request){
        logger.error("409 Status Code",ex);
        final GenericResponse bodyOfResponse=new GenericResponse(message.getMessage("message.regError",null,request.getLocale()),"UserAlreadyExists");
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.CONFLICT,request);
    }
    //500
    @ExceptionHandler({MailAuthenticationException.class})
    public ResponseEntity<Object> handleMail(final RuntimeException ex,final WebRequest request){
        logger.error("500 Status Code",ex);
        final GenericResponse bodyOfResponse=new GenericResponse(message.getMessage("message.mail.config.error",null,request.getLocale()),"MailError");
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR,request);
    }
    @ExceptionHandler({ReCaptchaUnavailableException.class})
    public ResponseEntity<Object> handleReCaptchaUnavailable(final RuntimeException ex,final WebRequest request){
        logger.error("500 Status Code",ex);
        final GenericResponse bodyOfResponse=new GenericResponse(message.getMessage("message.unavailableReCaptcha",null,request.getLocale()),"UnavailableReCaptcha");
        return handleExceptionInternal(ex,bodyOfResponse,new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR,request);
    }
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(message.getMessage("message.error", null, request.getLocale()), "InternalError");
        return new ResponseEntity<Object>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
