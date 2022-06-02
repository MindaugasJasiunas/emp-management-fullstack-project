package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;

// Updated Web Security configuration ('extends WebSecurityConfigurerAdapter' is deprecated)
@Configuration
@EnableWebSecurity
//public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
public class SpringSecurityConfig {

//    protected void configure(HttpSecurity http) throws Exception {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .antMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        .antMatchers(HttpMethod.PUT, "/api/v1/**").hasRole("SUPER_ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/v1/**").hasAuthority("canDeleteUsers")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        return http.build();
    }

//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    /*@Bean
    public UserDetailsManager users(DataSource dataSource) {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.createUser(user);
        return users;
    }*/

}
