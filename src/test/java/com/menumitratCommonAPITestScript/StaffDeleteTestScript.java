package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.xml.transform.sax.SAXTransformerFactory;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Log;
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
public class StaffDeleteTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String access;
    private staffRequest staffDeleteRequest;
    private URL url;
    private int user_id;
    private JSONObject expectedResponseJson;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(StaffDeleteTestScript.class);

    @DataProvider(name="getStaffDeleteUrl")
    private Object[][] getStaffDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Delete API endpoint data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No Staff Delete API endpoint data found in Excel sheet");
                throw new customException("No Staff Delete API endpoint data found in Excel sheet");
            }

           
            LogUtils.info("Successfully retrieved Staff Delete API endpoint data");
            return Arrays.stream(readExcelData)
            		.filter(row->"staffDelete".equalsIgnoreCase(row[0].toString()))
            		.toArray(Object[][]::new);
            
        }
        catch(Exception e) {
            LogUtils.error("Error while reading Staff Delete API endpoint data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading Staff Delete API endpoint data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name = "getStaffDeleteData")
    public static Object[][] getStaffDeleteData() throws customException {
        try {
            LogUtils.info("Reading staff delete test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff delete test scenario data found in Excel sheet");
                throw new customException("No staff delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "staffdelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading staff delete test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading staff delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getStaffDeleteNegativeData")
    public Object[][] getStaffDeleteNegativeData() throws customException {
        try {
            LogUtils.info("Reading staff delete negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading staff delete negative test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "staffdelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid staff delete negative test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            return result;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting staff delete negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting staff delete negative test data: " + e.getMessage());
            throw new customException("Error in getting staff delete negative test data: " + e.getMessage());
        }
    }
    

    @BeforeClass
    private void staffDeleteSetUp() throws customException
    {
        try
        {
            LogUtils.info("Staff Delete SetUp");
            ExtentReport.createTest("Staff Delete SetUp");
            ExtentReport.getTest().log(Status.INFO,"Staff Delete SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getStaffDeleteUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No staff delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No staff delete URL found in test data");
                throw new customException("No staff delete URL found in test data");
            }
            
            access = TokenManagers.getJwtToken();
            if(access.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            user_id=TokenManagers.getUserId();
            staffDeleteRequest = new staffRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in staff delete setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in staff delete setup: " + e.getMessage());
            throw new customException("Error in staff delete setup: " + e.getMessage());
        }
    }

    //@Test(dataProvider = "getStaffDeleteData")
    public void testStaffDelete(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            ExtentReport.createTest("Staff Delete Test - " + testCaseid);
            LogUtils.info("Executing staff delete test for scenario: " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Executing staff delete test "+description);
            
            requestBodyJson = new JSONObject(requestBody);
            staffDeleteRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            staffDeleteRequest.setStaff_id(String.valueOf(String.valueOf(requestBodyJson.getInt("staff_id"))));
            staffDeleteRequest.setUser_id(user_id);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            response = ResponseUtil.getResponseWithAuth(baseURI, staffDeleteRequest, httpsmethod, access);
            
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
            
            // Validate response body
            actualJsonBody = new JSONObject(response.asString());
            if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                expectedResponseJson = new JSONObject(expectedResponseBody);
                
                if(!expectedResponseJson.similar(actualJsonBody)) {
                    String errorMsg = "Response body mismatch\nExpected: " + expectedResponseJson.toString(2) + "\nActual: " + actualJsonBody.toString(2);
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
            }

            // Success case
            String successMsg = "Staff deleted successfully";
            LogUtils.success(logger, successMsg + "\nResponse: " + response.asPrettyString());
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
            ExtentReport.getTest().log(Status.PASS, "Expected Response: " + (expectedResponseJson != null ? expectedResponseJson.toString(2) : "No expected response provided"));
            ExtentReport.getTest().log(Status.PASS, "Actual Response: " + actualJsonBody.toString(2));
            
        }
        catch (Exception e) 
        {
            String errorMsg = "Error during staff deletion test execution: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

  
    @Test(dataProvider = "getStaffDeleteNegativeData")
    public void staffDeleteNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff delete negative test case: " + testCaseid);
            ExtentReport.createTest("Staff Delete Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("staffdelete") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                staffDeleteRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                staffDeleteRequest.setStaff_id(String.valueOf(String.valueOf(requestBodyJson.getInt("staff_id"))));
                staffDeleteRequest.setUser_id(user_id);
                // Add more fields as needed for staff delete API
                
                // Log request details for debugging
                ExtentReport.getTest().log(Status.INFO, "Request URL: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Request Method: " + httpsmethod);
                ExtentReport.getTest().log(Status.INFO, "Request Headers: Authorization Bearer Token applied");
                
                // Execute API call
                response = ResponseUtil.getResponseWithAuth(baseURI, staffDeleteRequest, httpsmethod, access);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Step 1: Validate Status Code
                ExtentReport.getTest().log(Status.INFO, "STEP 1: Validating Status Code");
                
                // Check for server errors
                if (response.getStatusCode() == 500 || response.getStatusCode() == 502) {
                    LogUtils.failure(logger, "Server error detected with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error detected: " + response.getStatusCode(), ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
                // Validate status code
                else if (response.getStatusCode() != expectedStatusCode) {
                    LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                }
                else {
                    LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                    
                    // Step 2: Validate Response Body
                    ExtentReport.getTest().log(Status.INFO, "STEP 2: Validating Response Body");
                    actualJsonBody = new JSONObject(response.asString());
                    
                    // Display actual response for report
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:");
                    ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedResponseJson = new JSONObject(expectedResponseBody);
                        
                        // Display expected response for report
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body:");
                        ExtentReport.getTest().log(Status.INFO, expectedResponseJson.toString(4));
                        
                        // Step 2.1: Validate error message if applicable
                        ExtentReport.getTest().log(Status.INFO, "STEP 2.1: Validating Error Message");
                        if (expectedResponseJson.has("detail") && actualJsonBody.has("detail")) {
                            String expectedDetail = expectedResponseJson.getString("detail");
                            String actualDetail = actualJsonBody.getString("detail");
                            
                            if (expectedDetail.equals(actualDetail)) {
                                LogUtils.info("Error message validation passed: " + actualDetail);
                                ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                            } else {
                                LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail + ", Actual: " + actualDetail);
                            }
                        }
                        
                        // Step 2.2: Complete response body validation
                        ExtentReport.getTest().log(Status.INFO, "STEP 2.2: Validating Complete Response Structure");
                        validateResponseBody.handleResponseBody(response, expectedResponseJson);
                    }
                    
                    LogUtils.success(logger, "Staff delete negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff delete negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Step 3: Summary
                ExtentReport.getTest().log(Status.INFO, "STEP 3: Test Summary");
                ExtentReport.getTest().log(Status.INFO, "Test Case ID: " + testCaseid);
                ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
                ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
                ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Test Result: " + 
                    (response.getStatusCode() == expectedStatusCode ? "PASS" : "FAIL"));
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            }
        } catch (Exception e) {
            String errorMsg = "Error in staff delete negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
    
    
    
    
   
}
