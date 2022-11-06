package com.management.CustomerManagement.Exception;

public class JWTExpiredException extends RuntimeException {
    public JWTExpiredException(String msg) { super(msg);}
}
