package com.example.demo;

import com.example.demo.domain.Authority;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.email.EmailService;
import com.example.demo.exception.domain.ExceptionHandling;
import com.example.demo.exception.domain.UserValidationException;
import com.example.demo.repository.PasswordResetRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.resource.AuthenticationResource;
import com.example.demo.resource.UserResource;
import com.example.demo.service.LoginAttemptService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import com.example.demo.utility.JWTTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.tomcat.jni.Local;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest  // brings up all application
@ExtendWith(MockitoExtension.class)  // initialize mocks
//@EnableConfigurationProperties(value = JWTTokenProvider.class)
//@TestPropertySource("classpath:application.yml")
public class AuthenticationResourceTest {
    MockMvc mockMvc;

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    LoginAttemptService loginAttemptService;
    @Mock
    EmailService emailService;
    @Mock
    PasswordResetRepository passwordResetRepository;
    @Mock
    PasswordResetService passwordResetService;

    String secretKey = "SuperLongAndVerySecureKey-[].~^+$&4";
    String tokenPrefix = "Bearer ";
    Integer tokenExpirationAfterDays = 14;
    JWTTokenProvider jwtProvider = new JWTTokenProvider(secretKey, tokenPrefix, tokenExpirationAfterDays);

    static ObjectMapper objectMapper;

    @BeforeAll
    static void init(){
        // Jackson Object Mapper with Java Time Module for LocalDate objects
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  //  objectMapper.registerModule(new JSR310Module());  // JSR310Module deprecated
    }

    @BeforeEach
    void setUp(){
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(wac)
//                .apply(springSecurity())
//                .alwaysDo(print())
//                .build();

        mockMvc = MockMvcBuilders
                    .standaloneSetup(new AuthenticationResource(new UserServiceImpl(userRepository, jwtProvider, roleRepository, passwordEncoder, loginAttemptService), passwordResetService, emailService))
                .setControllerAdvice(new ExceptionHandling())
                .build();
    }

    @DisplayName("Register new user (test saving to DB, default fields set in entity & user service, checking validity before saving)")
    @Test
    void testRegisterNewUser() throws Exception {
        // When
        final String username = "username";
        final String email = "username@example.com";
        final String roleUser = "ROLE_USER";

        final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        User userToReturn = Util.buildUser();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(userArgumentCaptor.capture())).thenReturn(userToReturn);
        Mockito.when(roleRepository.findByRoleName(roleUser)).thenReturn(Optional.of(Util.buildUserRoleWithAuthority()));

        HttpAuthRegisterRequest request = new HttpAuthRegisterRequest(username, email, "password", "User", "Name", "http://imgUrl", LocalDate.of(2022,1,1));

        // Then
        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.publicId", Matchers.equalTo(userToReturn.getPublicId().toString())))
                .andExpect(jsonPath("$.firstName", Matchers.equalTo(userToReturn.getFirstName())))
                .andExpect(jsonPath("$.lastName", Matchers.equalTo(userToReturn.getLastName())))
                .andExpect(jsonPath("$.email", Matchers.equalTo(userToReturn.getEmail())))
                .andExpect(jsonPath("$.username", Matchers.equalTo(userToReturn.getUsername())))
                .andExpect(jsonPath("$.profileImageUrl", Matchers.equalTo(userToReturn.getProfileImageUrl())))
                .andExpect(jsonPath("$.joinDate[0]", Matchers.equalTo(userToReturn.getJoinDate().getYear())))
                .andExpect(jsonPath("$.joinDate[1]", Matchers.equalTo(userToReturn.getJoinDate().getMonthValue())))
                .andExpect(jsonPath("$.joinDate[2]", Matchers.equalTo(userToReturn.getJoinDate().getDayOfMonth())))
                .andExpect(jsonPath("$.lastLoginDate[0]", Matchers.equalTo(userToReturn.getLastLoginDate().getYear())))
                .andExpect(jsonPath("$.lastLoginDate[1]", Matchers.equalTo(userToReturn.getLastLoginDate().getMonthValue())))
                .andExpect(jsonPath("$.lastLoginDate[2]", Matchers.equalTo(userToReturn.getLastLoginDate().getDayOfMonth())))
                .andExpect(jsonPath("$.dateOfBirth[0]", Matchers.equalTo(userToReturn.getDateOfBirth().getYear())))
                .andExpect(jsonPath("$.dateOfBirth[1]", Matchers.equalTo(userToReturn.getDateOfBirth().getMonthValue())))
                .andExpect(jsonPath("$.dateOfBirth[2]", Matchers.equalTo(userToReturn.getDateOfBirth().getDayOfMonth())))
                .andExpect(jsonPath("$.roles[0].id", Matchers.equalTo(userToReturn.getRoles().iterator().next().getId().intValue())))
                .andExpect(jsonPath("$.roles[0].roleName", Matchers.equalTo(userToReturn.getRoles().iterator().next().getRoleName())))
                .andExpect(jsonPath("$.active", Matchers.equalTo(true)))
                .andExpect(jsonPath("$.notLocked", Matchers.equalTo(true)));

