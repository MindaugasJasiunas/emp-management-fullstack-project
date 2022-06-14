package com.example.demo;

import com.example.demo.domain.Authority;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.exception.domain.ExceptionHandling;
import com.example.demo.exception.domain.UserValidationException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.resource.AuthenticationResource;
import com.example.demo.service.UserServiceImpl;
import com.example.demo.utility.JWTTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest  // brings up all application
@ExtendWith(MockitoExtension.class)  // initialize mocks
public class EndpointsWithJWTTokenTest {
    MockMvc mockMvc;
    @Autowired
    JWTTokenProvider jwtProvider;
    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void testAccessingEndpoint_WithoutJWT() throws Exception {
        String token = jwtProvider.generateJwtToken(new UserPrincipal(Util.buildUser()));

        mockMvc.perform(
                get("/users/home")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                // exception thrown in JwtAuthenticationEntryPoint
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.httpStatusCode", Matchers.is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.httpStatus", Matchers.is("FORBIDDEN")))
                .andExpect(jsonPath("$.reason", Matchers.is(HttpStatus.FORBIDDEN.getReasonPhrase())))
                .andExpect(jsonPath("$.httpStatusCode", Matchers.is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.message", Matchers.is("You need to login to access this page")));
    }

    @Test
    void testAccessingEndpoint_WithValidJWT() throws Exception {
        String token = jwtProvider.generateJwtToken(new UserPrincipal(Util.buildUser("ROLE_SUPER_ADMIN")));
        assertNotNull(token);

        mockMvc.perform(
                get("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @DisplayName("Accessing endpoint with malformed JWT - returns FORBIDDEN 403")
    @Test
    void testAccessingEndpoint_WithMalformedJWT() throws Exception{
        String token = jwtProvider.generateJwtToken(new UserPrincipal(Util.buildUser()));
        assertNotNull(token);

        // malform token
        token = token.replaceAll(token.substring(0,1), "q");
        token = token.replaceAll(token.substring(1,2), "q");
        token = token.replaceAll(token.substring(2,3), "q");

        mockMvc.perform(
                get("/users/home")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @DisplayName("Accessing endpoint with valid JWT but invalid role - returns UNAUTHORIZED 401")
    @Test
    void testAccessingEndpointWithInvalidJWT_ForbiddenEndpointAccess() throws Exception {
        String token = jwtProvider.generateJwtToken(new UserPrincipal(Util.buildUser()));
        assertNotNull(token);

        mockMvc.perform(
                get("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @DisplayName("Testing accessing non auth endpoint without auth (without JWT)")
    @Test
    void testingNonAuthWithoutAuth() throws Exception {
        mockMvc
                .perform(get("/"))
                .andExpect(status().isOk());
    }

}
