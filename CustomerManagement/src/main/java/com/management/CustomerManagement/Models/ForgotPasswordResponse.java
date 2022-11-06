package com.management.CustomerManagement.Models;

public class ForgotPasswordResponse {
    private int userId;
    private int key;

    public ForgotPasswordResponse(int userId, int key) {
        this.userId = userId;
        this.key = key;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
