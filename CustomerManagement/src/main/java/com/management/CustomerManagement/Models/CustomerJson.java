package com.management.CustomerManagement.Models;

import com.management.CustomerManagement.Entity.Customer;

public class CustomerJson {
    int id;
    private String name;
    private String email;
    private String phoneNumber;
    private String bio;
    private int dependantAmount;
    private String file;

    public CustomerJson(Customer customer){
        this.id = customer.getId();
        this.name = customer.getName();
        this.dependantAmount = customer.getDependants().size();
        this.email = customer.getEmail();
        this.phoneNumber = customer.getPhoneNumber();
        this.bio = customer.getBio();
        this.file = customer.getFile();
    }

    public CustomerJson() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getDependantAmount() {
        return dependantAmount;
    }

    public void setDependantAmount(int dependantAmount) {
        this.dependantAmount = dependantAmount;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "CustomerJson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", bio='" + bio + '\'' +
                ", dependantAmount=" + dependantAmount +
                ", file='" + file + '\'' +
                '}';
    }
}
