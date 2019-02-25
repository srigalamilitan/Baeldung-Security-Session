package com.putracode.baeldung.security.registration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired private MessageSource messageSource;
    @Autowired private LocaleResolver localeResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login?error=true");
        super.onAuthenticationFailure(request,response,exception);
        final Locale locale=localeResolver.resolveLocale(request);
        String message=messageSource.getMessage("message.badCredentials",null,locale);
        if(exception.getMessage().equalsIgnoreCase("User is disabled")){
            message=messageSource.getMessage("auth.message.disabled",null,locale);
        }else if (exception.getMessage().equalsIgnoreCase("User account has expired")){
            message=messageSource.getMessage("auth.message.expired",null,locale);
        }else if(exception.getMessage().equalsIgnoreCase("blocked")){
            message=messageSource.getMessage("auth.message.blocked",null,locale);
        }
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,message);
    }
}
