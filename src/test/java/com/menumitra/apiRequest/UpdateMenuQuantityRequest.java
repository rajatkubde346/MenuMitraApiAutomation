package com.menumitra.apiRequest;

public class UpdateMenuQuantityRequest {

	private String order_id;
	private String outlet_id;
	private String quantity;
	private String menu_id;
	private String user_id;
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
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getMenu_id() {
		return menu_id;
	}
	public void setMenu_id(String menu_id) {
		this.menu_id = menu_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	
	
}
