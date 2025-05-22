package com.menumitra.apiRequest;

import java.util.List;

public class OrderRequest {
    private String outlet_id;
    private String user_id;
    private List<Integer> tables;
    private Integer section_id;
    private String order_type;
    private String payment_method;
    private List<Object> order_items;
    private String action;
    private String customer_name;
    private String customer_mobile;
    private String customer_address;
    private String customer_alternate_mobile;
    private String customer_landmark;
    private String special_discount;
    private String charges;
    private String tip;
    private String order_id;
    private String order_status;
    private String order_number;
    private String payment_status;
    private String payment_mode;

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public static class OrderItem {
        private int menu_id;
        private int quantity;
        private String portion_name;
        private String comment;

        public int getMenu_id() {
            return menu_id;
        }

        public void setMenu_id(int menu_id) {
            this.menu_id = menu_id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
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

    // Getters and Setters
    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Integer> getTables() {
        return tables;
    }

    public void setTables(List<Integer> tables) {
        this.tables = tables;
    }

    public Integer getSection_id() {
        return section_id;
    }

    public void setSection_id(Integer section_id) {
        this.section_id = section_id;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public List<Object> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(List<Object> order_items) {
        this.order_items = order_items;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_mobile() {
        return customer_mobile;
    }

    public void setCustomer_mobile(String customer_mobile) {
        this.customer_mobile = customer_mobile;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_alternate_mobile() {
        return customer_alternate_mobile;
    }

    public void setCustomer_alternate_mobile(String customer_alternate_mobile) {
        this.customer_alternate_mobile = customer_alternate_mobile;
    }

    public String getCustomer_landmark() {
        return customer_landmark;
    }

    public void setCustomer_landmark(String customer_landmark) {
        this.customer_landmark = customer_landmark;
    }

    public String getSpecial_discount() {
        return special_discount;
    }

    public void setSpecial_discount(String special_discount) {
        this.special_discount = special_discount;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}
    
    
    
}