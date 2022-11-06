package com.management.CustomerManagement.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() { super("User not found");}
    public UserNotFoundException(String msg){super(msg);}
}
