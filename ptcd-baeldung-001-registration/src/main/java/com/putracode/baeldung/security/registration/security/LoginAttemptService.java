package com.putracode.baeldung.security.registration.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPT=10;
    private LoadingCache<String,Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        this.attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1,TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String s) throws Exception {
                return 0;
            }
        });
    }
    public void loginSucceded(final String key){
        attemptsCache.invalidate(key);
    }
    public void loginFailed(final String key){
        int attempts=0;
        try{
            attempts=attemptsCache.get(key);
        }catch (final ExecutionException e){
            attempts=0;
        }
        attempts++;
        attemptsCache.put(key,attempts);
    }
    public boolean isBlocked(final String key){
        try{
            return attemptsCache.get(key)>=MAX_ATTEMPT;
        }catch (final ExecutionException e){
            return false;
        }
    }

}
