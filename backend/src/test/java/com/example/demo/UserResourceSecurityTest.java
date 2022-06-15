package com.example.demo;

import com.example.demo.domain.Authority;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.repository.UserRepository;
import com.example.demo.resource.UserResource;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import com.example.demo.utility.JWTTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.checkerframework.checker.units.qual.A;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserResourceSecurityTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext wac;
    @Autowired
    JWTTokenProvider jwtTokenProvider;

    @MockBean
    UserServiceImpl userService;
    @InjectMocks
    UserResource userResource; // UserResource userResource = new UserResource(userService);

    static ObjectMapper objectMapper;

    @BeforeAll
    static void init(){
        // Jackson Object Mapper with Java Time Module for LocalDate objects
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  //  objectMapper.registerModule(new JSR310Module());  // JSR310Module deprecated
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

//    @DisplayName("Test accessing main '/' endpoint without JWT authentication - returns OK")
    @DisplayName("GET '/' without JWT - OK")
    @Test
    void testWithoutJWT_mainPage() throws Exception {
        mockMvc.perform(
                get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

//    @DisplayName("Test accessing endpoint without JWT authentication - returns Forbidden")
    @DisplayName("GET '/users/' without JWT - Forbidden")
    @Test
    void testWithoutJWT() throws Exception {
        mockMvc.perform(
                get("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", Matchers.is("You need to login to access this page")));

        Mockito.verify(userService, Mockito.never()).getUsers();
    }

//    @DisplayName("Test accessing endpoint with JWT & required 'user:read' authority - returns mocked list of users")
    @DisplayName("GET '/users/' with 'user:read' - list of users")
    @Test
    void testWithJWT() throws Exception {
        User user = Util.buildUser();
        user.setRoles(null);

        Mockito.when(userService.getUsers()).thenReturn(List.of(user));

        Authority canReadUsers = new Authority();
        canReadUsers.setPermission("user:read");
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        role.setAuthorities(Set.of(canReadUsers));
        user.setRoles(Set.of(role));

        String token = jwtTokenProvider.generateJwtToken(new UserPrincipal(user));

        mockMvc.perform(
                get("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].publicId", Matchers.is(user.getPublicId().toString())))
                .andExpect(jsonPath("$[0].username", Matchers.is(user.getUsername())))
                .andExpect(jsonPath("$[0].email", Matchers.is(user.getEmail())))
                .andExpect(jsonPath("$[0].roles[0].roleName", Matchers.is(user.getRoles().iterator().next().getRoleName())));
    }

//    @DisplayName("Test accessing endpoint with JWT but INVALID authorities - returns error")
    @DisplayName("PUT '/users/{publicId}' with JWT but INVALID authorities - Unauthorized & error")
    @Test
    void testWithInvalidJWT() throws Exception {
        User user = Util.buildUser();
        user.setRoles(null);

        Authority canReadUsers = new Authority();
        canReadUsers.setPermission("user:read");
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        role.setAuthorities(Set.of(canReadUsers));
        user.setRoles(Set.of(role));

        Mockito.when(userService.updateUser(any(User.class), anyString())).thenReturn(user);

        String token = jwtTokenProvider.generateJwtToken(new UserPrincipal(user));

        mockMvc.perform(
                put("/users/"+user.getPublicId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", Matchers.is("You do not have permission to access this page")));

        Mockito.verify(userService, Mockito.never()).updateUser(any(User.class), anyString());
    }

//    @DisplayName("Test accessing endpoint with JWT & required 'user:update' authority - returns mocked created user")
    @DisplayName("PUT '/users/{publicId}' with 'user:update' - user")
    @Test
    void testWithValidJWT() throws Exception {
        User user = Util.buildUser();
        user.setRoles(null);

        Authority canUpdateUsers = new Authority();
        canUpdateUsers.setPermission("user:update");
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_HR");
        role.setAuthorities(Set.of(canUpdateUsers));
        user.setRoles(Set.of(role));

        Mockito.when(userService.updateUser(any(User.class), anyString())).thenReturn(user);

        String token = jwtTokenProvider.generateJwtToken(new UserPrincipal(user));

        mockMvc.perform(
                put("/users/"+user.getPublicId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId().intValue())))
                .andExpect(jsonPath("$.publicId", Matchers.is(user.getPublicId().toString())))
                .andExpect(jsonPath("$.username", Matchers.is(user.getUsername())))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())))
                .andExpect(jsonPath("$.firstName", Matchers.is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", Matchers.is(user.getLastName())))
                .andExpect(jsonPath("$.password", Matchers.is(user.getPassword())))
                .andExpect(jsonPath("$.profileImageUrl", Matchers.is(user.getProfileImageUrl())))
                .andExpect(jsonPath("$.roles[0].id", Matchers.is(user.getRoles().iterator().next().getId().intValue())))
                .andExpect(jsonPath("$.roles[0].roleName", Matchers.is(user.getRoles().iterator().next().getRoleName())));

        Mockito.verify(userService, Mockito.times(1)).updateUser(any(User.class), anyString());
    }

//    @DisplayName("Test accessing endpoint with JWT & required 'user:create' authority - returns mocked user")
    @DisplayName("POST '/users/' with 'user:create' - user")
    @Test
    void testWithValidJWT_create() throws Exception {
        User user = Util.buildUser();
        user.setRoles(null);

        Authority canCreateUsers = new Authority();
        canCreateUsers.setPermission("user:create");
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_ADMIN");
        role.setAuthorities(Set.of(canCreateUsers));
        user.setRoles(Set.of(role));

        Mockito.when(userService.createUser(any(User.class))).thenReturn(user);

        String token = jwtTokenProvider.generateJwtToken(new UserPrincipal(user));

        mockMvc.perform(
                post("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId().intValue())))
                .andExpect(jsonPath("$.publicId", Matchers.is(user.getPublicId().toString())))
                .andExpect(jsonPath("$.firstName", Matchers.is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", Matchers.is(user.getLastName())))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())))
                .andExpect(jsonPath("$.password", Matchers.is(user.getPassword())))
                .andExpect(jsonPath("$.username", Matchers.is(user.getUsername())))
                .andExpect(jsonPath("$.profileImageUrl", Matchers.is(user.getProfileImageUrl())))
                .andExpect(jsonPath("$.roles[0].roleName", Matchers.is(user.getRoles().iterator().next().getRoleName())))
                .andExpect(jsonPath("$.active", Matchers.is(user.isActive())))
                .andExpect(jsonPath("$.notLocked", Matchers.is(user.isNotLocked())));

        Mockito.verify(userService, Mockito.times(1)).createUser(any(User.class));
    }

//    @DisplayName("Test accessing endpoint with JWT but without required 'user:create' authority - returns Unauthorized")
    @DisplayName("POST '/users/' without 'user:create' - Unauthorized")
    @Test
    void testWithInvalidJWT_create() throws Exception {
        User user = Util.buildUser();
        user.setRoles(null);

        Authority canCreateUsers = new Authority();
        canCreateUsers.setPermission("user:read");
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_USER");
        role.setAuthorities(Set.of(canCreateUsers));
        user.setRoles(Set.of(role));

        Mockito.when(userService.createUser(any(User.class))).thenReturn(user);

        String token = jwtTokenProvider.generateJwtToken(new UserPrincipal(user));

        mockMvc.perform(
                post("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized());

        Mockito.verify(userService, Mockito.never()).createUser(any(User.class));
    }

//    @DisplayName("Test accessing endpoint with JWT & required 'user:delete' authority - no return")
    @DisplayName("DELETE '/users/{publicId}' with 'user:delete' - no Content")
    @Test
    void testWithValidJWT_delete() throws Exception {
        User user = Util.buildUser();
        user.setRoles(null);

        Authority canDeleteUsers = new Authority();
        canDeleteUsers.setPermission("user:delete");
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_ADMIN");
        role.setAuthorities(Set.of(canDeleteUsers));
        user.setRoles(Set.of(role));

        Mockito.doNothing().when(userService).deleteUser(anyString());

        String token = jwtTokenProvider.generateJwtToken(new UserPrincipal(user));

        mockMvc.perform(
                delete("/users/"+user.getPublicId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(anyString());
    }

//    @DisplayName("Test accessing endpoint with JWT but without required 'user:delete' authority - returns Unauthorized")
    @DisplayName("DELETE '/users/{publicId}' without 'user:delete' - Unauthorized")
    @Test
    void testWithInvalidJWT_delete() throws Exception {
        User user = Util.buildUser();
        user.setRoles(null);

        Authority canDeleteUsers = new Authority();
        canDeleteUsers.setPermission("user:read");
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_ADMIN");
        role.setAuthorities(Set.of(canDeleteUsers));
        user.setRoles(Set.of(role));

        Mockito.doNothing().when(userService).deleteUser(anyString());

        String token = jwtTokenProvider.generateJwtToken(new UserPrincipal(user));

        mockMvc.perform(
                delete("/users/"+user.getPublicId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized());

        Mockito.verify(userService, Mockito.never()).deleteUser(anyString());
    }
}
