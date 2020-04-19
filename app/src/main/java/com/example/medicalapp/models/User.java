package com.example.medicalapp.models;

public class User {

    public String email;
    public String name;
    public String phone;
    public String license;
    public String userType;


    public User() {
    }

    public User(String email, String name, String phone, String license, String userType) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.license = license;
        this.userType = userType;
    }
}
