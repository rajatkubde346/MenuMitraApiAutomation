package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.menumitra.apiRequest.ResendOTPRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class ResendOTPTestScript extends APIBase
{
    private String baseUri;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private ResendOTPRequest resendOTPRequest;
    private Response response;
	private URL url;
	Logger logger=Logger.getLogger(ResendOTPTestScript.class);

    
    @DataProvider(name="getResendURl")
    private Object[][] getResendURl() throws customException
    {
    	try
    	{
    		LogUtils.info("Starting to read resendOTP API endpoint data from Excel sheet");
    		Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,property.getProperty("commonAPI"));
    		LogUtils.info("Excel data read successfully for resendOTP API endpoint");
    		return Arrays.stream(readData)
    				.filter(row -> "resendOTP".contains(row[0].toString()))
    				.toArray(Object[][]::new);
    	}
    	catch (Exception e)
    	{
    		LogUtils.error("Failed to read resendOTP API endpoint data: " + e.getMessage());
            throw new customException("Error reading resendOTP API endpoint data from Excel sheet: " + e.getMessage());
		}
    }
    @DataProvider(name="getPositiveinputData")
    private Object[][] getPositiveinputData() throws customException
    {
        try
        {
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis,"CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0)
            {
                LogUtils.error("No positive test scenario data found in Excel sheet");
                throw new customException("No positive test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 2 &&
                    "resendotp".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }



            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;   
        }
    	catch (Exception e) 
    	{
			LogUtils.error("Failed to read positive input data: " + e.getMessage());
            throw new customException("Error reading positive input data from Excel sheet: " + e.getMessage());
		}
    }

    
    @DataProvider(name="getNegativeResendOPTData")
    private Object[][] getNegativeResendOPTData() throws customException {
        try {
            LogUtils.info("Reading negative test scenario data for resend OTP API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.error("No resend OTP API negative test scenario data found in Excel sheet");
                throw new customException("No resend OTP API negative test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            // Filter for resend OTP API negative test cases
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "resendotp".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            
            return obj;
        } catch (Exception e) {
            LogUtils.error("Failed to read resend OTP API negative test scenario data: " + e.getMessage());
            throw new customException("Error reading resend OTP API negative test scenario data: " + e.getMessage());
        }
    }
    
    @BeforeClass
    private void setupResendOTP() throws customException 
    {
    	try {
            LogUtils.info("=====Resend OTP Test Script Setup=====");
            //ExtentReport.createTest("Resend OTP Test Script Setup");
            resendOTPRequest=new ResendOTPRequest();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set resend OTP URL
            Object[][] resendOTPURLData =getResendURl();
            if (resendOTPURLData.length > 0) {
                String endpoint = resendOTPURLData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                //LogUtils.success(logger, "Constructed Resend OTP Base URI: " + baseUri);
                //ExtentReport.getTest().log(Status.INFO, "Constructed Resend OTP Base URI: " + baseUri);
            } else {
                LogUtils.failure(logger, "Failed constructed Resend OTP Base URI.");
                //ExtentReport.getTest().log(Status.ERROR, "Failed constructed Resend OTP Base URI.");
                throw new customException("Failed constructed Resend OTP Base URI.");
            }
            //check connection first
           // HandelConnections.checkConnection(baseUri,"post");

           LogUtils.success(logger, "Resend OTP test script Setup completed successfully");
           //ExtentReport.getTest().log(Status.INFO, "Resend OTP test script Setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during Resend OTP test script setup", e);
            //ExtentReport.getTest().log(Status.FAIL, "Error during Resend OTP test script setup " + e.getMessage());
            throw new customException("Error during Resend OTP setup: " + e.getMessage());
        }
    }

    @Test(dataProvider="getPositiveinputData",priority=1)
    private void verifyRessendOTPUSingPositiveInputData(String apiName,String testCaseid, String testType, String description,
    	    String httpsmethod,String requestBody,String expectedResponseBody,String statusCode) throws customException
    {
    	try
    	{
			LogUtils.info("====verify resend OTP using Valid Input data===="+description);
			ExtentReport.createTest("verify resend OTP using Valid Input data");
			ExtentReport.getTest().log(Status.INFO, "verify resend OTP using Valid Input data: "+description);
			LogUtils.info("constructing resend OTP base URI: "+baseUri);
			ExtentReport.getTest().log(Status.INFO, "constructing resend OTP base URI: "+baseUri);
    		if(apiName.equalsIgnoreCase("resendotp") && testType.equalsIgnoreCase("positive"))
    		{
    			requestBodyJson = new JSONObject(requestBody);
    			resendOTPRequest = new ResendOTPRequest();
    			resendOTPRequest.setMobile(requestBodyJson.getString("mobile"));
    			LogUtils.info("constructing resend OTP request");
    			ExtentReport.getTest().log(Status.INFO, "constructing resend OTP request");

    			response=ResponseUtil.getResponse(baseUri,resendOTPRequest, httpsmethod);
    			LogUtils.info("resend OTP API responded with status code: "+response.getStatusCode());
    			ExtentReport.getTest().log(Status.INFO, "resend OTP API responded with status code: "+response.getStatusCode());
    			if(response.getStatusCode()==200)
    			{
    				actualResponseBody = new JSONObject(response.getBody().asString());
					expectedResponse=new JSONObject(expectedResponseBody);
					//validateResponseBody.handleResponseBody(response, expectedResponse);
					LogUtils.success(logger, "resend OTP API responded with status code: "+response.getStatusCode());
					ExtentReport.getTest().log(Status.PASS, "resend OTP API responded with status code: "+response.getStatusCode());

    			}
    			else
				{
					LogUtils.failure(logger, "resend OTP API responded with status code: "+response.getStatusCode());
					ExtentReport.getTest().log(Status.FAIL, "resend OTP API responded with status code: "+response.getStatusCode());
				}
    		}
    	}
    	catch (Exception e) {
			LogUtils.exception(logger, "Error verifying resend OTP using positive input data: "+e.getMessage(), e);
			ExtentReport.getTest().log(Status.FAIL, "Error verifying resend OTP using positive input data: "+e.getMessage());
            throw new customException("Error verifying resend OTP using positive input data: " + e.getMessage());
		}
        
    }
    
/**
 * Test method for negative scenarios of Resend OTP API
 */
@Test(dataProvider = "getNegativeResendOPTData", priority = 2)
private void resendOTPusingInvalidData(String apiName, String testCaseId, 
    String testType, String description, String httpsMethod, 
    String requestBody, String expectedResponseBody, String statusCode) throws customException 
    {
    
    try {
        LogUtils.info("=====Starting Resend OTP API negative test=====");
        ExtentReport.createTest("Resend OTP using InValid Input data: "+description);
        ExtentReport.getTest().log(Status.INFO,"====Resend OTP using Valid Input data====");
        ExtentReport.getTest().log(Status.INFO, "Constructed Resend OTP Base URI: " + baseUri);
        if (apiName.contains("resendotp") && testType.contains("negative")) 
        {
            // Parse request and expected response
            requestBodyJson = new JSONObject(requestBody);
            expectedResponse = new JSONObject(expectedResponseBody);
            
            resendOTPRequest = new ResendOTPRequest();
            resendOTPRequest.setMobile(requestBodyJson.getString("mobile"));
            LogUtils.info("Resend OTP payload prepared ");
            ExtentReport.getTest().log(Status.INFO, "Resend OTP payload prepared ");
            
            // Make API call
            response = ResponseUtil.getResponse(baseUri, resendOTPRequest, httpsMethod);
            LogUtils.info("POST request executed for Resend OTP API");
            ExtentReport.getTest().log(Status.INFO, "POST request executed for Resend OTP API");
           
            // Validate response based on test case
            switch (testCaseId)
             {
                case "resendotp_002": // Empty mobile number
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        LogUtils.success(logger,"Resend OTP API responded with status code 200");
                        ExtentReport.getTest().log(Status.PASS, "Resend OTP API responded with status code 200");
                    break;
                    
                case "resendotp_003": // Less than 10 digits
                            validateResponseBody.handleResponseBody(response, expectedResponse);
                            LogUtils.success(logger,"Resend OTP API responded with status code 200");
                            ExtentReport.getTest().log(Status.PASS, "Resend OTP API responded with status code 200");
                    break;
                    
                case "resendotp_004": // Special characters
                            validateResponseBody.handleResponseBody(response, expectedResponse);
                            LogUtils.success(logger,"Resend OTP API responded with status code 200");
                            ExtentReport.getTest().log(Status.PASS, "Resend OTP API responded with status code 200");
                    break;
                    
                case "resendotp_005": // Invalid characters
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        LogUtils.success(logger,"Resend OTP API responded with status code 200");
                        ExtentReport.getTest().log(Status.PASS, "Resend OTP API responded with status code 200");
                    break;
                    
                default:
                    
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    LogUtils.success(logger,"Resend OTP API responded with status code 200");
                    ExtentReport.getTest().log(Status.PASS, "Resend OTP API responded with status code 200");
            }
            
            LogUtils.success(logger,"Successfully validated Resend OTP API negative test case: "+testCaseId);
            ExtentReport.getTest().log(Status.PASS, "Successfully validated Resend OTP API negative test case: " + testCaseId);
        }
    } catch (Exception e) {
        LogUtils.error("Error in negative test case " + testCaseId + ": " + e.getMessage());
        ExtentReport.getTest().log(Status.FAIL, "Error in negative test case " + testCaseId + ": " + e.getMessage());
        throw new customException("Error in negative test case " + testCaseId + ": " + e.getMessage());
    }
}
    
}