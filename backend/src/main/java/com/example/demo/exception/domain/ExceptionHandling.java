package com.example.demo.exception.domain;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.demo.HttpResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandling {
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration.";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username/password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration.";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    private static final String EMAIL_NOT_FOUND = "Email not found";
    private static final String EMAIL_EXISTS = "User with email %s already exists";
    private static final String USERNAME_EXISTS = "User with username %s already exists";
//    private static final String USER_MALFORMED_PUBLIC_ID = "Error occurred while trying to find user by id. Try changing user id and try again.";
    private static final String USER_NOT_FOUND = "User not found.";
    private static final String NOT_AN_IMAGE_FILE = "File provided is not an image.";


    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus status, String message){
        HttpResponse response = new HttpResponse(status.value(), status, status.getReasonPhrase(), message);
        return new ResponseEntity<HttpResponse>(response, status);
    }

    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<HttpResponse> handleUserDisabledException(UserDisabledException exception){ // we are not using exception obj to not reveal inner workings of a server
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> handleEmailNotFoundException(EmailNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, EMAIL_NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<HttpResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, String.format(EMAIL_EXISTS, exception.getMessage()));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<HttpResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, String.format(USERNAME_EXISTS, exception.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> handleUnsupportedMethodException(HttpRequestMethodNotSupportedException exception){
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods().iterator().next());
        return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> handleJWTTokenExpiredException(TokenExpiredException exception){
        return createHttpResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> handleJWTBadCredentialsException(BadCredentialsException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> handleJWTAccessDeniedException(AccessDeniedException exception){
        return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> handleJWTAccountLockedException(LockedException exception){
        return createHttpResponse(HttpStatus.LOCKED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception){
        return createHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> ioException(IOException exception){
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> handleFallbackException(Exception exception){
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
    }*/

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<HttpResponse> handleUserValidationException(UserValidationException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> handleUserNotFoundException(UserNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, USER_NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HttpResponse> handleUsernameNotFoundException(UsernameNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, USER_NOT_FOUND);
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<HttpResponse> handleNotAnImageFileException(NotAnImageFileException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, NOT_AN_IMAGE_FILE);
    }

//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<HttpResponse> handleGlobalSpringBootExceptions(NoHandlerFoundException exception){
//        return createHttpResponse(HttpStatus.NOT_FOUND, "This page was not found");
//    }

}
