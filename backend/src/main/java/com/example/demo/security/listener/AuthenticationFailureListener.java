package com.example.demo.security.listener;

import com.example.demo.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener {
    private final LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event){
        // add user to login attempt service cache
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String){
            String username = (String) principal;
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
