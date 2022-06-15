package com.example.demo.resource;

import com.example.demo.HttpAuthLoginRequest;
import com.example.demo.HttpAuthRegisterRequest;
import com.example.demo.domain.User;
import com.example.demo.exception.domain.*;
import com.example.demo.service.UserService;
import com.example.demo.utility.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
public class AuthenticationResource {
    private final UserService userService;

    public AuthenticationResource(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User registerNewUser(@Valid @RequestBody HttpAuthRegisterRequest register, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }

        // map to user class
        User user = UserMapper.INSTANCE.HttpAuthRegisterRequestToUser(register);

        return userService.createUser(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> getTokenForUser(@Valid @RequestBody HttpAuthLoginRequest request, BindingResult result) throws Exception {
        // check if request has errors
        if (result.hasErrors()) {
            throw new BadCredentialsException("");
        }
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        // check if user exists & generate JWT for that user
        User user;
        try{
            user = userService.getUserByUsername(request.username());
        }catch (UserNotFoundException e){
            throw new BadCredentialsException("");
        }

        // check if not locked or inactive
        if(!user.isNotLocked()) {
            throw new LockedException("");
        }else if(!user.isActive()){
            throw new UserDisabledException("");
        }

        // validate
        try{
            userService.validateLoginAttempt(user);
        }catch (ExecutionException e){
            System.err.println(e.getMessage());
        }

        if(!userService.passwordMatches(request.username(), request.password())){
            throw new BadCredentialsException("");
        }

        String token = userService.generateTokenForUser(user.getUsername());

        if(token == null){
            // error - user cannot be found in DB & therefore cannot generate JWT token
            throw new Exception("");
        }
        HttpHeaders headers= new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
        return ResponseEntity.ok().headers(headers).build();
    }
}
