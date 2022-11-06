package com.management.CustomerManagement.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Something went wrong")
public class GeneralException extends RuntimeException {

    public GeneralException(String msg){
        super(msg);
    }
}
