package com.putracode.baeldung.security.registration.security;

import com.putracode.baeldung.security.registration.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class MyCustomLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy=new DefaultRedirectStrategy();
    @Autowired ActiveUserStore activeUserStore;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        addWelcomeCookie(getUsername(authentication),httpServletResponse);
        redirectStrategy.sendRedirect(httpServletRequest,httpServletResponse,"/homepage.html?user="+authentication.getName());
        final HttpSession session=httpServletRequest.getSession(false);
        if(session!=null){
            session.setMaxInactiveInterval(30*60);
            String username;
            if(authentication.getPrincipal() instanceof User){
                username=((User)authentication.getPrincipal()).getEmail();
            }else{
                username=authentication.getName();
            }
            LoggedUser user=new LoggedUser(username,activeUserStore);
            session.setAttribute("user",user);

        }
        clearAuthenticationAttributes(httpServletRequest);
    }
    protected void clearAuthenticationAttributes(final HttpServletRequest request){
        final HttpSession session= request.getSession(false);
        if(session==null){
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
    private String getUsername(final Authentication authentication){
        return ((User)authentication.getPrincipal()).getFirstName();
    }
    private void addWelcomeCookie(final String user,final HttpServletResponse response){
        Cookie welcomeCookie=getWelcomeCookie(user);
        response.addCookie(welcomeCookie);
    }
    private Cookie getWelcomeCookie(final String user){
        Cookie welcomeCooke=new Cookie("Welcome",user);
        welcomeCooke.setMaxAge(60*60*24*30);
        return welcomeCooke;
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}
