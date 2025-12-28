package org.app.credit.controllers;

import org.app.credit.entities.Error;
import org.app.credit.exceptions.BusinessException;
import org.app.credit.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
public class ErrorHandlerExceptionController {

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Error> userNotFoundException(Exception e){
        Error error = new Error();
        error.setMessage("Resource not found");
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(e.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(error);
    }

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Error> businessException(Exception e){
        Error error = new Error();
        error.setMessage("Business Exception");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(e.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }
}
