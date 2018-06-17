package com.jpm.test.s3app.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid number")
public class InvalidNumberExceptionResponse extends Exception
{
    public InvalidNumberExceptionResponse(String numberName, double value)
    {
        super(String.format("Invalid %s:", numberName, String.valueOf(value)));
    }

    public InvalidNumberExceptionResponse(String message)
    {
        super(message);
    }
}
