package com.example.medicalapp.models;

public class Profile {
   public  String name;
   public  String address;
   public  String speciality;
   public  String gender;
   public  String qualification;

    public Profile() {
    }

    public Profile(String name, String address, String speciality, String gender, String qualification) {
        this.name = name;
        this.address = address;
        this.speciality = speciality;
        this.gender = gender;
        this.qualification = qualification;
    }
}
