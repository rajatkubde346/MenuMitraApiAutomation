package com.menumitratCommonAPITestScript;


import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.staffRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class StaffViewTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private staffRequest staffViewRequest;
    private URL url;
    Logger logger=LogUtils.getLogger(StaffViewTestScript.class);
    private JSONObject expectedResponseJson;
    private JSONObject actualResponseBody;
    @DataProvider(name="getStaffViewUrl")
    private Object[][] getStaffViewUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading staff view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading staff view URL from Excel sheet");
            
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData==null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "staffview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No staff view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved staff view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved staff view URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getStaffViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name="getstaffviewValidData")
    public Object[][] getstaffviewValidData() throws customException
    {
        try
        {
            LogUtils.info("Reading staff view valid data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading staff view valid data from Excel sheet");
            
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if(readExcelData==null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                    "staffview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            if(filteredData.isEmpty()) {
                String errorMsg = "No valid staff view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " staff view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " staff view test scenarios");
            return obj;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getstaffviewValidData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    
    @DataProvider(name="getstaffviewNegativeData")
    public Object[][] getstaffviewNegativeData() throws customException
    {
        try
        {
            LogUtils.info("Reading staff view negative data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading staff view negative data from Excel sheet");
            
            Object[][] readExcelData=DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if(readExcelData==null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                    "staffview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            if(filteredData.isEmpty()) {
                String errorMsg = "No negative staff view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " negative staff view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " negative staff view test scenarios");
            return obj;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getstaffviewNegativeData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
  
    @BeforeClass
    private void staffviewsetUp() throws customException
    {
        try
        {
            LogUtils.info("Setting up staff view test");
            ExtentReport.createTest("Staff View Test Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing staff view test setup");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI=EnviromentChanges.getBaseUrl();
            
            Object[][] staffViewData = getStaffViewUrl();
            if (staffViewData.length > 0) {
                String endpoint = staffViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No staff view URL found in test data";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            accessToken=TokenManagers.getJwtToken();
            if (accessToken.isEmpty()) 
            {
                String errorMsg = "Required tokens not found. Please ensure login and OTP verification is completed";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            staffViewRequest = new staffRequest();
            LogUtils.info("Staff view test setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff view test setup completed successfully", ExtentColor.GREEN));
        }
        catch(Exception e)
        {
            String errorMsg = "Error in staff view setUp: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider="getstaffviewValidData")
    private void verifyStaffView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException
    {
        try
        {
            LogUtils.info("Starting staff view test case: " + testCaseid);
            ExtentReport.createTest("Staff View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if(apiName.equalsIgnoreCase("staffview"))
            {
                requestBodyJson = new JSONObject(requestBody);
                staffViewRequest.setStaff_id(String.valueOf(requestBodyJson.getInt("staff_id")));
                staffViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, staffViewRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Validate status code
                if(response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                
                // Validate response body if expected response is provided
                if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    JSONObject expectedJson = new JSONObject(expectedResponseBody);
                    JSONObject actualJson = new JSONObject(response.asString());
                    
                    if(!expectedJson.similar(actualJson)) {
                        String errorMsg = "Response body mismatch\nExpected: " + expectedJson.toString(2) + "\nActual: " + actualJson.toString(2);
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        throw new customException(errorMsg);
                    }
                }
                
                LogUtils.success(logger, "Staff view test completed successfully\nResponse: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff view test completed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
            }
        }
        catch (Exception e)
        {
            String errorMsg = "Error in staff view test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
      
   
    
    @Test(dataProvider="getstaffviewNegativeData")
    private void verifyStaffViewNegative(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException
    {
        try
        {
            LogUtils.info("Starting staff view negative test case: " + testCaseid);
            ExtentReport.createTest("Staff View Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if(apiName.equalsIgnoreCase("staffview") && testType.equalsIgnoreCase("negative"))
            {
                requestBodyJson = new JSONObject(requestBody);
                staffViewRequest = new staffRequest();
                
                // Handle type conversion safely for staff_id
                if(requestBodyJson.has("staff_id")) {
                    Object staffIdValue = requestBodyJson.get("staff_id");
                    // Convert any type to string
                    staffViewRequest.setStaff_id(String.valueOf(staffIdValue));
                }
                
                // Handle type conversion safely for outlet_id
                if(requestBodyJson.has("outlet_id")) {
                    
                        // First try to get as int
                        int outletId = requestBodyJson.getInt("outlet_id");
                        staffViewRequest.setOutlet_id(outletId);
                   
                }
               
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, staffViewRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Check for server errors
                if (response.getStatusCode() == 500 || response.getStatusCode() == 502) {
                    LogUtils.failure(logger, "Server error detected with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error: " + response.getStatusCode(), ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
                // Validate status code
                else if (response.getStatusCode() != expectedStatusCode) {
                    LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
                // Validate response body
                else {
                    LogUtils.info("Status code validation passed: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                    
                    // Validate response body if expected response is provided
                    if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        try {
                            expectedResponseJson = new JSONObject(expectedResponseBody);
                            actualResponseBody = new JSONObject(response.asString());
                            
                            // Use validateResponseBody to validate the full response body structure
                            validateResponseBody.handleResponseBody(response, expectedResponseJson);
                            
                            // For more visible validation of specific error fields
                            if(expectedResponseJson.has("detail") && actualResponseBody.has("detail")) {
                                String expectedDetail = expectedResponseJson.getString("detail");
                                String actualDetail = actualResponseBody.getString("detail");
                                
                                if(expectedDetail.equals(actualDetail)) {
                                    LogUtils.info("Error message validation passed: " + actualDetail);
                                    ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                                } else {
                                    LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                    ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                }
                            }
                            
                            // Also check for "msg" field which is common in error responses
                            if(expectedResponseJson.has("msg") && actualResponseBody.has("msg")) {
                                String expectedMsg = expectedResponseJson.getString("msg");
                                String actualMsg = actualResponseBody.getString("msg");
                                
                                if(expectedMsg.equals(actualMsg)) {
                                    LogUtils.info("Error message validation passed: " + actualMsg);
                                    ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualMsg);
                                } else {
                                    LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedMsg + ", Actual: " + actualMsg);
                                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                    ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedMsg + ", Actual: " + actualMsg);
                                }
                            }
                            
                            LogUtils.success(logger, "Response body validation passed");
                            ExtentReport.getTest().log(Status.PASS, "Response body validation passed");
                        } catch(Exception e) {
                            LogUtils.failure(logger, "Error validating response body: " + e.getMessage());
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error validating response body", ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
                        }
                    }
                    
                    LogUtils.success(logger, "Staff view negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff view negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response for reference
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            }
        }
        catch (Exception e)
        {
            String errorMsg = "Error in staff view negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
}

