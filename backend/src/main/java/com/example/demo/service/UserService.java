package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.exception.domain.EmailAlreadyExistsException;
import com.example.demo.exception.domain.UsernameAlreadyExistsException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();
    User createUser(User user) throws Exception;
    Optional<User> getUserByUsername(String username);
    String generateTokenForUser(String username);
}
