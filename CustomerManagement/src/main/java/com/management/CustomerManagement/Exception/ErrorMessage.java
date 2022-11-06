package com.management.CustomerManagement.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ErrorMessage {
    @Autowired
    private final Logger logger = LoggerFactory.getLogger(getClass());;

    private String message;
    private int code;
    private final boolean error;

    public ErrorMessage() {
        this.message = "";
        this.error = false;
    }

    public ErrorMessage(String message, int code) {
        logger.info("There was an issue: " + message + " | " + code);
        this.message = message;
        this.code = code;
        this.error = true;
    }

    public ErrorMessage(String message) {
        logger.info("There was an issue: " + message);
        this.message = message;
        this.error = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isError() {
        return error;
    }
}
