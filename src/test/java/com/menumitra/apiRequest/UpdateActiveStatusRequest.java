package com.menumitra.apiRequest;

public class UpdateActiveStatusRequest {
    private String outlet_id;
    private String type;
    private String id;
    private boolean is_active;
    
    public String getOutlet_id() {
        return outlet_id;
    }
    
    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public boolean isIs_active() {
        return is_active;
    }
    
    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}