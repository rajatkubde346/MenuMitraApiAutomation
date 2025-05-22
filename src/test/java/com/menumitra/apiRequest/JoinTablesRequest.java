package com.menumitra.apiRequest;

import java.util.List;

public class JoinTablesRequest {
    private int outlet_id;
    private int section_id;
    private int primary_table_id;
    private List<Integer> tables_to_join;
    private int user_id;

    public int getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(int outlet_id) {
        this.outlet_id = outlet_id;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }

    public int getPrimary_table_id() {
        return primary_table_id;
    }

    public void setPrimary_table_id(int primary_table_id) {
        this.primary_table_id = primary_table_id;
    }

    public List<Integer> getTables_to_join() {
        return tables_to_join;
    }

    public void setTables_to_join(List<Integer> tables_to_join) {
        this.tables_to_join = tables_to_join;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}