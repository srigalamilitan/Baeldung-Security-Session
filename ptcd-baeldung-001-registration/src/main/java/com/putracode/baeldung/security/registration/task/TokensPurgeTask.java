package com.putracode.baeldung.security.registration.task;

import com.putracode.baeldung.security.registration.persistence.dao.PasswordResetTokenRepository;
import com.putracode.baeldung.security.registration.persistence.dao.VerificationTokenRepository;
import com.putracode.baeldung.security.registration.persistence.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.util.Date;

@Service
@Transactional
public class TokensPurgeTask {
    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpired(){
        Date  date=Date.from(Instant.now());
        passwordResetTokenRepository.deleteAllExpiredSince(date);
        verificationTokenRepository.deleteAllExpiredSince(date);
    }
}
