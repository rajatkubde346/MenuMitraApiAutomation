package com.menumitra.apiRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WareHouseDeleteRequest {
    @JsonProperty("user_id")
    private int userId;
    
    @JsonProperty("app_source")
    private String appSource;
    
    @JsonProperty("warehouse_id")
    private int warehouseId;

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

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"user_id\":%d,\"app_source\":\"%s\",\"warehouse_id\":%d}",
            userId, appSource != null ? appSource : "", warehouseId
        );
    }
}
