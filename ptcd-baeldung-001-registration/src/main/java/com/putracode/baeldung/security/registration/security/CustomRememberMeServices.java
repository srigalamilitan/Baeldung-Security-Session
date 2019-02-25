package com.putracode.baeldung.security.registration.security;

import com.putracode.baeldung.security.registration.persistence.dao.UserRepository;
import com.putracode.baeldung.security.registration.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class CustomRememberMeServices extends PersistentTokenBasedRememberMeServices {
    @Autowired private UserRepository userRepository;
    private GrantedAuthoritiesMapper authoritiesMapper=new NullAuthoritiesMapper();
    private AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource=new WebAuthenticationDetailsSource();
    private PersistentTokenRepository tokenRepository=new InMemoryTokenRepositoryImpl();
    private String key;

    public CustomRememberMeServices(String key, UserDetailsService userDetailsService,PersistentTokenRepository tokenRepository){
        super(key,userDetailsService,tokenRepository);
        this.tokenRepository=tokenRepository;
        this.key=key;
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        String userName=((User) successfulAuthentication.getPrincipal()).getEmail();
        logger.debug("Creating Persistent login for user "+userName);
        PersistentRememberMeToken persistentRememberMeToken= new PersistentRememberMeToken(userName,generateSeriesData(),generateTokenData(),new Date());
        try{
            tokenRepository.createNewToken(persistentRememberMeToken);
            addCookie(persistentRememberMeToken,request,response);
        }catch (Exception e){
            logger.error("Failed to save persistence token : ",e);
        }
    }
    private void addCookie(PersistentRememberMeToken token,HttpServletRequest request,HttpServletResponse response){
        setCookie(new String[]{token.getSeries(),token.getTokenValue()},getTokenValiditySeconds(),request,response);
    }

    @Override
    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
        User auser=userRepository.findByEmail(user.getUsername());
        RememberMeAuthenticationToken auth= new RememberMeAuthenticationToken(key,auser,authoritiesMapper.mapAuthorities(user.getAuthorities()));
        auth.setDetails(authenticationDetailsSource.buildDetails(request));
        return auth;
    }
}
