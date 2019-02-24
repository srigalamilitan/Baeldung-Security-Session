package com.putracode.baeldung.security.registration.spring;

import com.putracode.baeldung.security.registration.security.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ActiveUserStore activeUserStore(){
        return new ActiveUserStore();
    }
}
