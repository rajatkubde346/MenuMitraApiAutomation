package com.menumitra.apiRequest;

import java.util.List;

public class AddMenusToOrderRequest {
    private String order_id;
    private String outlet_id;
    private List<OrderItem> order_items;
    
    public String getOrder_id() {
        return order_id;
    }
    
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
    
    public String getOutlet_id() {
        return outlet_id;
    }
    
    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }
    
    public List<OrderItem> getOrder_items() {
        return order_items;
    }
    
    public void setOrder_items(List<OrderItem> order_items) {
        this.order_items = order_items;
    }
    
    public static class OrderItem {
        private String menu_id;
        private String quantity;
        private String portion_name;
        private String comment;
        
        public String getMenu_id() {
            return menu_id;
        }
        
        public void setMenu_id(String menu_id) {
            this.menu_id = menu_id;
        }
        
        public String getQuantity() {
            return quantity;
        }
        
        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
        
        public String getPortion_name() {
            return portion_name;
        }
        
        public void setPortion_name(String portion_name) {
            this.portion_name = portion_name;
        }
        
        public String getComment() {
            return comment;
        }
        
        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}