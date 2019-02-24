package com.putracode.baeldung.security.registration.security;

import com.putracode.baeldung.security.registration.persistence.dao.UserRepository;
import com.putracode.baeldung.security.registration.persistence.model.Privilege;
import com.putracode.baeldung.security.registration.persistence.model.Role;
import com.putracode.baeldung.security.registration.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service("userdetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {
    @Autowired private UserRepository userRepository;
    @Autowired private LoginAttemptService loginAttemptService;
    @Autowired private HttpServletRequest request;

    public MyUserDetailsService(){
        super();
    }
    //API

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final String ip=getClientIP();
        if(loginAttemptService.isBlocked(ip)){
            throw  new RuntimeException("blocked");
        }
        try {
            final User user=userRepository.findByEmail(email);
            if(user==null){
                throw new UsernameNotFoundException("No User Found with Username : "+email);
            }
            return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),user.isEnabled(),true,true,true,getAuthorities(user.getRoles()));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    private final List<String> getPrivileges(final Collection<Role> roles){
        final List<String> privileges=new ArrayList<>();
        final List<Privilege> collection=new ArrayList<>();
        for(final Role role:roles){
            collection.addAll(role.getPrivileges());
        }
        for(final Privilege privilege:collection){
            privileges.add(privilege.getName());
        }
        return privileges;
    }
    private final List<GrantedAuthority> grantedAuthorities(final List<String> privileges){
        final List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        for(final String privilege:privileges){
            grantedAuthorities.add(new SimpleGrantedAuthority(privilege));
        }
        return grantedAuthorities;
    }
    private final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles){
        return grantedAuthorities(getPrivileges(roles));
    }
    private final String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
