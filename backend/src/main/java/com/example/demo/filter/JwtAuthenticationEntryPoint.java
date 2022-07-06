package com.example.demo.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.demo.HttpResponse;
import com.example.demo.utility.JWTTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    private final JWTTokenProvider jwtTokenProvider;

    public JwtAuthenticationEntryPoint(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException {
        // generate custom response body
        HttpResponse customResponse = new HttpResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), "You need to login to access this page");
        String token = request.getHeader("Authorization");
        if(token != null && !token.trim().isEmpty()){
            token = request.getHeader("Authorization").substring(7);
        }

        try{
            jwtTokenProvider.isTokenExpired(token);
        }catch (Exception e){
            if(e instanceof TokenExpiredException) customResponse = new HttpResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), "JWT token expired");
        }

        // send forbidden error
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, customResponse);
        out.flush();
    }
}
