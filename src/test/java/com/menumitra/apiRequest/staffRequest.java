package com.menumitra.apiRequest;

public class staffRequest 
{
    private String staff_id;
	private int outlet_id;
	private int user_id;
	private String staff_role;
	
	
	public String getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(String staff_id) {
		this.staff_id = staff_id;
	}	

	public int getOutlet_id() {
		return outlet_id;
	}

	public void setOutlet_id(int outlet_id) {			
        this.outlet_id = outlet_id;
    }

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getStaff_role() {
		return staff_role;
	}

	public void setStaff_role(String staff_role) {
		this.staff_role = staff_role;
	}
	
	
}
