package com.menumitra.apiRequest;

import java.util.List;

public class LoadOrderRequest {
    private String outlet_id;
    private String user_id;
    private int table_id;
    private String section_id;
    private String order_type;
    private String action;
    private String customer_name;
    private String customer_mobile;
    private String customer_address;
    private String customer_alternate_mobile;
    private String customer_landmark;
    private double special_discount;
    private double charges;
    private double tip;
    private String order_payment_settle_type;
    private List<OrderRequest.OrderItem> order_items;

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

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
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

    public double getSpecial_discount() {
        return special_discount;
    }

    public void setSpecial_discount(double special_discount) {
        this.special_discount = special_discount;
    }

    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public String getOrder_payment_settle_type() {
        return order_payment_settle_type;
    }

    public void setOrder_payment_settle_type(String order_payment_settle_type) {
        this.order_payment_settle_type = order_payment_settle_type;
    }

    public List<OrderRequest.OrderItem> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(List<OrderRequest.OrderItem> order_items) {
        this.order_items = order_items;
    }
}