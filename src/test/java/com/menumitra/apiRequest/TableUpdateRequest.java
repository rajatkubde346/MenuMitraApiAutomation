package com.menumitra.apiRequest;

public class TableUpdateRequest {
    private String table_number;
    private String new_table_number;
    private String section_id;
    private String outlet_id;
    private String order_id;
    private String user_id;

    public String getTable_number() {
        return table_number;
    }

    public void setTable_number(String table_number) {
        this.table_number = table_number;
    }

    public String getNew_table_number() {
        return new_table_number;
    }

    public void setNew_table_number(String new_table_number) {
        this.new_table_number = new_table_number;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}