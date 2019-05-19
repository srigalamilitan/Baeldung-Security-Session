package com.putracode.baeldung.security.registration.security.google2fa;

import com.putracode.baeldung.security.registration.security.CustomRememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    private static final long serialVersionUID=1L;
    private final String verificationCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request){
        super(request);
        verificationCode=request.getParameter("code");
    }
    public String getVerificationCode(){
        return verificationCode;
    }

}
