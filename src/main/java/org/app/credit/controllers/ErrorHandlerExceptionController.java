package org.app.credit.controllers;

import org.app.credit.entities.Error;
import org.app.credit.exceptions.BusinessException;
import org.app.credit.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ErrorHandlerExceptionController {

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error userNotFoundException(ResourceNotFoundException e){
        String message = "Resource not found";

        return buildError(message, HttpStatus.NOT_FOUND.value(), e.getMessage());

    }

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error businessException(BusinessException e){
        String message = "Business Exception";

        return buildError(message, HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error argumentException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), "The field " + error.getField() + " " + error.getDefaultMessage()));

        String message = "Argument Exception";

        return buildError(message, HttpStatus.BAD_REQUEST.value(), errors);
    }

    private Error buildError(String message, int status, Object error){
        Error responseError = new Error();
        responseError.setMessage(message);
        responseError.setStatus(status);
        responseError.setError(error);
        responseError.setTimestamp(LocalDateTime.now());

        return responseError;
    }

}
