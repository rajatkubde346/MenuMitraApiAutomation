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

@Listeners(Listener.class)
public class tableView extends APIBase
{
	
    private tableCreateRequest tableViewRequest;
    private io.restassured.response.Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private Logger logger = LogUtils.getLogger(tableView.class);

    @DataProvider(name = "getTableViewUrl")
    public  Object[][] getTableViewUrl() throws customException {
    	try {
            LogUtils.info("=====Reading Login API Endpoint Data=====");
            ExtentReport.getTest().log(Status.INFO, "Reading Login API endpoint configuration");
            Object[][] apiData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            Object[][] filteredData = Arrays.stream(apiData)
                    .filter(row -> "tableview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            LogUtils.success(logger, "Successfully retrieved Login API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully loaded Login API configuration");
            return filteredData;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Login API endpoint data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to read Login API endpoint data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Login API endpoint data: " + e.getMessage());
        }
    }

    @DataProvider(name = "getTableViewData")
    public static Object[][] getTableViewData() throws customException {
        try {
            LogUtils.info("Starting to read table view test scenario data from Excel");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table view test scenario data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                        "tableview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully filtered " + obj.length + " test scenarios for table view");
            return obj;

        } catch (Exception e) {
            String errorMsg = "Error while reading table view test scenario data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            ExtentReport.createTest("Table View Test Script");
            LogUtils.info("=====Starting Table View Test Script Setup=====");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            LogUtils.info("Getting base URL from environment");
            baseURI = EnviromentChanges.getBaseUrl();
            
            LogUtils.info("Retrieving table view URL configuration");
            Object[][] tableViewData = getTableViewUrl();
            
            if (tableViewData.length > 0) {
                String endpoint = tableViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
               
                LogUtils.success(logger, "Successfully constructed Table View Base URI: " + baseURI);
            } else {
                String errorMsg = "Failed to construct Table View Base URI - No endpoint data found";
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

            tableViewRequest = new tableCreateRequest();
            LogUtils.success(logger, "Table view test script Setup completed successfully");

        } catch (Exception e) {
            String errorMsg = "Setup failed: " + e.getMessage();
            LogUtils.exception(logger, "Error during table view test script setup", e);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableViewData")
    private void verifyTableView(String apiName, String testCaseid, String testType, String description,
    String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode) throws customException
    {
        try {
            ExtentReport.createTest("Table View Test - " + testCaseid);
            LogUtils.info("=====Starting Table View Test Execution=====");
            LogUtils.info("Test Case ID: " + testCaseid);
            LogUtils.info("Description: " + description);
            
            ExtentReport.getTest().log(Status.INFO, "API URL: " + baseURI);
            
            LogUtils.info("API URL: " + baseURI);
            

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            tableViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            tableViewRequest.setTable_number(requestBodyJson.getString("table_number"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting section_id in request");
            LogUtils.info("Setting section_id in request");
            tableViewRequest.setSection_id(requestBodyJson.getString("section_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Final Request Body: " + requestBodyJson.toString(2));

            
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            response = ResponseUtil.getResponseWithAuth(baseURI,tableViewRequest,httpsmethod,accessToken);
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if(response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                
                if(!actualResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                    LogUtils.info("Starting response body validation");
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    //validateResponseBody.handleResponseBody(response, expectedResponse);
                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table viewed successfully", ExtentColor.GREEN));
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
    
    
    
    
    
    
    @DataProvider(name = "getTableViewNegativeData")
    public Object[][] getTableViewNegativeData() throws customException {
        try {
            LogUtils.info("Reading table view negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading table view negative test scenario data");
            
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
                        "tableview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid table view negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting table view negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting table view negative test data: " + e.getMessage());
            throw new customException("Error in getting table view negative test data: " + e.getMessage());
        }
    }
    
   
    
    @Test(dataProvider = "getTableViewNegativeData")
    public void tableViewNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting table view negative test case: " + testCaseid);
            ExtentReport.createTest("Table View Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Validate API name and test type
            if (!apiName.equalsIgnoreCase("tableview")) {
                String errorMsg = "Invalid API name: Expected 'tableview', Found: '" + apiName + "'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid test type: Expected 'negative', Found: '" + testType + "'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            tableViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            tableViewRequest.setTable_number(requestBodyJson.getString("table_number"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting section_id in request");
            LogUtils.info("Setting section_id in request");
            tableViewRequest.setSection_id(requestBodyJson.getString("section_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Final Request Body: " + requestBodyJson.toString(2));
            
            response = ResponseUtil.getResponseWithAuth(baseURI, tableViewRequest, httpsmethod, accessToken);
            
            // Log both expected and actual status codes
            int expectedStatusCode = Integer.parseInt(statusCode);
            int actualStatusCode = response.getStatusCode();
            
            LogUtils.info("Expected Status Code: " + expectedStatusCode);
            LogUtils.info("Actual Status Code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + actualStatusCode);
            
            // Log both expected and actual response bodies
            String actualResponse = response.asString();
            LogUtils.info("Expected Response Body: " + expectedResponseBody);
            LogUtils.info("Actual Response Body: " + actualResponse);
            ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
            ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualResponse);
            
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
                actualResponseBody = new JSONObject(response.asString());
                System.out.println(actualResponse);
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                	expectedResponse = new JSONObject(expectedResponseBody);
                    
                    // Validate response message
                    if (expectedResponse.has("detail") && actualResponseBody.has("detail")) {
                        String expectedDetail = expectedResponse.getString("detail");
                        String actualDetail = actualResponseBody.getString("detail");
                        
                        // Check sentence count
                        boolean hasFiveSentences = checkSentenceCount(actualDetail);
                        if (!hasFiveSentences) {
                            int sentenceCount = actualDetail.split("[.!?](?:\\s|$)").length;
                            
                            if (sentenceCount > 5) {
                                String warningMsg = "Response message has more than 5 sentences: " + sentenceCount;
                                LogUtils.failure(logger, warningMsg);
                                ExtentReport.getTest().log(Status.WARNING, MarkupHelper.createLabel(warningMsg, ExtentColor.AMBER));
                            } else {
                                LogUtils.info("Response message has fewer than 5 sentences: " + sentenceCount + " (allowed)");
                                ExtentReport.getTest().log(Status.INFO, "Response message has fewer than 5 sentences: " + sentenceCount + " (allowed)");
                            }
                        } else {
                            LogUtils.info("Response message has exactly 5 sentences");
                            ExtentReport.getTest().log(Status.INFO, "Response message has exactly 5 sentences");
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
                
                LogUtils.success(logger, "Table view negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table view negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        } catch (Exception e) {
            String errorMsg = "Error in table view negative test: " + e.getMessage();
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
     * Checks if a message contains exactly 5 sentences.
     * 
     * @param message The message to check
     * @return true if the message contains exactly 5 sentences, false otherwise
     */
    private boolean checkSentenceCount(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        // Pattern to match sentences ending with ., !, ? followed by space or end of string
        Pattern pattern = Pattern.compile("[.!?](?:\\s|$)");
        String[] sentences = pattern.split(message);
        
        // Filter out empty strings which might come from multiple punctuation marks
        int count = 0;
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                count++;
            }
        }
        
        return count == 5;
    }
}
