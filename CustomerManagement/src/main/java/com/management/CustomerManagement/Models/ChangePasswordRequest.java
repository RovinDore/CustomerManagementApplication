package com.management.CustomerManagement.Models;

public class ChangePasswordRequest {
    private int userId;
    private String email;

    public ChangePasswordRequest() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
