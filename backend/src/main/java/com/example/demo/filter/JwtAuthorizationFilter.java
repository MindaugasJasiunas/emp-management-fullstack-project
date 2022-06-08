package com.example.demo.filter;

import com.example.demo.utility.JWTTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // take request & make sure that request is not OPTIONS (collects information about server)
        if(request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())){
            response.setStatus(HttpStatus.OK.value());
        }else{
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(authorizationHeader == null || authorizationHeader.isBlank() || !authorizationHeader.startsWith( jwtTokenProvider.tokenPrefix)){
                filterChain.doFilter(request, response);
                return;
            }
            String token= authorizationHeader.replace("Bearer ","");
            if(jwtTokenProvider.isTokenValid(token) /*&& SecurityContextHolder.getContext().getAuthentication() == null*/){ // SecurityContext check not needed because we are not using session
                String username = jwtTokenProvider.getSubject(token);
                List<? extends GrantedAuthority> authorities = new ArrayList<>(jwtTokenProvider.getAuthoritiesFromToken(token));
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);

                // set user as authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
