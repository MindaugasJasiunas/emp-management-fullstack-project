package com.example.demo.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    public static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 3;
    public static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService(){
        // initialize cache
        super();
        loginAttemptCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)  //expire cache after 15 minutes
                .maximumSize(100)  // maximum entries for cache
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username){
        // remove user from the cache after successful login
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username){
        // add user after first unsuccessful login (if not already in)
        int attempts = 0;
        try{
            attempts = loginAttemptCache.get(username) + ATTEMPT_INCREMENT;
        }catch (ExecutionException e){
            attempts = 1;
        }
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try{
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        }catch (ExecutionException e){
            return false;
        }
    }
}
