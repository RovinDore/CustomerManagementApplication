package com.management.CustomerManagement.Models;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Entity.User;

public class UserResponse {
    private int id;
    private String username;
    private boolean active;
    private String authorities;
    private String email;

    public UserResponse() {
    }

    public UserResponse(MyUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.active = user.isEnabled();
        this.authorities = user.getAuthorities().stream().toList().get(0).toString();
        this.email = user.getEmail();
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.active = user.isEnabled();
        this.authorities = user.getRoles();
        this.email = user.getEmail();
    }

    static UserResponse SetUpUserResponse(MyUser user){
        return new UserResponse(user);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", active=" + active +
                ", authorities='" + authorities + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
