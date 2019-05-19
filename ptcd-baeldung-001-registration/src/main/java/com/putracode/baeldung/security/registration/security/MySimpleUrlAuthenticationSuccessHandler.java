package com.putracode.baeldung.security.registration.security;

import com.putracode.baeldung.security.registration.persistence.model.User;
import com.putracode.baeldung.security.registration.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@Component("myAuthenticationSuccessHandler")
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger=LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy=new DefaultRedirectStrategy();

    @Autowired ActiveUserStore activeUserStore;
    @Autowired
    DeviceService deviceService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException{
        handle(httpServletRequest,httpServletResponse,authentication);
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
        loginNotification(authentication,httpServletRequest);

    }
    private void loginNotification(Authentication authentication,HttpServletRequest request){
        try{
            if(authentication.getPrincipal() instanceof User){
                deviceService.verifyDevice(((User)authentication.getPrincipal()),request);
            }
        }catch (Exception e){
            logger.error("An Error occurred while verifying device or location",e);
            throw new RuntimeException(e);
        }
    }
    protected void handle(final HttpServletRequest request, final HttpServletResponse response,final Authentication authentication) throws IOException {
        final String targetUrl=determineTargetUrl(authentication);
        if(response.isCommitted()){
            logger.debug("Response has already been committed. Unable to redirect to "+targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request,response,targetUrl);
    }
    protected String determineTargetUrl(final Authentication authentication)  {
        boolean isUser=false;
        boolean isAdmin=false;
        final Collection<? extends GrantedAuthority> authorities=authentication.getAuthorities();
        for( final GrantedAuthority grantedAuthority:authorities){
            if(grantedAuthority.getAuthority().equalsIgnoreCase("READ_PRIVILEGE")){
                isUser=true;
            }else if(grantedAuthority.getAuthority().equalsIgnoreCase("WRITE_PRIVILEGE")){
                isAdmin=true;
                isUser=true;
                break;
            }
        }
        if(isUser){
            String username;
            if(authentication.getPrincipal() instanceof User){
                username=((User)authentication.getPrincipal()).getEmail();
            }else
            {
                username=authentication.getName();
            }
            return "/homepage.html?user="+username;
        }else if( isAdmin){
            return "/console.html";
        }else {
            throw new IllegalStateException();
        }
    }
    protected  void clearAuthenticationAttributes(final HttpServletRequest request){
        final HttpSession session=request.getSession(false);
        if(session==null){
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}
