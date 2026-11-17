package com.guerr.attendancecontrol.data.model;

import com.google.gson.annotations.SerializedName;

public class UserCreateDTO {
    @SerializedName("rut")
    private String rut;

    @SerializedName("name")
    private String name;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public UserCreateDTO() {}

    public UserCreateDTO(String rut, String name, String lastName, String phone, String address, String email, String password) {
        this.rut = rut;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.password = password;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
