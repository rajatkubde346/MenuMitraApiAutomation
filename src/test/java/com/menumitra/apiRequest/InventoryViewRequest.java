package com.menumitra.apiRequest;

public class InventoryViewRequest 
{
    private String inventory_id;
    private String outlet_id;
    
    public String getInventory_id() {
        return inventory_id;
    }
    
    public void setInventory_id(String inventory_id) {
        this.inventory_id = inventory_id;
    }
    
    public String getOutlet_id() {
        return outlet_id;
    }
    
    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }
}