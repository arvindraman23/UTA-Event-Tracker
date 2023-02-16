package com.example.utaeventtracker;

public class User {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String password;
    private Integer utaId;
    private Integer zipcode;
    private String address;
    private Integer phNo;
    private String barcode;
    private UserRole[] roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getUtaId() {
        return utaId;
    }

    public void setUtaId(Integer utaId) {
        this.utaId = utaId;
    }

    public Integer getZipcode() {
        return zipcode;
    }

    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPhNo() {
        return phNo;
    }

    public void setPhNo(Integer phNo) {
        this.phNo = phNo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public UserRole[] getRoles() {
        return roles;
    }

    public void setRoles(UserRole[] roles) {
        this.roles = roles;
    }
}
