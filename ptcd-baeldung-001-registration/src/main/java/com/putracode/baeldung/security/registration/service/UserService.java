package com.putracode.baeldung.security.registration.service;

import com.putracode.baeldung.security.registration.persistence.dao.PasswordResetTokenRepository;
import com.putracode.baeldung.security.registration.persistence.dao.RoleRepository;
import com.putracode.baeldung.security.registration.persistence.dao.UserRepository;
import com.putracode.baeldung.security.registration.persistence.dao.VerificationTokenRepository;
import com.putracode.baeldung.security.registration.persistence.model.PasswordResetToken;
import com.putracode.baeldung.security.registration.persistence.model.User;
import com.putracode.baeldung.security.registration.persistence.model.VerificationToken;
import com.putracode.baeldung.security.registration.web.dto.UserDto;
import com.putracode.baeldung.security.registration.web.error.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {
    @Autowired private UserRepository userRepository;
    @Autowired private VerificationTokenRepository tokenRepository;
    @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoleRepository roleRepository;
    @Autowired private SessionRegistry sessionRegistry;

    public static final String TOKEN_INVALID="invalidToken";
    public static final String TOKEN_EXPIRED="expired";
    public static final String TOKEN_VALID="valid";
    public static final String QR_PREFIX="https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static final String APP_NAME="SpringRegistration";

    @Override
    public User getUser(String verificationToken) {
        final VerificationToken token=tokenRepository.findByToken(verificationToken);
        if(token!=null){
            return token.getUser();
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(final User user) {
        final VerificationToken verificationToken=tokenRepository.findByUser(user);
        if(verificationToken!=null){
            tokenRepository.delete(verificationToken);
        }
        final PasswordResetToken passwordResetToken=passwordResetTokenRepository.findByUser(user);
        if(passwordResetToken!=null){
            passwordResetTokenRepository.delete(passwordResetToken);
        }
        userRepository.delete(user);
    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {
        final VerificationToken myToken=new VerificationToken(token,user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(String token) {
        VerificationToken vToken=tokenRepository.findByToken(token);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken=tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException {
        if(emailExists(accountDto.getEmail())){
            throw new UserAlreadyExistException("There is an Account with that email address: "+accountDto.getEmail());
        }
        final User user=new User();
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setUsing2FA(accountDto.isUsing2FA());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }
    private boolean emailExists(final String email){
        return userRepository.findByEmail(email)!=null;
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        final PasswordResetToken myToken=new PasswordResetToken(token,user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token).getUser();
    }

    @Override
    public Optional<User> getUserByID(long id) {
        return userRepository.findById(id);
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String password) {
        return passwordEncoder.matches(password,user.getPassword());
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken=tokenRepository.findByToken(token);
        if(verificationToken==null){
            return TOKEN_INVALID;
        }
        final User user=verificationToken.getUser();
        final Calendar cal=Calendar.getInstance();
        if((verificationToken.getExpiryDate().getTime()-cal.getTime().getTime())<=0){
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }
        user.setEnabled(true);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public String generateQRUrl(User user)throws UnsupportedEncodingException {
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, user.getEmail(), user.getSecret(), APP_NAME), "UTF-8");
    }

    @Override
    public User updateUser2FA(boolean use2FA) {
        final Authentication curAuth=SecurityContextHolder.getContext().getAuthentication();
        User cuUser=(User) curAuth.getPrincipal();
        cuUser.setUsing2FA(use2FA);
        cuUser=userRepository.save(cuUser);
        final Authentication auth=new UsernamePasswordAuthenticationToken(cuUser,cuUser.getPassword(),curAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return cuUser;
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream().filter((u)-> !sessionRegistry.getAllSessions(u,false).isEmpty())
                .map(o->{
                    if(o instanceof  User){
                        return ((User)o).getEmail();
                    }else{
                        return o.toString();
                    }
                }).collect(Collectors.toList());
    }
}
