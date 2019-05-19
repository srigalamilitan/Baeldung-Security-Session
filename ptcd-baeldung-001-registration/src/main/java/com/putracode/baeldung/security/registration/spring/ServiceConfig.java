package com.putracode.baeldung.security.registration.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "com.putracode.baeldung.security.registration.service" })
public class ServiceConfig {
}