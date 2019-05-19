package com.putracode.baeldung.security.registration.security.google2fa;

import com.putracode.baeldung.security.registration.persistence.dao.UserRepository;
import com.putracode.baeldung.security.registration.persistence.model.User;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        return super.authenticate(authentication);
        final User user =userRepository.findByEmail(authentication.getName());
        if(user==null){
            throw new BadCredentialsException("Invalid Username or Password");
        }
        if(user.isUsing2FA()){
            final String verificationCode=((CustomWebAuthenticationDetails) authentication.getDetails()).getVerificationCode();
            final Totp totp=new Totp(user.getSecret());
            if(!isValidLong(verificationCode) || !totp.verify(verificationCode)){
                throw new BadCredentialsException("Invalid verification code");
            }
        }
        final Authentication result=super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(user,result.getCredentials(),result.getAuthorities());
    }
    private boolean isValidLong(String code){
        try {
            Long.parseLong(code);
        }catch (final NumberFormatException e){
            return false;
        }
        return true;
    }
}
