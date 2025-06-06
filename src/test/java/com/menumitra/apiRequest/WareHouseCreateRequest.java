package com.menumitra.apiRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WareHouseCreateRequest {
    @JsonProperty("user_id")
    private int userId;
    
    @JsonProperty("app_source")
    private String appSource;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("manager_name")
    private String managerName;
    
    @JsonProperty("manager_mobile")
    private String managerMobile;
    
    @JsonProperty("manager_alternate_mobile")
    private String managerAlternateMobile;
    
    @JsonProperty("warehouse_type")
    private String warehouseType;
    
    @JsonProperty("capacity_unit")
    private String capacityUnit;
    
    @JsonProperty("capacity_value")
    private int capacityValue;
    
    @JsonProperty("is_active")
    private int isActive;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerMobile() {
        return managerMobile;
    }

    public void setManagerMobile(String managerMobile) {
        this.managerMobile = managerMobile;
    }

    public String getManagerAlternateMobile() {
        return managerAlternateMobile;
    }

    public void setManagerAlternateMobile(String managerAlternateMobile) {
        this.managerAlternateMobile = managerAlternateMobile;
    }

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

    public String getCapacityUnit() {
        return capacityUnit;
    }

    public void setCapacityUnit(String capacityUnit) {
        this.capacityUnit = capacityUnit;
    }

    public int getCapacityValue() {
        return capacityValue;
    }

    public void setCapacityValue(int capacityValue) {
        this.capacityValue = capacityValue;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"user_id\":%d,\"app_source\":\"%s\",\"location\":\"%s\",\"address\":\"%s\"," +
            "\"manager_name\":\"%s\",\"manager_mobile\":\"%s\",\"manager_alternate_mobile\":\"%s\"," +
            "\"warehouse_type\":\"%s\",\"capacity_unit\":\"%s\",\"capacity_value\":%d,\"is_active\":%d}",
            userId, appSource, location, address, managerName, managerMobile, 
            managerAlternateMobile != null ? managerAlternateMobile : "", 
            warehouseType, capacityUnit, capacityValue, isActive
        );
    }
}
