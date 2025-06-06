package com.menumitra.apiRequest;

public class WareHouseUpdateRequest {
    private int userId;
    private int warehouseId;
    private String appSource;
    private String location;
    private String address;
    private String managerName;
    private String managerMobile;
    private String managerAlternateMobile;
    private String warehouseType;
    private String capacityUnit;
    private int capacityValue;
    private int isActive;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
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
}
