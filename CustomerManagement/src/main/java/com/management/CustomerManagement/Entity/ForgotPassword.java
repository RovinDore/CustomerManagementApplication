package com.management.CustomerManagement.Entity;

import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class ForgotPassword {
    @Id
    @GeneratedValue
    private int id;

    @NonNull
    private int userId;

    @NonNull
    private String keyValue;

    private final long expDate;

    public ForgotPassword() {
        long fiveMinutes = 300000 * 12;
        expDate = new Date().getTime() + fiveMinutes;
    }

    public ForgotPassword(int userId, String key){
        this.userId = userId;
        this.keyValue = key;
        long fiveMinutes = 300000 * 12;
        expDate = new Date().getTime() + fiveMinutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public long getExpDate() {
        return expDate;
    }

}
