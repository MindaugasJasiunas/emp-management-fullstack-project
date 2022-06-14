package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.exception.domain.EmailAlreadyExistsException;
import com.example.demo.exception.domain.MalformedUserPublicIdException;
import com.example.demo.exception.domain.UserNotFoundException;
import com.example.demo.exception.domain.UsernameAlreadyExistsException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utility.JWTTokenProvider;
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
import java.util.Set;
import java.util.UUID;

@Slf4j

@Service
@Transactional // manage propagation when dealing with 1 transaction
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final JWTTokenProvider jwtProvider;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, JWTTokenProvider jwtProvider, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty()){
            log.debug("Username not found: "+username);
            throw new UsernameNotFoundException("User does not exist!");
        }
        User user = userOptional.get();
        user.setLastLoginDate(LocalDate.now());
        // update user
        userRepository.save(user);
        return new UserPrincipal(user);
    }

    @Override
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) throws Exception {
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException(user.getUsername());
        }else if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        // set default role & save
        if(roleRepository.findByRoleName("ROLE_USER").isPresent()){
            user.setRoles(Set.of(roleRepository.findByRoleName("ROLE_USER").get()));
            return userRepository.save(user);
        }
        // else internal server error
        throw new Exception("");
    }

    @Override
    public User updateUser(User user, String publicId) throws Exception {
        User userFromDB = getUserByPublicId(publicId);
        user.setId(userFromDB.getId());
        user.setPublicId(userFromDB.getPublicId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String publicId) throws UserNotFoundException {
        User userToDelete = getUserByPublicId(publicId);
        userRepository.deleteById(userToDelete.getId());
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String generateTokenForUser(String username){
        if(userRepository.findByUsername(username).isPresent()){
            User user = userRepository.findByUsername(username).get();
            return jwtProvider.generateJwtToken(new UserPrincipal(user));
        }else{
            return null;
        }
    }

    @Override
    public User getUserByPublicId(String publicId) throws UserNotFoundException {
        try{
            UUID uuid = UUID.fromString(publicId);
            return userRepository.findByPublicId(uuid).get();
        }catch (Exception e){
            throw new UserNotFoundException("");
        }
    }
}
