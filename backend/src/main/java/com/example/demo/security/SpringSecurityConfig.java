package com.example.demo.security;

import com.example.demo.filter.JwtAccessDeniedHandler;
import com.example.demo.filter.JwtAuthenticationEntryPoint;
import com.example.demo.filter.JwtAuthorizationFilter;
import com.example.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.springframework.security.config.Customizer.withDefaults;

// Updated Web Security configuration ('extends WebSecurityConfigurerAdapter' is deprecated)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
public class SpringSecurityConfig {
    private final JwtAuthorizationFilter jwtAuthFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JpaUserDetailsManager jpaUserDetailsManager;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    public SpringSecurityConfig(JwtAuthorizationFilter jwtAuthFilter, JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .antMatchers("/users/**").permitAll()

                        .antMatchers("/").permitAll()
                        .antMatchers("/register", "/login").permitAll()
                        .antMatchers("/canTest").hasAuthority("canTest")
                        .anyRequest().authenticated() //should be after all matchers
                )
                .httpBasic(withDefaults())

                // disable Cross Site Request Forgery (for non-browser clients)
                .csrf().disable()

                // specify what can connect to our API
                .cors() // allow OPTIONS request
                .and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Spring Security, dont manage or create sessions (REST API)

                .and()
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // filter that works for each request and sets up security context each time

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider jpaDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // works either way - with user details service OR with user details manager
//        daoAuthenticationProvider.setUserDetailsService(jpaUserDetailsManager);
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }



}
