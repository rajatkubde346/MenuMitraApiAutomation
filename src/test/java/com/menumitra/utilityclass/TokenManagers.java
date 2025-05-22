package com.menumitra.utilityclass;
import java.net.URL;
import org.json.JSONObject;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.Status;
import com.google.gson.JsonObject;
import com.menumitra.apiRequest.LogoutRequest;
import com.menumitra.apiRequest.loginRequest;
import com.menumitra.apiRequest.verifyOTPRequest;
import com.menumitra.superclass.APIBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class TokenManagers
{
    private static String jwtToken; // JWT token for authentication
    private static int userId; // User ID for user identification
    private static String role; // User role for access control
    private static String app; // App type for app-specific logic
    private static JSONObject acutalresponse;
    

	public static void setJwtToken(String jwtToken) 
	{
		TokenManagers.jwtToken = jwtToken;
	}

	public static void setUserId(int userId)
	{
		TokenManagers.userId = userId;
	}

	public static void setRole(String role) 
	{
		TokenManagers.role = role;
	}

	public static void setApp(String app) {
		
		TokenManagers.app = app;
	}
   
    public static String getJwtToken() 
    {
        if (jwtToken == null)
        {
            LogUtils.warn("JWT token is null - Authentication may fail");
        }
        return jwtToken;
    }

    
    public static int getUserId() 
    {
       
        return userId;
    }

    
    public static String getRole() 
    {
        if (role == null)
        {
            LogUtils.warn("Role is null - Role-based access may fail");
        }
        return role;
    }

    
    public static String getApp() 
    {
        if (app == null) 
        {
            LogUtils.warn("App type is null - App-specific logic may fail");
        }
        return app;
    }
    
    public static void clearTokens()
    {
        LogUtils.info("Starting token cleanup process");
        jwtToken = null; // Clearing JWT token
        userId=0; // Clearing user ID
        role = null; // Clearing user role
        app = null; // Clearing app type
        LogUtils.info("All tokens and user information cleared successfully");
    }
}
