package com.example.demo.security.listener;

import com.example.demo.domain.User;
import com.example.demo.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    private final LoginAttemptService loginAttemptService;

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        // add user to login attempt service cache
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof User){
            User user = (User) principal;
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
