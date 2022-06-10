package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.exception.domain.EmailAlreadyExistsException;
import com.example.demo.exception.domain.UsernameAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j

@Service
@Transactional // manage propagation when dealing with 1 transaction
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<User> userOptional = repository.findByUsername(username);
        if(userOptional.isEmpty()){
            log.debug("Username not found: "+username);
            throw new UsernameNotFoundException("User does not exist!");
        }
        User user = userOptional.get();
        user.setLastLoginDate(LocalDate.now());
        // update user
        repository.save(user);
        return new UserPrincipal(user);
    }

    public List<User> getUsers(){
        return repository.findAll();
    }

    public User createUser(User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        if(repository.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException(user.getUsername());
        }else if(repository.findByEmail(user.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        return repository.save(user);
    }
}
