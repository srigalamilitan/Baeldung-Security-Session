package com.putracode.baeldung.security.registration.security;

import com.putracode.baeldung.security.registration.persistence.dao.PasswordResetTokenRepository;
import com.putracode.baeldung.security.registration.persistence.model.PasswordResetToken;
import com.putracode.baeldung.security.registration.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;

@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {

    @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public String validatePasswordResetToken(long id, String token) {
        final PasswordResetToken passwordResetToken=passwordResetTokenRepository.findByToken(token);
        if((passwordResetToken==null) || (passwordResetToken.getUser().getId()!=id)){
            return "invalidToken";
        }
        final Calendar cal=Calendar.getInstance();
        if((passwordResetToken.getExpiryDate().getTime()-cal.getTime().getTime()<=0)){
            return "expired";
        }
        final User user=passwordResetToken.getUser();
        final Authentication authentication=new UsernamePasswordAuthenticationToken(user,null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return null;
    }
}
