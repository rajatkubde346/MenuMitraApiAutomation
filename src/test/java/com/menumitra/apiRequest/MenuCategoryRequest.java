package com.menumitra.apiRequest;

public class MenuCategoryRequest {
    private String outlet_id;
    private String menu_cat_id;
    private String user_id;
    
    public String getOutlet_id() {
        return outlet_id;
    }
    
    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }
    
    public String getMenu_cat_id() {
        return menu_cat_id;
    }
    
    public void setMenu_cat_id(String menu_cat_id) {
        this.menu_cat_id = menu_cat_id;
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}