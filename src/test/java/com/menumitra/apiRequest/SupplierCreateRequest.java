package com.menumitra.apiRequest;

import java.util.List;
import java.util.ArrayList;

public class SupplierCreateRequest {
    private int userId;
    private String appSource;
    private String companyName;
    private String address;
    private String name;
    private String mobile;
    private String alternateMobile;
    private String gstin;
    private String settlementFrequency;
    private int settlementFrequencyValue;
    private double creditLimit;
    private int isActive;
    private List<BankDetail> bankDetails;
    private List<UpiDetail> upiDetails;

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAppSource() {
        return appSource;
    }

    public void setAppSource(String appSource) {
        this.appSource = appSource;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAlternateMobile() {
        return alternateMobile;
    }

    public void setAlternateMobile(String alternateMobile) {
        this.alternateMobile = alternateMobile;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getSettlementFrequency() {
        return settlementFrequency;
    }

    public void setSettlementFrequency(String settlementFrequency) {
        this.settlementFrequency = settlementFrequency;
    }

    public int getSettlementFrequencyValue() {
        return settlementFrequencyValue;
    }

    public void setSettlementFrequencyValue(int settlementFrequencyValue) {
        this.settlementFrequencyValue = settlementFrequencyValue;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public List<BankDetail> getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(List<BankDetail> bankDetails) {
        this.bankDetails = bankDetails;
    }

    public List<UpiDetail> getUpiDetails() {
        return upiDetails;
    }

    public void setUpiDetails(List<UpiDetail> upiDetails) {
        this.upiDetails = upiDetails;
    }
}