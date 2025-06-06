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
import com.menumitra.apiRequest.TableDeleteRequest;
import com.menumitra.apiRequest.tableCreateRequest;
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
public class TableDeleteTestScript extends APIBase
{
    private TableDeleteRequest tableDeleteRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private Logger logger = LogUtils.getLogger(TableDeleteTestScript.class);

    @DataProvider(name = "getTableDeleteUrl")
    public static Object[][] getTableDeleteUrl() throws customException {
        try {
            LogUtils.info("Starting to read Table Delete API endpoint data from Excel");
            
            if(excelSheetPathForGetApis == null || excelSheetPathForGetApis.isEmpty()) {
                String errorMsg = "Excel sheet path is null or empty";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Table Delete API endpoint data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length > 0 && "tabledelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if(filteredData.length == 0) {
                String errorMsg = "No matching Table Delete API endpoints found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved " + filteredData.length + " Table Delete API endpoints");
            return filteredData;

        } catch (Exception e) {
            String errorMsg = "Error while reading Table Delete API endpoint data from Excel sheet: " + 
                            (e.getMessage() != null ? e.getMessage() : "Unknown error");
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getTableDeleteData")
    public static Object[][] getTableDeleteData() throws customException {
        try {
            LogUtils.info("Starting to read table delete test scenario data from Excel");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table delete test scenario data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                        "tabledelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully filtered " + obj.length + " test scenarios for table delete");
            return obj;

        } catch (Exception e) {
            String errorMsg = "Error while reading table delete test scenario data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            ExtentReport.createTest("Table Delete Test Script");
            LogUtils.info("=====Starting Table Delete Test Script Setup=====");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            LogUtils.info("Getting base URL from environment");
            baseURI = EnviromentChanges.getBaseUrl();
            
            LogUtils.info("Retrieving table delete URL configuration");
            Object[][] tableDeleteData = getTableDeleteUrl();
            
            if (tableDeleteData.length > 0) {
                String endpoint = tableDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
               
                LogUtils.success(logger, "Successfully constructed Table Delete Base URI: " + baseURI);
            } else {
                String errorMsg = "Failed to construct Table Delete Base URI - No endpoint data found";
                LogUtils.failure(logger, errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Retrieving authentication tokens");
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                String errorMsg = "Authentication failed - Required tokens not found. Please verify login and OTP verification";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            tableDeleteRequest=new TableDeleteRequest();
            LogUtils.success(logger, "Table delete test script Setup completed successfully");

        } catch (Exception e) {
            String errorMsg = "Setup failed: " + e.getMessage();
            LogUtils.exception(logger, "Error during table delete test script setup", e);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableDeleteData")
    private void verifyTableDelete(String apiName, String testCaseid, String testType, String description,
    String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode) throws customException
    {
        try {
            ExtentReport.createTest("Table Delete Test - " + testCaseid);
            LogUtils.info("=====Starting Table Delete Test Execution=====");
            LogUtils.info("Test Case ID: " + testCaseid);
            LogUtils.info("Description: " + description);
            
            ExtentReport.getTest().log(Status.INFO, "API URL: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsmethod);
            LogUtils.info("API URL: " + baseURI);
            LogUtils.info("HTTP Method: " + httpsmethod);

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            tableDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            tableDeleteRequest.setUser_id(String.valueOf(user_id));
            
            ExtentReport.getTest().log(Status.INFO, "Setting table_id in request");
            LogUtils.info("Setting table_id in request");
            tableDeleteRequest.setSection_id(requestBodyJson.getString("section_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Final Request Body: " + requestBodyJson.toString(2));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            response = ResponseUtil.getResponseWithAuth(baseURI, tableDeleteRequest, httpsmethod, accessToken);

            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());
            System.out.println(response.getStatusCode());
            // Validation
            
            if(response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                
                if(!actualResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table deleted successfully", ExtentColor.GREEN));
                } else {
                    ExtentReport.getTest().log(Status.INFO, "Response body is empty");
                    LogUtils.info("Response body is empty");
                }
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
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }



    @DataProvider(name = "getTableDeleteNegativeData")
    public Object[][] getTableDeleteNegativeData() throws customException {
        try {
            LogUtils.info("Reading table delete negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading table delete negative test scenario data");
            
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
                        "tabledelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid table delete negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting table delete negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting table delete negative test data: " + e.getMessage());
            throw new customException("Error in getting table delete negative test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getTableDeleteNegativeData")
    public void tableDeleteNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting table delete negative test case: " + testCaseid);
            ExtentReport.createTest("Table Delete Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Verify API name and test type
            if (!apiName.equalsIgnoreCase("tabledelete")) {
                String errorMsg = "Invalid API name. Expected: tabledelete, Actual: " + apiName;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid test type. Expected: negative, Actual: " + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Set payload for table delete request
            try {
                ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
                LogUtils.info("Setting outlet_id in request");
                if (requestBodyJson.has("outlet_id")) {
                    tableDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                } 
                
                ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
                LogUtils.info("Setting user_id in request: " + user_id);
                tableDeleteRequest.setUser_id(String.valueOf(user_id));
                
                ExtentReport.getTest().log(Status.INFO, "Setting table_id in request");
                LogUtils.info("Setting table_id in request");
                if (requestBodyJson.has("section_id")) {
                    tableDeleteRequest.setSection_id(requestBodyJson.getString("section_id"));
                } 
                
                ExtentReport.getTest().log(Status.INFO, "Setting section_id in request");
                LogUtils.info("Setting section_id in request");
                if (requestBodyJson.has("section_id")) {
                    tableDeleteRequest.setSection_id(requestBodyJson.getString("section_id"));
                } 
                
                ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + tableDeleteRequest.toString());
                LogUtils.info("Final Request Body: " + tableDeleteRequest.toString());
                
                // Make API call
                ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
                LogUtils.info("Making API call to endpoint: " + baseURI);
                response = ResponseUtil.getResponseWithAuth(baseURI, tableDeleteRequest, httpsmethod, accessToken);
                
                // Validate response body structure
                if (response != null && !response.asString().isEmpty()) {
                    try {
                        actualResponseBody = new JSONObject(response.asString());
                        LogUtils.info("Response body parsed successfully");
                        ExtentReport.getTest().log(Status.INFO, "Response body parsed successfully");
                    } catch (Exception e) {
                        LogUtils.error("Failed to parse response body: " + e.getMessage());
                        ExtentReport.getTest().log(Status.ERROR, "Failed to parse response body: " + e.getMessage());
                    }
                } else {
                    LogUtils.failure(logger,"Response body is empty");
                    ExtentReport.getTest().log(Status.WARNING, "Response body is empty");
                }
            } catch (Exception e) {
                LogUtils.error("Error preparing request or processing response: " + e.getMessage());
                ExtentReport.getTest().log(Status.ERROR, "Error preparing request or processing response: " + e.getMessage());
                throw e;
            }
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Log expected vs actual status code
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
                
                // Log expected vs actual response body
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
                }
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + response.asString());
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    // Validate response message
                    if (expectedResponse.has("detail") && actualResponseBody.has("detail")) {
                        String expectedDetail = expectedResponse.getString("detail");
                        String actualDetail = actualResponseBody.getString("detail");
                        
                        // Check for 6 sentences exactly
                        int sentenceCount = countSentences(actualDetail);
                        if (sentenceCount > 6) {
                            String errorMsg = "Error message contains more than 6 sentences. Found: " + sentenceCount;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        } else {
                            LogUtils.info("Sentence count validation passed. Found: " + sentenceCount + " sentences (maximum 6 allowed)");
                            ExtentReport.getTest().log(Status.PASS, "Sentence count validation passed. Found: " + sentenceCount + " sentences (maximum 6 allowed)");
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
                
                LogUtils.success(logger, "Table delete negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table delete negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        } catch (Exception e) {
            String errorMsg = "Error in table delete negative test: " + e.getMessage();
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
     * Count the number of sentences in a string.
     * Sentences are delimited by periods, exclamation marks, or question marks.
     * 
     * @param text The text to count sentences in
     * @return The number of sentences
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Split by common sentence terminators
        String[] sentences = text.split("[.!?]+");
        
        // Filter out empty strings that may result from sequential punctuation
        int count = 0;
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                count++;
            }
        }
        
        return count;
    }

}
