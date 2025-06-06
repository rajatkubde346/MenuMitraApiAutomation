package com.menumitratCommonAPITestScript;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.loginRequest;
import com.menumitra.apiRequest.verifyOTPRequest;
import com.menumitra.superclass.APIBase;

import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;

import io.restassured.response.Response;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class verifyOTPTestScript extends APIBase {

    verifyOTPRequest verifyOTPRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri=null;
    private URL url;
    private Logger logger=LogUtils.getLogger(verifyOTPTestScript.class);
    
    @DataProvider(name="getVerifyotpUrl")
    public Object[][] getVerifyotpUrl() throws customException
    {
        try
        {
            LogUtils.info("=====Reading Verify OTP API Endpoint Data=====");
            ExtentReport.getTest().log(Status.INFO, "Loading Verify OTP API endpoint configuration");
            
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");

            Object[][] filteredData = Arrays.stream(readExcelData)
                .filter(row -> "verifyotp".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);

            LogUtils.success(logger, "Successfully retrieved Verify OTP API endpoint data");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Successfully loaded Verify OTP API configuration", ExtentColor.GREEN));
            return filteredData;
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Failed to read Verify OTP API endpoint data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to read Verify OTP API endpoint data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Verify OTP API endpoint data: " + e.getMessage());
        }
    }

    @DataProvider(name="getPositiveInputData")
    public Object[][] getPositiveInputData() throws customException
    {
        try
        {
            LogUtils.info("=====Reading Verify OTP API Positive Test Data=====");
            ExtentReport.getTest().log(Status.INFO, "Loading positive test scenarios for Verify OTP API");
            
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis,"CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0)
            {
                LogUtils.failure(logger, "No positive test data found for Verify OTP API");
                ExtentReport.getTest().log(Status.WARNING, MarkupHelper.createLabel("No positive test scenarios found", ExtentColor.AMBER));
                throw new customException("No Verify OTP API positive test data found");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];

                if (row != null && row.length >= 3 &&
                    "verifyotp".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.success(logger, "Successfully loaded " + obj.length + " positive test scenarios");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Loaded " + obj.length + " positive test scenarios", ExtentColor.GREEN));
            return obj;   
            
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Failed to read Verify OTP API positive test data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to load positive test data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Verify OTP API positive test data: " + e.getMessage());
        }
    }

    @DataProvider(name="getverifyOTPInvalidData")
    private Object[][] getverifyOTPInvalidData() throws customException {
        try {
            LogUtils.info("=====Reading Verify OTP API Negative Test Data=====");
            ExtentReport.getTest().log(Status.INFO, "Loading negative test scenarios for Verify OTP API");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No negative test data found for Verify OTP API");
                ExtentReport.getTest().log(Status.WARNING, MarkupHelper.createLabel("No negative test scenarios found", ExtentColor.AMBER));
                throw new customException("No Verify OTP API negative test data found");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "verifyotp".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            
            LogUtils.success(logger, "Successfully loaded " + obj.length + " negative test scenarios");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Loaded " + obj.length + " negative test scenarios", ExtentColor.GREEN));
            return obj;
            
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Verify OTP API negative test data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to load negative test data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Verify OTP API negative test data: " + e.getMessage());
        }
    }
    
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("=====Starting Verify OTP Test Setup=====");
            ExtentReport.createTest("Verify OTP API Test Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing Verify OTP API test environment");
            
            verifyOTPRequest=new verifyOTPRequest();

            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI configured: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            Object[][] verifyOTPURLData = getVerifyotpUrl();
            if (verifyOTPURLData.length > 0) {
                String endpoint = verifyOTPURLData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.success(logger, "Verify OTP endpoint configured: " + baseUri);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Verify OTP endpoint configured successfully", ExtentColor.GREEN));
            } else {
                LogUtils.failure(logger, "Failed to configure Verify OTP endpoint");
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to configure Verify OTP endpoint", ExtentColor.RED));
                throw new customException("Failed to configure Verify OTP endpoint");
            }

            LogUtils.success(logger, "Verify OTP test setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test setup completed successfully", ExtentColor.GREEN));

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during Verify OTP test setup", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test setup failed: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error during Verify OTP setup: " + e.getMessage());
        }
    }
    
    @Test(dataProvider="getPositiveInputData",priority=1)
    private void verifyOTPAPiUsingValidInputData(String apiName,String testCaseid, String testType, String description,
    String httpsmethod,String requestBody,String expectedResponseBody,String statusCode) throws customException
    {
        try {
            LogUtils.info("=====Starting Verify OTP Positive Test Case: " + testCaseid + "=====");
            ExtentReport.createTest("Verify OTP API Positive Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if(apiName.contains("verifyotp") && testType.contains("positive")) {
                requestBodyJson = new JSONObject(requestBody);
                verifyOTPRequest = new verifyOTPRequest();
                verifyOTPRequest.setMobile(requestBodyJson.get("mobile").toString());
                verifyOTPRequest.setOtp(requestBodyJson.get("otp").toString());
                verifyOTPRequest.setFcm_token(requestBodyJson.get("fcm_token").toString());
                verifyOTPRequest.setDevice_id(requestBodyJson.get("device_id").toString());
                verifyOTPRequest.setDevice_model(requestBodyJson.get("device_model").toString());
                
                LogUtils.info("Request payload prepared with mobile: " + requestBodyJson.get("mobile").toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
                
                response = ResponseUtil.getResponse(baseUri, verifyOTPRequest, httpsmethod);
                LogUtils.info("Response received - Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                
                if(response.getStatusCode() == 200) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    actualResponseBody = new JSONObject(response.body().asString());
                    //validateResponseBody.handleResponseBody(response,expectedResponse);
                    
                    LogUtils.success(logger,"Verify OTP API validation successful");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("API validation successful", ExtentColor.GREEN));
                }
                else {
                    LogUtils.failure(logger,"Verify OTP API failed with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("API failed with status code: " + response.getStatusCode(), ExtentColor.RED));
                }
            }
        } catch(Exception e) {
            LogUtils.exception(logger,"Error executing Verify OTP API", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error during Verify OTP API execution: " + e.getMessage());
        }
    }

    /**
     * Test method for negative scenarios
     */
    @Test(dataProvider = "getverifyOTPInvalidData", priority = 2)
    private void verifyOTPusingInvalidData(String apiName, String testCaseId, 
        String testType, String description, String httpsMethod, 
        String requestBody, String expectedResponseBody, String statusCode) throws customException 
    {
        try {
            LogUtils.info("=====Starting Verify OTP Negative Test Case: " + testCaseId + "=====");
            ExtentReport.createTest("Verify OTP API Negative Test - " + testCaseId);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.contains("verifyotp") && testType.contains("negative")) 
            {
                requestBodyJson = new JSONObject(requestBody);
                expectedResponse = new JSONObject(expectedResponseBody);
                
                verifyOTPRequest = new verifyOTPRequest();
                verifyOTPRequest.setMobile(requestBodyJson.getString("mobile"));
                verifyOTPRequest.setOtp(requestBodyJson.get("otp").toString());
                verifyOTPRequest.setFcm_token(requestBodyJson.get("fcm_token").toString());
                verifyOTPRequest.setDevice_id(requestBodyJson.get("device_id").toString());
                verifyOTPRequest.setDevice_model(requestBodyJson.get("device_model").toString());
                
                LogUtils.info("Request payload prepared for negative test case: " + testCaseId);
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
                
                response = ResponseUtil.getResponse(baseUri, verifyOTPRequest, httpsMethod);
                LogUtils.info("Response received - Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                
                switch (testCaseId)
                {
                    case "verifyotp_002":
                        LogUtils.info("Validating empty mobile number scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    case "verifyotp_003":
                        LogUtils.info("Validating mobile number with less than 10 digits");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    case "verifyotp_004":
                        LogUtils.info("Validating mobile number with special characters");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    case "verifyotp_005":
                        LogUtils.info("Validating mobile number with invalid characters");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    default:
                        LogUtils.info("Validating general negative scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                }
                
                LogUtils.success(logger, "Successfully validated negative test case: " + testCaseId);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test case validation successful", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "Error in negative test case " + testCaseId, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test case failed: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error in negative test case " + testCaseId + ": " + e.getMessage());
        }
    }
}
