package com.putracode.baeldung.security.registration.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan({ "com.putracode.baeldung.security.registration.task" })
public class SpringTaskConfig {
}