        // check if default fields were set by user service & user entity
        assertNotNull(userArgumentCaptor.getValue());
        assertEquals("ROLE_USER", userArgumentCaptor.getValue().getRoles().iterator().next().getRoleName());
        assertNotNull(userArgumentCaptor.getValue().getPublicId());
        assertEquals(LocalDate.now(), userArgumentCaptor.getValue().getJoinDate());
        assertEquals(LocalDate.now(), userArgumentCaptor.getValue().getLastLoginDate());
        assertTrue(userArgumentCaptor.getValue().isActive());
        assertTrue(userArgumentCaptor.getValue().isNotLocked());

        // Verify calls to mocks
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(anyString());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @DisplayName("Register new user - email already exists in DB (expect EmailAlreadyExistsException)")
    @Test
    @Disabled(value="Disabled until implemented")
    void testRegisterNewUser_EmailAlreadyExists() throws Exception {}

    @DisplayName("Register new user with invalid input - password too short (expect UserValidationException)")
    @Test
    void testRegisterNewUser_InvalidInput() throws Exception {
        // When
        String invalidPassword = "pass";
        HttpAuthRegisterRequest request = new HttpAuthRegisterRequest("username", "username@example.com", invalidPassword, "User", "Name", "http://imgUrl", LocalDate.of(2022,1,1));

        // Then
        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserValidationException))
                .andExpect(result -> assertEquals("password should be at least 8 characters long", result.getResolvedException().getMessage()));

        Mockito.verify(userRepository, Mockito.never()).findByUsername(anyString());
        Mockito.verify(userRepository, Mockito.never()).findByEmail(anyString());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void testLoginWithNewUserAndGetValidJWTToken() throws Exception {
        User userToReturn = Util.buildUser();
        HttpAuthLoginRequest request = new HttpAuthLoginRequest(userToReturn.getUsername(), "password");

        Mockito.when(userRepository.findByUsername(userToReturn.getUsername())).thenReturn(Optional.of(userToReturn));
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        MvcResult response = mockMvc.perform(
                get("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());

        String authorizationHeader = response.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertNotNull(authorizationHeader);
        assertTrue(authorizationHeader.startsWith("Bearer "));

        String jwtToken = authorizationHeader.replace("Bearer ","");

        // Validate token
        assertTrue(jwtProvider.isTokenValid(jwtToken));
        assertEquals(userToReturn.getUsername(), jwtProvider.getSubject(jwtToken));
        assertEquals(userToReturn.getAuthorities().iterator().next().getPermission(), jwtProvider.getAuthoritiesFromToken(jwtToken).iterator().next().toString());

        Mockito.verify(userRepository, Mockito.times(6)).findByUsername(anyString());
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches(anyString(), anyString());

    }

}
