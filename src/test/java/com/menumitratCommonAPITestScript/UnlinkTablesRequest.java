package com.menumitratCommonAPITestScript;

public class UnlinkTablesRequest {
    private int outlet_id;
    private int primary_table_id;
    private int user_id;

    // Default constructor
    public UnlinkTablesRequest() {
    }

    // Parameterized constructor
    public UnlinkTablesRequest(int outlet_id, int primary_table_id, int user_id) {
        this.outlet_id = outlet_id;
        this.primary_table_id = primary_table_id;
        this.user_id = user_id;
    }

    // Getters and Setters
    public int getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(int outlet_id) {
        this.outlet_id = outlet_id;
    }

    public int getPrimary_table_id() {
        return primary_table_id;
    }

    public void setPrimary_table_id(int primary_table_id) {
        this.primary_table_id = primary_table_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UnlinkTablesRequest{" +
                "outlet_id=" + outlet_id +
                ", primary_table_id=" + primary_table_id +
                ", user_id=" + user_id +
                '}';
    }
} 