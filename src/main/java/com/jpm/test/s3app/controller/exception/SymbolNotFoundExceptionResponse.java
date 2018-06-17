package com.jpm.test.s3app.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Could not find symbol")
public class SymbolNotFoundExceptionResponse extends Exception
{
    public SymbolNotFoundExceptionResponse(String symbol)
    {
        super(String.format("Symbol not found: %s", symbol));
    }
}
