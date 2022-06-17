package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.exception.domain.*;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utility.JWTTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j

@Service
//@Transactional // manage propagation when dealing with 1 transaction
@Primary
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final JWTTokenProvider jwtProvider;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public UserServiceImpl(UserRepository userRepository, JWTTokenProvider jwtProvider, RoleRepository roleRepository, PasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = getUserByUsername(username);
        if(user == null) throw new UsernameNotFoundException("");
        user.setLastLoginDate(LocalDate.now());
        // update user
        userRepository.save(user);
        return new UserPrincipal(user);
    }

    @Override
    public void validateUser(String username, String password) throws Exception {
        // check if user exists
        User user = getUserByUsername(username);
        if(user == null) throw new BadCredentialsException("");

        // check if user is not locked
        if(user.isNotLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(username)){
                // lock the user
                user.setNotLocked(false);
                user = userRepository.save(user);  // @Transactional prevents immediate saving to DB
            }
        }
        if(!user.isNotLocked()){
            // try to delete user from cache if exists - user is already locked.
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
            throw new LockedException("");
        }

        // check if user active
        if(!user.isActive()){
            throw new UserDisabledException("");
        }

        // check if user password matches
        boolean passwordMatch = passwordMatches(username, password);
        if(passwordMatch){
            // try to delete user from cache if exists - user is now successful.
            loginAttemptService.evictUserFromLoginAttemptCache(username);
        }else{
            loginAttemptService.addUserToLoginAttemptCache(username);
            throw new BadCredentialsException("");
        }
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
        // encode password, set default role & save
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);
        user.setProfileImageUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString()); // temporary profile image url - default
        if(user.getRoles() == null || user.getRoles().isEmpty()){
            // add default role
            if(roleRepository.findByRoleName("ROLE_USER").isPresent()){
                user.setRoles(Set.of(roleRepository.findByRoleName("ROLE_USER").get()));
                return userRepository.save(user);
            }
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
    public User getUserByUsername(String username) {
        if(userRepository.findByUsername(username).isPresent()){
            return userRepository.findByUsername(username).get();
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            return userRepository.findByEmail(email).get();
        }
        return null;
    }

    @Override
    public void updatePassword(String emailByLink, String newPassword) {
        if(!userRepository.findByEmail(emailByLink).isPresent()) return;
        User userInDB= userRepository.findByEmail(emailByLink).get();
        userInDB.setActive(true);
        userInDB.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userInDB);
    }

    @Override
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

    @Override
    public User resetPassword(String email) {
        // disable user
        User user = getUserByEmail(email);
        if(user==null) return null;
        user.setActive(false);
        return userRepository.save(user);
    }

    private boolean passwordMatches(String username, String password) {
        User user = getUserByUsername(username);
        if(user == null) return false;
        return passwordEncoder.matches(password, user.getPassword());
    }
}
