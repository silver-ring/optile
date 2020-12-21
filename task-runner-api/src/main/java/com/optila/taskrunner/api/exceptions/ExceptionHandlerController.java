package com.optila.taskrunner.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(value = JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> exception(JobNotFoundException jobNotFoundException) {
        Map<String, String> result = new HashMap<>();
        result.put("message", jobNotFoundException.getMessage());
        return result;
    }

    @ExceptionHandler(value = MissingParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> exception(MissingParameterException missingParameterException) {
        Map<String, String> result = new HashMap<>();
        result.put("message", missingParameterException.getMessage());
        return result;
    }

}
