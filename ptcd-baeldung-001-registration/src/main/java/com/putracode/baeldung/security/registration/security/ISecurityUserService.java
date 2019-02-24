package com.putracode.baeldung.security.registration.security;

public interface ISecurityUserService {
    String validatePasswordResetToken(long id, String token);
}
