package com.putracode.baeldung.security.registration.captcha;

public interface ICaptchaService {
    void processResponse(final String response) ;//throws ReCaptchaInvalidException;
    String getReCaptchaSite();
    String getReCaptchaSecret();
}
