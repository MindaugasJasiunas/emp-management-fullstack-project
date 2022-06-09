package com.example.demo.resource;

import com.example.demo.domain.User;
import com.example.demo.exception.domain.ExceptionHandling;
import com.example.demo.exception.domain.UserDisabledException;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResource extends ExceptionHandling {  // ExceptionHandling class will be used when exception occurs (looks for handler)
    @Autowired
    UserRepository repo;

    @GetMapping("/")
    public List<User> allUsers(){
        return repo.findAll();
    }

    @GetMapping("/home")
    public String testNonAuthEndpoint(){
        return "non auth endpoint";
    }

    @GetMapping("/auth")
    public String testAuthEndpoint(){
        return "auth endpoint";
    }

    @GetMapping("/error")
    public String testErrorHandling() throws UserDisabledException {
        throw new UserDisabledException("User with id X and username X is disabled");
    }

}
