package com.menumitra.apiRequest;

public class WaiterRequest {
    private String user_id;
    private String outlet_id;
    private String name;
    private String mobile;
    private String address;
    private String aadhar_number;
    private String dob;
    private String email;
    private String update_user_id; // Used for update/view/delete operations
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    
    public String getOutlet_id() {
        return outlet_id;
    }
    
    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getAadhar_number() {
        return aadhar_number;
    }
    
    public void setAadhar_number(String aadhar_number) {
        this.aadhar_number = aadhar_number;
    }
    
    public String getDob() {
        return dob;
    }
    
    public void setDob(String dob) {
        this.dob = dob;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getupdate_user_id() {
        return update_user_id;
    }
    
    public void setupdate_user_id(String update_user_id) {
        this.update_user_id = update_user_id;
    }
}