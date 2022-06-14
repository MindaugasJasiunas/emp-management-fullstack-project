package com.example.demo;

import com.example.demo.domain.Authority;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;

import java.time.LocalDate;
import java.util.Set;

public class Util {
    private Util(){}

    public static User buildUser(String roleName){
        User user = new User();
        user.setId(1L);
//        user.setPublicId(UUID.fromString("c4a0d130-e372-42cd-96c2-dfbf8f3fb888"));
        user.setUsername("johnd");
        user.setPassword("password");
        user.setEmail("johnd@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setDateOfBirth(LocalDate.of(2000,1,1));
        user.setActive(true);
        user.setNotLocked(true);
        user.setJoinDate(LocalDate.of(2000,2,2));
        user.setLastLoginDate(LocalDate.of(2000,3,3));
        user.setProfileImageUrl("http://imgUrl");

        user.setRoles(Set.of(buildUserRoleWithAuthority(roleName)));

        return user;
    }
    public static User buildUser(){
        return buildUser("ROLE_USER");
    }
    public static Role buildUserRoleWithAuthority(String roleName){
        Authority canRead = new Authority();
        canRead.setId(1L);
        canRead.setPermission("can read");

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setRoleName(roleName);

        userRole.setAuthorities(Set.of(canRead));
        canRead.setRoles(Set.of(userRole));

        return userRole;
    }
    public static Role buildUserRoleWithAuthority(){
        return buildUserRoleWithAuthority("ROLE_USER");
    }
}
