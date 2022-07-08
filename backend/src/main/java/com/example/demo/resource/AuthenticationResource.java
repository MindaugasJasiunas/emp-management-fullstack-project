package com.example.demo.resource;

import com.example.demo.*;
import com.example.demo.domain.PasswordReset;
import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.email.EmailService;
import com.example.demo.exception.domain.*;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import com.example.demo.utility.UserMapper;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@RestController
public class AuthenticationResource {
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public AuthenticationResource(UserService userService, PasswordResetService passwordResetService, EmailService emailService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User registerNewUser(@Valid @RequestBody HttpAuthRegisterRequest register, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }

        // map to user class
        User user = UserMapper.INSTANCE.HttpAuthRegisterRequestToUser(register);

        String rawPassword = user.getPassword();

        user = userService.createUser(user);

        //send email
        emailService.sendNewPasswordEmail(user.getFirstName(), rawPassword, user.getEmail());

        return user;
    }

//    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getTokenForUser(@Valid @RequestBody HttpAuthLoginRequest request, BindingResult result) throws Exception {
        // check if request has errors
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }

        // check if user exists & generate JWT for that user
        userService.validateUser(request.username(), request.password());

        User userToReturn = userService.getUserByUsername(request.username());
        // if error not thrown from validateUser - generate token & return

        String refreshToken = userService.generateTokenForUser(request.username(), true);
        if(refreshToken == null) throw new Exception("");

        HttpHeaders headers= new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", refreshToken));
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Length, Authorization");
        return ResponseEntity.ok().headers(headers).body(userToReturn);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody HttpPasswordResetRequest email, BindingResult result) throws EmailNotFoundException, MessagingException {
        if(result.hasErrors()){
            System.out.println(result.getAllErrors());
            throw new EmailNotFoundException("");
        }
        User user = userService.resetPassword(email.email());
        if(user == null) throw new EmailNotFoundException("");
        String link = passwordResetService.generatePasswordResetLink(email.email());
        emailService.sendResetPasswordEmail(user.getFirstName(), link, user.getEmail());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/reset/{resetLink}", method = RequestMethod.POST)
    public ResponseEntity<Void> newPassword(@Valid @RequestBody HttpNewPasswordRequest request, @PathVariable("resetLink") String resetLink) {
        if(!request.newPassword().equals(request.newPasswordRepeated())) return ResponseEntity.badRequest().build();

        String emailByLink = passwordResetService.getEmailByLink(resetLink);
        if(emailByLink == null) return ResponseEntity.badRequest().build();

        userService.updatePassword(emailByLink, request.newPassword());
        passwordResetService.deleteDBEntryByEmail(emailByLink);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Void> getAccessToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String refreshToken) throws UserValidationException {
        String accessToken = userService.refreshToken(refreshToken);

        HttpHeaders headers= new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Length, Authorization");
        return ResponseEntity.ok().headers(headers).build();
    }
}
