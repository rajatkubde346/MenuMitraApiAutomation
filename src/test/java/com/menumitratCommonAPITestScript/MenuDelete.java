package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.MenuRequest;
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
public class MenuDelete extends APIBase
{
    private MenuRequest menuDeleteRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(MenuDelete.class);

    /**
     * Data provider for menu delete API endpoint URLs
     */
    @DataProvider(name = "getMenuDeleteUrl")
    public static Object[][] getMenuDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Delete API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "menuDelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Menu Delete API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Menu Delete API endpoint data from Excel sheet");
            throw new customException("Error While Reading Menu Delete API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for menu delete test scenarios
     */
    @DataProvider(name = "getMenuDeleteData")
    public static Object[][] getMenuDeleteData() throws customException {
        try {
            LogUtils.info("Reading menu delete test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No menu delete test scenario data found in Excel sheet");
                throw new customException("No menu delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "menuDelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for menu delete");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading menu delete test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading menu delete test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading menu delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }

  
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for menu delete test====");
            ExtentReport.createTest("Menu Delete Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set menu delete URL
            Object[][] menuDeleteData = getMenuDeleteUrl();
            if (menuDeleteData.length > 0) {
                String endpoint = menuDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for menu delete: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu delete URL found in test data");
                throw new customException("No menu delete URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            menuDeleteRequest = new MenuRequest();
            LogUtils.success(logger, "Menu Delete Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Menu Delete Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during menu delete setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during menu delete setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to delete menu
     */
    @Test(dataProvider = "getMenuDeleteData")
    private void deleteMenu(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting menu deletion test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Menu Deletion Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            menuDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            menuDeleteRequest.setUser_id(String.valueOf(user_id));
            
            // Set menu_id which is required for deletion
            menuDeleteRequest.setMenu_id(requestBodyJson.getString("menu_id"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, menuDeleteRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                if (response.asString() != null && !response.asString().isEmpty()) {
                    actualResponseBody = new JSONObject(response.asString());
                    
                    if (!actualResponseBody.isEmpty() && expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedResponse = new JSONObject(expectedResponseBody);
                        
                        ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                        LogUtils.info("Starting response body validation");
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                        LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                        ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                        LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                        
                        ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                        LogUtils.info("Performing detailed response validation");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        
                        ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                        LogUtils.success(logger, "Response body validation passed successfully");
                    }
                }
                
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu deleted successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Menu deleted successfully");
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            LogUtils.error("Stack trace: " + Arrays.toString(e.getStackTrace()));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
    
    
    @DataProvider(name = "getMenuDeleteNegativeData")
    public Object[][] getMenuDeleteNegativeData() throws customException {
        try {
            LogUtils.info("Reading menu delete negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu delete negative test scenario data");
            
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
                        "menudelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu delete negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting menu delete negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu delete negative test data: " + e.getMessage());
            throw new customException("Error in getting menu delete negative test data: " + e.getMessage());
        }
    }
    
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Pattern to match end of sentences (period, question mark, exclamation mark followed by space or end of string)
        Pattern pattern = Pattern.compile("[.!?][ $]");
        String[] sentences = pattern.split(text);
        return sentences.length;
    }
    
    @Test(dataProvider = "getMenuDeleteNegativeData")
    public void menuDeleteNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu delete negative test case: " + testCaseid);
            ExtentReport.createTest("Menu Delete Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("menudelete") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Set payload for menu delete request based on available fields
                if (requestBodyJson.has("menu_id")) {
                    menuDeleteRequest.setMenu_id(requestBodyJson.getString("menu_id"));
                }
                if (requestBodyJson.has("outlet_id")) {
                    menuDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }
                menuDeleteRequest.setUser_id(requestBodyJson.getString("user_id"));
                response = ResponseUtil.getResponseWithAuth(baseURI, menuDeleteRequest, httpsmethod, accessToken);
                
                int actualStatusCode = response.getStatusCode();
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                LogUtils.info("Expected Status Code: " + expectedStatusCode);
                LogUtils.info("Actual Status Code: " + actualStatusCode);
                LogUtils.info("Response Body: " + response.asString());
                
                ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
                ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + actualStatusCode);
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Check for server errors
                if (actualStatusCode == 500 || actualStatusCode == 502) {
                    LogUtils.failure(logger, "Server error detected with status code: " + actualStatusCode);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error detected: " + actualStatusCode, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
                // Validate status code
                else if (actualStatusCode != expectedStatusCode) {
                    LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
                }
                else {
                    LogUtils.success(logger, "Status code validation passed: " + actualStatusCode);
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + actualStatusCode);
                    
                    // Validate response body
                    actualResponseBody= new JSONObject(response.asString());
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    	expectedResponse = new JSONObject(expectedResponseBody);
                        
                        // Validate response message
                        if (expectedResponse.has("detail") && actualResponseBody.has("detail")) {
                            String expectedDetail = expectedResponse.getString("detail");
                            String actualDetail = expectedResponse.getString("detail");
                            
                            // Validate sentence count
                            int sentenceCount = countSentences(actualDetail);
                            LogUtils.info("Response message has " + sentenceCount + " sentences");
                            ExtentReport.getTest().log(Status.INFO, "Response message has " + sentenceCount + " sentences");
                            
                            if (sentenceCount > 6) {
                                LogUtils.failure(logger, "Response message has more than 6 sentences: " + sentenceCount);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Response message has more than 6 sentences: " + sentenceCount, ExtentColor.RED));
                            } else {
                                LogUtils.success(logger, "Response message sentence count validation passed: " + sentenceCount);
                                ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount);
                            }
                            
                            if (expectedDetail.equals(actualDetail)) {
                                LogUtils.info("Error message validation passed: " + actualDetail);
                                ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                            } else {
                                LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail + ", Actual: " + actualDetail);
                            }
                        }
                        
                        // Complete response validation
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                    }
                    
                    LogUtils.success(logger, "Menu delete negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu delete negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            } else {
                String errorMsg = "Invalid API name or test type. Expected: menudelete/negative, Actual: " + apiName + "/" + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error in menu delete negative test: " + e.getMessage();
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