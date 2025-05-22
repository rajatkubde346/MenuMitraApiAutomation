package com.menumitra.apiRequest;

public class LogoutRequest
{
	private int user_id;
	private String role;
	private String app;
	
	
	public int getUser_id() 
	{
		return user_id;
	}
	public void setUser_id(int user_id) 
	{
		this.user_id = user_id;
	}
	public String getRole() 
	{
		return role;
	}
	public void setRole(String role) 
	{
		this.role = role;
	}
	public String getApp()
	{
		return app;
	}
	public void setApp(String app)
	{
		this.app = app;
	}
	
}
