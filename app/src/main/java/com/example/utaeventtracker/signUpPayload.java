package com.example.utaeventtracker;

public class signUpPayload {
    private String name;
    private String username;
    private String email;
    private String password;
    private Integer utaId;
    private Integer zipcode;
    private Integer phNo;
    private String address;

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

    public Integer getPhNo() {
        return phNo;
    }

    public void setPhNo(Integer phNo) {
        this.phNo = phNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
