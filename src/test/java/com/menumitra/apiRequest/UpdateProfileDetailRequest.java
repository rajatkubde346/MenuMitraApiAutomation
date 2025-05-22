package com.menumitra.apiRequest;

public class UpdateProfileDetailRequest {
    private String update_user_id;
    private String user_id;
    private String name;
    private String email;
    private String mobile_number;
    private String dob;
    private String aadhar_number;
    
    public String getUpdate_user_id() {
        return update_user_id;
    }
    
    public void setUpdate_user_id(String update_user_id) {
        this.update_user_id = update_user_id;
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
    
    public String getMobile_number() {
        return mobile_number;
    }
    
    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
    
    public String getDob() {
        return dob;
    }
    
    public void setDob(String dob) {
        this.dob = dob;
    }
    
    public String getAadhar_number() {
        return aadhar_number;
    }
    
    public void setAadhar_number(String aadhar_number) {
        this.aadhar_number = aadhar_number;
    }
}