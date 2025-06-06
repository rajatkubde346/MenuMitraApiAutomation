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
import com.menumitra.apiRequest.CaptainRequest;
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
public class CaptainDeleteTestScript extends APIBase
{
    private CaptainRequest captainDeleteRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private JSONObject expectedResponseJson;
    private Logger logger = LogUtils.getLogger(CaptainDeleteTestScript.class);

    /**
     * Data provider for captain delete API endpoint URLs
     */
    @DataProvider(name = "getCaptainDeleteUrl")
    public static Object[][] getCaptainDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Captain Delete API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "captainDelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Captain Delete API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Captain Delete API endpoint data from Excel sheet");
            throw new customException("Error While Reading Captain Delete API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for captain delete test scenarios
     */
    @DataProvider(name = "getCaptainDeleteData")
    public static Object[][] getCaptainDeleteData() throws customException {
        try {
            LogUtils.info("Reading captain delete test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No captain delete test scenario data found in Excel sheet");
                throw new customException("No captain delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "captainDelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for captain delete");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading captain delete test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading captain delete test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading captain delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }

  
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for captain delete test====");
            ExtentReport.createTest("Captain Delete Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set captain delete URL
            Object[][] captainDeleteData = getCaptainDeleteUrl();
            if (captainDeleteData.length > 0) {
                String endpoint = captainDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for captain delete: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No captain delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No captain delete URL found in test data");
                throw new customException("No captain delete URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            captainDeleteRequest = new CaptainRequest();
            LogUtils.success(logger, "Captain Delete Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Captain Delete Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during captain delete setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during captain delete setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to delete captain
     */
    @Test(dataProvider = "getCaptainDeleteData")
    private void deleteCaptain(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting captain deletion test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Captain Deletion Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            captainDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            captainDeleteRequest.setUser_id(requestBodyJson.getString("user_id"));
            captainDeleteRequest.setUpdate_user_id(requestBodyJson.getString("update_user_id"));
           
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, captainDeleteRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Status code validation only
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                // Log response without validation
                if (response.asString() != null && !response.asString().isEmpty()) {
                    actualResponseBody = new JSONObject(response.asString());
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
                }
                
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Captain deleted successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Captain deleted successfully");
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
    
    @DataProvider(name = "getCaptainDeleteNegativeData")
    public Object[][] getCaptainDeleteNegativeData() throws customException {
        try {
            LogUtils.info("Reading captain delete negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading captain delete negative test scenario data");
            
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
                        "captaindelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid captain delete negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting captain delete negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting captain delete negative test data: " + e.getMessage());
            throw new customException("Error in getting captain delete negative test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getCaptainDeleteNegativeData")
    public void captainDeleteNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting captain delete negative test case: " + testCaseid);
            ExtentReport.createTest("Captain Delete Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("captaindelete") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Set payload for captain delete request
                if (requestBodyJson.has("outlet_id")) {
                    captainDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }
                if (requestBodyJson.has("user_id")) {
                    captainDeleteRequest.setUser_id(requestBodyJson.getString("user_id"));
                }
                if (requestBodyJson.has("update_user_id")) {
                    captainDeleteRequest.setUpdate_user_id(requestBodyJson.getString("update_user_id"));
                }
                
                response = ResponseUtil.getResponseWithAuth(baseURI, captainDeleteRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Report both expected and actual status codes
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
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedResponseJson = new JSONObject(expectedResponseBody);
                        
                        // Log expected vs actual response bodies
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseJson.toString(2));
                        ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualResponseBody.toString(2));
                        
                        // Validate response message
                        if (expectedResponseJson.has("detail") && actualResponseBody.has("detail")) {
                            String expectedDetail = expectedResponseJson.getString("detail");
                            String actualDetail = actualResponseBody.getString("detail");
                            
                            // Check if message exceeds 6 sentences
                            String[] sentences = actualDetail.split("[.!?]+");
                            int sentenceCount = 0;
                            for (String sentence : sentences) {
                                if (sentence.trim().length() > 0) {
                                    sentenceCount++;
                                }
                            }
                            
                            if (sentenceCount > 6) {
                                String errorMsg = "Response message exceeds maximum allowed sentences - Found: " + sentenceCount + ", Maximum allowed: 6";
                                LogUtils.failure(logger, errorMsg);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Message: " + actualDetail);
                            } else {
                                LogUtils.info("Response message sentence count is valid: " + sentenceCount);
                                ExtentReport.getTest().log(Status.PASS, "Response message sentence count is valid: " + sentenceCount);
                            }
                            
                            if (expectedDetail.equals(actualDetail)) {
                                LogUtils.info("Error message validation passed: " + actualDetail);
                                ExtentReport.getTest().log(Status.PASS, "Error message validation passed");
                            } else {
                                LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail);
                                ExtentReport.getTest().log(Status.FAIL, "Actual: " + actualDetail);
                            }
                        }
                        
                        // Complete response validation
                        validateResponseBody.handleResponseBody(response, expectedResponseJson);
                    }
                    
                    LogUtils.success(logger, "Captain delete negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Captain delete negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            } else {
                String errorMsg = "Invalid API name or test type: API=" + apiName + ", TestType=" + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error in captain delete negative test: " + e.getMessage();
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
     * Validate if a message contains more than the maximum allowed number of sentences
     * @param message The message to validate
     * @param maxSentences Maximum allowed sentences
     * @return Error message if validation fails, null if validation passes
     */
    private String validateSentenceCount(String message, int maxSentences) {
        if (message == null || message.trim().isEmpty()) {
            return null; // Empty message, no sentences
        }
        
        String[] sentences = message.split("[.!?]+");
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                sentenceCount++;
            }
        }
        
        if (sentenceCount > maxSentences) {
            return "Response message exceeds maximum allowed sentences - Found: " + sentenceCount + 
                   ", Maximum allowed: " + maxSentences;
        }
        
        return null; // Validation passed
    }  
}