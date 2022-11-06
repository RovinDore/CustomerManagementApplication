package com.management.CustomerManagement.Models;

public class ForgotPasswordRequest {
    private int userId;
    private String key;

    public ForgotPasswordRequest(int userId, String key) {
        this.userId = userId;
        this.key = key;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
