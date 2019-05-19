package com.putracode.baeldung.security.registration.registration.listener;

import com.putracode.baeldung.security.registration.persistence.model.User;
import com.putracode.baeldung.security.registration.registration.OnRegistrationCompleteEvent;
import com.putracode.baeldung.security.registration.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private IUserService service;
    @Autowired
    private MessageSource source;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent onRegistrationCompleteEvent) {
        this.confirmRegistration(onRegistrationCompleteEvent);
    }
    private void confirmRegistration(final OnRegistrationCompleteEvent event){
        final User user=event.getUser();
        final String token= UUID.randomUUID().toString();
        service.createVerificationTokenForUser(user,token);
        final SimpleMailMessage simpleMailMessage=constructEmailMessage(event,user,token);
        mailSender.send(simpleMailMessage);
    }
    private final SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token){
        final String recipientAddress=user.getEmail();
        final String subject="Registration Confirmation";
        final String confirmationURL=event.getAppUrl()+"/registrationConfirm.html?token="+token;
        final String message=source.getMessage("message.regSucc",null,event.getLocale());
        final SimpleMailMessage email=new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message+" \r\n"+confirmationURL);
        email.setFrom(environment.getProperty("support.email"));

    return email;
    }
}
