package com.example.demo.security;

import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername "+username+" in JpaUserDetailsManager");
        Optional<User> userFromDB = repository.findByUsername(username);
        userFromDB.orElseThrow(() -> new UsernameNotFoundException("No user found with username = " + username));
        return new UserPrincipal(userFromDB.get());
    }

    @Override
    public void createUser(UserDetails userDetails) {
        User u= new User();
        u.setUsername(userDetails.getUsername());
        u.setPassword(userDetails.getPassword());
        u.setActive(userDetails.isEnabled());
        userDetails.getAuthorities();
        repository.save(u);
    }

    @Override
    public void updateUser(UserDetails user) {
//        repository.save(user);
    }

    @Override
    public void deleteUser(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No User found for username -> " + username));
        repository.delete(user);
    }

    /**
     * This method assumes that both oldPassword and the newPassword params
     * are encoded with configured passwordEncoder
     *
     * @param oldPassword the old password of the user
     * @param newPassword the new password of the user
     */
    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
//        AuthUserDetails userDetails = repository.findByPassword(oldPassword)
//                .orElseThrow(() -> new UsernameNotFoundException("Invalid password "));
//        userDetails.setPassword(newPassword);
//        repository.save(userDetails);
    }

    @Override
    public boolean userExists(String username) {
        return repository.findByUsername(username).isPresent();
    }
}
