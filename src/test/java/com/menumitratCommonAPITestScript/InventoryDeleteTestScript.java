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
import com.menumitra.apiRequest.InventoryRequest;
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
public class InventoryDeleteTestScript extends APIBase
{
    private InventoryRequest inventoryDeleteRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(InventoryDeleteTestScript.class);

    /**
     * Data provider for inventory delete API endpoint URLs
     */
    @DataProvider(name = "getInventoryDeleteUrl")
    public static Object[][] getInventoryDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Inventory Delete API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "inventoryDelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Inventory Delete API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Inventory Delete API endpoint data from Excel sheet");
            throw new customException("Error While Reading Inventory Delete API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for inventory delete test scenarios
     */
    @DataProvider(name = "getInventoryDeleteData")
    public static Object[][] getInventoryDeleteData() throws customException {
        try {
            LogUtils.info("Reading inventory delete test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory delete test scenario data found in Excel sheet");
                throw new customException("No inventory delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "inventoryDelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for inventory delete");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory delete test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory delete test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }

  
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for inventory delete test====");
            ExtentReport.createTest("Inventory Delete Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set inventory delete URL
            Object[][] inventoryDeleteData = getInventoryDeleteUrl();
            if (inventoryDeleteData.length > 0) {
                String endpoint = inventoryDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for inventory delete: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No inventory delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No inventory delete URL found in test data");
                throw new customException("No inventory delete URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            inventoryDeleteRequest = new InventoryRequest();
            LogUtils.success(logger, "Inventory Delete Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory Delete Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during inventory delete setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during inventory delete setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to delete inventory
     */
    @Test(dataProvider = "getInventoryDeleteData")
    private void deleteInventory(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory deletion test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Deletion Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            inventoryDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            inventoryDeleteRequest.setUser_id(String.valueOf(user_id));
            
            // Set inventory_id which is required for deletion
            inventoryDeleteRequest.setInventory_id(requestBodyJson.getString("inventory_id"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryDeleteRequest, httpsmethod, accessToken);
            
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
                
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory deleted successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Inventory deleted successfully");
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


    /**
     * Data provider for inventory delete negative test scenarios
     */
    @DataProvider(name = "getInventoryDeleteNegativeData")
    public Object[][] getInventoryDeleteNegativeData() throws customException {
        try {
            LogUtils.info("Reading inventory delete negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading inventory delete negative test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory delete test scenario data found in Excel sheet");
                throw new customException("No inventory delete test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "inventorydelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid inventory delete negative test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            LogUtils.info("Successfully retrieved " + result.length + " test scenarios for inventory delete negative testing");
            return result;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting inventory delete negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting inventory delete negative test data: " + e.getMessage());
            throw new customException("Error in getting inventory delete negative test data: " + e.getMessage());
        }
    }
    
    /**
     * Test method for negative inventory delete scenarios
     */
    @Test(dataProvider = "getInventoryDeleteNegativeData")
    public void deleteInventoryNegative(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory delete negative test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Delete Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Validate API name and test type
            if (!"inventorydelete".equalsIgnoreCase(apiName)) {
                String errorMsg = "Invalid API name for inventory delete test: " + apiName;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!"negative".equalsIgnoreCase(testType)) {
                String errorMsg = "Invalid test type for inventory delete negative test: " + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Initialize inventory request with payload from Excel sheet
            inventoryDeleteRequest = new InventoryRequest();
            
            if (requestBodyJson.has("inventory_id")) {
                inventoryDeleteRequest.setInventory_id(requestBodyJson.getString("inventory_id"));
            }
            
            if (requestBodyJson.has("outlet_id")) {
                inventoryDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            }
            
            if (requestBodyJson.has("user_id")) {
                inventoryDeleteRequest.setUser_id(requestBodyJson.getString("user_id"));
            } else {
                inventoryDeleteRequest.setUser_id(String.valueOf(user_id));
            }
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryDeleteRequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Report actual vs expected status code
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
            
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
                
                // Validate response body
                actualResponseBody = new JSONObject(response.asString());
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualResponseBody.toString());
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    // Validate response message
                    if (expectedResponse.has("detail") && actualResponseBody.has("detail")) {
                        String expectedDetail = expectedResponse.getString("detail");
                        String actualDetail = actualResponseBody.getString("detail");
                        
                        // Count and validate sentence count (maximum 6 sentences allowed)
                        int sentenceCount = countSentences(actualDetail);
                        
                        // Validate sentence count - 6 or fewer sentences required
                        if (sentenceCount > 6) {
                            String errorMsg = "Response message contains more than 6 sentences. Found: " + sentenceCount;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        } else {
                            LogUtils.info("Sentence count validation passed. Found: " + sentenceCount + " sentences (maximum 6 allowed)");
                            ExtentReport.getTest().log(Status.PASS, "Sentence count validation passed. Found: " + sentenceCount + " sentences (maximum 6 allowed)");
                        }
                        
                        // Validate message content
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
                
                LogUtils.success(logger, "Inventory delete negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory delete negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            
        } catch (Exception e) {
            String errorMsg = "Error in inventory delete negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
    
    /**
     * Helper method to count sentences in a string
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Count sentences by looking for sentence terminators (., !, ?)
        // followed by whitespace or end of string
        int count = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            char c = text.charAt(i);
            char next = text.charAt(i + 1);
            
            if ((c == '.' || c == '!' || c == '?') && (Character.isWhitespace(next) || i == text.length() - 2)) {
                count++;
            }
        }
        
        // Check if the last character is a sentence terminator
        char lastChar = text.charAt(text.length() - 1);
        if (lastChar == '.' || lastChar == '!' || lastChar == '?') {
            count++;
        }
        
        return count;
    }
}