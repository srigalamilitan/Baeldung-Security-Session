package com.putracode.baeldung.security.registration.captcha;

import com.putracode.baeldung.security.registration.web.error.ReCaptchaInvalidException;
import com.putracode.baeldung.security.registration.web.error.ReCaptchaUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Service("captchaService")
public class CaptchaService implements ICaptchaService {
    private final static Logger LOGGER=LoggerFactory.getLogger(CaptchaService.class);
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private CaptchaSettings captchaSettings;
    @Autowired
    private ReCaptchaAttemptService reCaptchaAttemptService;

    @Autowired
    private RestOperations restTemplate;

    private static final Pattern RESPONSE_PATTERN=Pattern.compile("[A-Za-z0-9_-]+");


    @Override
    public void processResponse(String response) {
        LOGGER.debug("Attempting to validate response {}", response);
        if(reCaptchaAttemptService.isBlocked(getClientIP())){
            throw new ReCaptchaInvalidException("Client exceeded maximum number or failed attemps");
        }
        if (!responseSanity(response)){
            throw new ReCaptchaInvalidException("Response Charecter contains invalid character");
        }
        final URI verifyURI=URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", getReCaptchaSecret(), response, getClientIP()));
        try {
            final GoogleResponse googleResponse=restTemplate.getForObject(verifyURI,GoogleResponse.class);
            LOGGER.debug("Google Response : {}",googleResponse.toString());
            if(!googleResponse.isSuccess()){
                if(googleResponse.hasClientError()){
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
            }

        }catch (RestClientException rce){
            throw new ReCaptchaUnavailableException("Registration unavailable at this time.  Please try again later.", rce);
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }
    private boolean responseSanity(final String response){
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public String getReCaptchaSite() {
        return captchaSettings.getSite();
    }

    @Override
    public String getReCaptchaSecret() {
        return captchaSettings.getSecret();
    }
    private String getClientIP(){
        final String xfheader=request.getHeader("X-Forwarded-For");
        if (xfheader==null)return request.getRemoteAddr();

        return xfheader.split(",")[0];
    }
}
