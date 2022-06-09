package com.example.demo.resource;

import com.example.demo.HttpResponse;
import com.example.demo.exception.domain.ExceptionHandling;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class ErrorResource implements ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request){
        Object status= request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(status != null){
            Integer statusCode= Integer.valueOf(status.toString());
            if(statusCode == HttpStatus.NOT_FOUND.value()){
                return new ResponseEntity<HttpResponse>(new HttpResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), "Page not found.") ,HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<HttpResponse>(new HttpResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), "Page not found.") ,HttpStatus.NOT_FOUND);
    }
}
