package com.example.myapplication.models;

import java.io.Serializable;

public class User implements Serializable {
    String id, fname, lname, phone, email, password;
    Integer dailycal;
    String gender;
    Double height;
    Double weight;
    Integer age;

    public User() {
    }

    public User(String id, String fname, String lname, String phone, String email, String password, Integer dailycal, String gender, Double height, Double weight, Integer age) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.dailycal = dailycal;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public User(User user) {
        this.id = user.id;
        this.fname = user.fname;
        this.lname = user.lname;
        this.phone = user.phone;
        this.email = user.email;
        this.password = user.password;
        this.dailycal = user.dailycal;
        this.gender = user.gender;
        this.height = user.height;
        this.weight = user.weight;
        this.age = user.age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDailycal() {
        return dailycal;
    }

    public void setDailycal(Integer dailycal) {
        this.dailycal = dailycal;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", dailycal='" + dailycal + '\'' +
                ", gender='" + gender + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
