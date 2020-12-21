package com.optila.taskrunner.api.exceptions;

public class MissingParameterException extends RuntimeException {

    public MissingParameterException(String parameterName) {
        super(String.format("%s is missing", parameterName));
    }

}
