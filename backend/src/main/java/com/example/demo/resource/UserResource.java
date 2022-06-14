package com.example.demo.resource;

import com.example.demo.domain.User;
import com.example.demo.exception.domain.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserResource extends ExceptionHandling {  // ExceptionHandling class will be used when exception occurs (looks for handler)
    @Autowired
    UserService userService;

    @GetMapping("/error")
    public String testErrorHandling() throws UserDisabledException {
        throw new UserDisabledException("User with id X and username X is disabled");
    }


    // + CRUD stuff (+ security config for it)

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @RequestMapping(value = "/{publicId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public User getUserByPublicId(@PathVariable("publicId") String publicId) throws UserNotFoundException {
        return userService.getUserByPublicId(publicId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.createUser(user);
    }

    @RequestMapping(value = "/{publicId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    public User updateUser(@PathVariable("publicId") String publicId, @Valid @RequestBody User user, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.updateUser(user, publicId);
    }

    @PreAuthorize("hasAuthority('canTest')")
    @RequestMapping(value = "/{publicId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT) //204
    public void deleteUser(@PathVariable("publicId") String publicId) throws UserNotFoundException{
        try{
            userService.deleteUser(publicId);
        }catch(EmptyResultDataAccessException e){
            //prevent error if ID doesn't exist
        }
    }




}
