package com.menumitra.apiRequest;

public class ChangeSettingsRequest {
    private String outlet_id;
    private int user_id;
    private String type;
    private boolean value;

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}