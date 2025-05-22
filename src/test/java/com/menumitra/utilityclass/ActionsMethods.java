package com.menumitra.utilityclass;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.menumitra.apiRequest.LogoutRequest;
import com.menumitra.apiRequest.loginRequest;
import com.menumitra.apiRequest.verifyOTPRequest;
import com.menumitra.superclass.APIBase;

import io.restassured.response.Response;

public class ActionsMethods 
{

	private JSONObject requestBody;
	private static JSONObject actionsMethodActualresponse;
	private static Response response;
	private static Logger logger=LogUtils.getLogger(ActionsMethods.class);
	
	

	public static void login() throws customException {
	    try {
	        LogUtils.info("===== Starting Login API Execution =====");
	        
	        ExtentReport.getTest().log(Status.INFO, "===== Starting Login API Execution =====");
	        
	        String baseURI = EnviromentChanges.getBaseUrl() + APIBase.property.getProperty("loginEndpoint");
	        LogUtils.info("Constructed Login Base URI: " + baseURI);
	        ExtentReport.getTest().log(Status.INFO, "Constructed Login Base URI: " + baseURI);

	        loginRequest login = new loginRequest();
	        login.setMobile(APIBase.property.getProperty("mobile"));
	        LogUtils.info("Login payload prepared with mobile");
	        ExtentReport.getTest().log(Status.INFO, "Login payload prepared with mobile: " + login.getMobile());

	        response = ResponseUtil.getResponse(baseURI, login, APIBase.property.getProperty("httpmethod"));
	        LogUtils.info("POST request executed for login API");
	        ExtentReport.getTest().log(Status.INFO, "POST request executed for login API");

	        if (response.getStatusCode() == 200) 
	        {
	            LogUtils.success(logger, "Login API responded with status code 200");
	            ExtentReport.getTest().log(Status.PASS, "Login API responded with status code 200");
	           ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseURI);
	        } 
	        else 
	        {
	            LogUtils.failure(logger, "Login API failed. Status code: " + response.getStatusCode());
	            ExtentReport.getTest().log(Status.FAIL, "Login API failed. Status code: " + response.getStatusCode());
	            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseURI);
	        }

	    } catch (Exception e) {
	        LogUtils.exception(logger, "Error while executing login API:", e);
	        ExtentReport.getTest().log(Status.FAIL, "Error while executing login API: " + e.getMessage());
	        throw new customException("[EXCEPTION] Error during login API execution: " + e.getMessage());
	    }
	}

    
	
    public static void verifyOTP() throws customException
    {
    	try
    	{
    		LogUtils.info("===== Starting VerifyOTP API Execution =====");
    		
    		ExtentReport.getTest().log(Status.INFO, "===== Starting VerifyOTP API Execution =====");
    		
    		String baseURI=EnviromentChanges.getBaseUrl()+APIBase.property.getProperty("verifyOTP");
    		LogUtils.info("Constructed Verify OTP Base URI: " + baseURI);
    		ExtentReport.getTest().log(Status.INFO, "Constructed Verify OTP Base URI: " + baseURI);
    		
    		verifyOTPRequest verifyotp=new verifyOTPRequest();
    		verifyotp.setMobile(APIBase.property.getProperty("mobile"));
    		verifyotp.setOtp(APIBase.property.getProperty("otp"));
    		verifyotp.setFcm_token(APIBase.property.getProperty("fcm_token"));
    		verifyotp.setDevice_id(APIBase.property.getProperty("device_id"));
    		verifyotp.setDevice_model(APIBase.property.getProperty("device_model"));
    		LogUtils.info("Verify OTP payload prepared with mobile: ");
    		ExtentReport.getTest().log(Status.INFO, "Verify OTP payload prepared");
    		
    		response=ResponseUtil.getResponse(baseURI,verifyotp,APIBase.property.getProperty("httpmethod"));
    		LogUtils.info("POST request executed for Verify OTP API");
    		ExtentReport.getTest().log(Status.INFO, "POST request executed for Verify OTP API");
    		if(response.getStatusCode()==200)
    		{
    			actionsMethodActualresponse=new JSONObject(response.asPrettyString());
    			TokenManagers.setJwtToken(actionsMethodActualresponse.getString("access_token"));
    			TokenManagers.setUserId(actionsMethodActualresponse.getInt("user_id"));
    			TokenManagers.setRole(actionsMethodActualresponse.getString("role"));
    			LogUtils.success(logger,"verify OTP API responded with status code 200");
    			ExtentReport.getTest().log(Status.PASS, "verify OTP API responded with status code 200");
    			
    		}
    		else
    		{
    			LogUtils.failure(logger,"Verify OTP API failed. Status code:");
    			ExtentReport.getTest().log(Status.FAIL, "Verify OTP API failed. Status code: " + response.getStatusCode());
    		}
    		
    	}
    	catch(Exception e)
    	{
    		LogUtils.exception(logger,"Error while executing login API:", e);
    		ExtentReport.getTest().log(Status.FAIL, "Error while executing login API: " + e.getMessage());
	        throw new customException("[EXCEPTION] Error during login API execution: " + e.getMessage());
    	}
    }
    
	
    public static void logout() throws customException
    {
    
    	try
    	{
    		LogUtils.info("===== Starting Logout API Execution =====");
    		ExtentReport.createTest("Logout");
    		ExtentReport.getTest().log(Status.INFO, "===== Starting Logout API Execution =====");
    		
    		String baseURI=EnviromentChanges.getBaseUrl()+APIBase.property.getProperty("logout");
    		LogUtils.info("Constructed Logout Base URI: " + baseURI);
    		ExtentReport.getTest().log(Status.INFO, "Constructed Logout Base URI: " + baseURI);
    		
    		LogoutRequest logout=new LogoutRequest();
    		logout.setUser_id(TokenManagers.getUserId());
    		logout.setRole(TokenManagers.getRole());
    		logout.setApp(TokenManagers.getRole());
    		LogUtils.info("Logout payload prepared");
    		ExtentReport.getTest().log(Status.INFO, "Logout payload prepared with mobile: ");
    		
    		response=ResponseUtil.getResponse(baseURI,logout,APIBase.property.getProperty("httpmethod"));
    		LogUtils.info("POST request executed for Logout API");
    		ExtentReport.getTest().log(Status.INFO, "POST request executed for Logout API");
    		
    		if(response.getStatusCode()==200)
    		{
    			LogUtils.success(logger,"[SUCCESS] Logout user successfully");
    			ExtentReport.getTest().log(Status.PASS, "[SUCCESS] Logout user successfully");
    		}
    		else
    		{
    			LogUtils.failure(logger,"[FAILURE] Logout failed. Status code: " + response.getStatusCode()
                + " | Response: " + response.getBody().asString());
    			ExtentReport.getTest().log(Status.FAIL, "[FAILURE] Logout failed. Status code: " + response.getStatusCode()
                + " | Response: " + response.getBody().asString());
    		}
    		
    	}
    	catch (Exception e)
    	{
    		LogUtils.exception(logger,"Error while executing logout API:", e);
    		ExtentReport.getTest().log(Status.FAIL, "Error while executing logout API: " + e.getMessage());
	        throw new customException("[EXCEPTION] Error during logout API execution: " + e.getMessage());
		}
    }
}
