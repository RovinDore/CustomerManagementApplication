package com.management.CustomerManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    int id;

    @NotEmpty(message = "Must enter a name")
    @Size(min = 4, message = "Name should be at least 4 characters")
    private String name;

    @NotEmpty(message = "Must enter email")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotEmpty(message = "Phone number must not be empty")
    @Size(min = 7, message = "Enter a valid phone number (must be at least 7 digits)")
    @Size(max = 10, message = "Enter a valid phone number (can't exceed 10 digits)")
    private String phoneNumber;

    private String bio;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Dependant> dependants;

    private String file;

    public Customer() {
    }

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public void removeDependant(Dependant dependant){
        this.dependants = dependants.stream()
                .filter(p -> dependant.getId() == p.getId())
                .toList();

//        return this.dependants;
    }

    public List<Dependant> addDependant(Dependant dependant){
        this.removeDependant(dependant);
        this.dependants.add(dependant);

        return this.dependants;
    }

    public List<Dependant> getDependants() {
        return dependants;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setDependants(List<Dependant> dependants) {
        this.dependants = dependants;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
