package com.management.CustomerManagement.Models;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Entity.User;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private String jwt;
    private UserResponse user;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public AuthenticationResponse(String jwt, MyUser user) {
        this.jwt = jwt;
        this.user = new UserResponse(user);
    }

    public AuthenticationResponse(String jwt, User user) {
        this.jwt = jwt;
        this.user = new UserResponse(user);
    }

    public String getJwt() {
        return jwt;
    }

    public UserResponse getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "jwt='" + jwt + '\'' +
                ", user=" + user +
                '}';
    }
}