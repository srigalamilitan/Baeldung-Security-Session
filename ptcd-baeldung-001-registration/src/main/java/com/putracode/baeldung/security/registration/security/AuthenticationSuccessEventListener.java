package com.putracode.baeldung.security.registration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired private HttpServletRequest request;
    @Autowired private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader==null){
            loginAttemptService.loginSucceded(request.getRemoteAddr());
        }else
        {
            loginAttemptService.loginSucceded(xfHeader.split(",")[0]);
        }

    }
}
