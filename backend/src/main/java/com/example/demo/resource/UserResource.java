package com.example.demo.resource;

import com.example.demo.domain.User;
import com.example.demo.exception.domain.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResource extends ExceptionHandling {  // ExceptionHandling class will be used when exception occurs (looks for handler)
    @Autowired
    UserService userService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<User> allUsers(){
        return Collections.emptyList();
    }

    @GetMapping("/home")
    @ResponseStatus(HttpStatus.OK)
    public String testNonAuthEndpoint(){
        return "non auth endpoint";
    }

    @GetMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public String testAuthEndpoint(){
        return "auth endpoint";
    }

    @GetMapping("/error")
    public String testErrorHandling() throws UserDisabledException {
        throw new UserDisabledException("User with id X and username X is disabled");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User registerNewUser(@Valid @RequestBody User user, BindingResult result) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, UserValidationException {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.createUser(user);
    }

}
