package com.management.CustomerManagement.Models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;

public class ChangePasswordResponse {
    private int userId;
    private int key;
    private String email;
    private long expDate;

    public ChangePasswordResponse() {
        long twoHours = 7200000;
        expDate = new Date().getTime() + twoHours;
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

    public long getExpDate() { return expDate; }
}
