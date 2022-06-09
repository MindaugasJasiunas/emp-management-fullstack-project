package com.example.demo.exception.domain;

import com.example.demo.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
