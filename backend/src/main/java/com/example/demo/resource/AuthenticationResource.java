package com.example.demo.resource;

import com.example.demo.HttpAuthRequest;
import com.example.demo.HttpResponse;
import com.example.demo.domain.User;
import com.example.demo.exception.domain.EmailAlreadyExistsException;
import com.example.demo.exception.domain.UserNotFoundException;
import com.example.demo.exception.domain.UserValidationException;
import com.example.demo.exception.domain.UsernameAlreadyExistsException;
import com.example.demo.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public class AuthenticationResource {
    private final UserService userService;

    public AuthenticationResource(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User registerNewUser(@Valid @RequestBody User user, BindingResult result) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, UserValidationException {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.createUser(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> getTokenForUser(@Valid @RequestBody HttpAuthRequest request, BindingResult result) throws Exception {
        // check if request has errors
        if (result.hasErrors()) {
            throw new BadCredentialsException("");
        }
        // check if user exists & generate JWT for that user
        Optional<User> user = userService.getUserByUsername(request.username());
        if(user.isEmpty()){
            throw new BadCredentialsException("");
        }

        String token = userService.generateTokenForUser(user.get().getUsername());

        if(token == null){
            // error - user cannot be found in DB & therefore cannot generate JWT token
            throw new Exception("");
        }
        HttpHeaders headers= new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
        return ResponseEntity.ok().headers(headers).build();
    }
}
