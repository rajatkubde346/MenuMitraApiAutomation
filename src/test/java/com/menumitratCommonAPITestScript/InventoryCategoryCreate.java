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
import com.menumitra.apiRequest.InventoryCategoryCreateRequest;
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
public class InventoryCategoryCreate extends APIBase
{
    private InventoryCategoryCreateRequest inventoryCategoryCreateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(InventoryCategoryCreate.class);

    /**
     * Data provider for inventory category create API endpoint URLs
     */
    @DataProvider(name = "getInventoryCategoryCreateUrl")
    public static Object[][] getInventoryCategoryCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Inventory Category Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "inventorycategorycreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Inventory Category Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Inventory Category Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Inventory Category Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for inventory category create test scenarios
     */
    @DataProvider(name = "getInventoryCategoryCreateData")
    public static Object[][] getInventoryCategoryCreateData() throws customException {
        try {
            LogUtils.info("Reading inventory category create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory category create test scenario data found in Excel sheet");
                throw new customException("No inventory category create test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "inventorycategorycreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for inventory category create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory category create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory category create test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory category create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name = "getInventoryCategoryCreateNegativeData")
    public Object[][] getInventoryCategoryCreateNegativeData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory category create test scenario data found in Excel sheet");
                ExtentReport.getTest().log(Status.ERROR, "No inventory category create test scenario data found in Excel sheet");
                throw new customException("No inventory category create test scenario data found in Excel sheet");
            }
            List<Object[]> filterData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && "inventorycategorycreate".equalsIgnoreCase(Objects.toString(row[0], ""))
                        && "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filterData.add(row);
                }
            }
            Object[][] obj = new Object[filterData.size()][];
            for (int i = 0; i < filterData.size(); i++) {
                obj[i] = filterData.get(i);
            }
            LogUtils.info("Successfully retrieved " + obj.length + " negative test scenarios for inventory category create");
            ExtentReport.getTest().log(Status.INFO, "Successfully retrieved " + obj.length + " negative test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory category create negative test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory category create negative test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory category create negative test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for inventory category create test====");
            ExtentReport.createTest("Inventory Category Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set inventory category create URL
            Object[][] inventoryCategoryCreateData = getInventoryCategoryCreateUrl();
            if (inventoryCategoryCreateData.length > 0) {
                String endpoint = inventoryCategoryCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for inventory category create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No inventory category create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No inventory category create URL found in test data");
                throw new customException("No inventory category create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            inventoryCategoryCreateRequest = new InventoryCategoryCreateRequest();
            LogUtils.success(logger, "Inventory Category Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory Category Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during inventory category create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during inventory category create setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create inventory category
     */
    @Test(dataProvider = "getInventoryCategoryCreateData")
    private void createInventoryCategory(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory category creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Category Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            inventoryCategoryCreateRequest.setUser_id(String.valueOf(user_id));
            
            ExtentReport.getTest().log(Status.INFO, "Setting category_name in request");
            LogUtils.info("Setting category_name in request");
            inventoryCategoryCreateRequest.setName(requestBodyJson.getString("name"));
            
            
            
            
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Final Request Body: " + requestBodyJson.toString(2));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryCategoryCreateRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                
                if (!actualResponseBody.isEmpty()) {
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
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory category created successfully", ExtentColor.GREEN));
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
     * Test method for negative inventory category create scenarios
     */
    @Test(dataProvider = "getInventoryCategoryCreateNegativeData")
    public void createInventoryCategoryNegative(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory category create negative test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Category Create Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Validate API name and test type
            if (!"inventorycategorycreate".equalsIgnoreCase(apiName)) {
                String errorMsg = "Invalid API name for inventory category create test: " + apiName;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!"negative".equalsIgnoreCase(testType)) {
                String errorMsg = "Invalid test type for inventory category create negative test: " + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Initialize inventory category create request with payload from Excel sheet
            inventoryCategoryCreateRequest = new InventoryCategoryCreateRequest();
            
           
            
            if (requestBodyJson.has("user_id")) {
                inventoryCategoryCreateRequest.setUser_id(requestBodyJson.getString("user_id"));
            } else {
                inventoryCategoryCreateRequest.setUser_id(String.valueOf(user_id));
            }
            
            if (requestBodyJson.has("name")) {
                inventoryCategoryCreateRequest.setName(requestBodyJson.getString("name"));
            }
            
            
            LogUtils.info("Inventory category create request initialized with payload from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Inventory category create request initialized with payload from Excel sheet");
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryCategoryCreateRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());
            
            // Validation
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + statusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
            
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                actualResponseBody = new JSONObject(response.asString());
                expectedResponse = new JSONObject(expectedResponseBody);
                
                ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                LogUtils.info("Starting response body validation");
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                
                // Validate response message sentence count
                if (actualResponseBody.has("message")) {
                    String message = actualResponseBody.getString("message");
                    String[] sentences = message.split("[.!?]+");
                    int sentenceCount = 0;
                    
                    for (String sentence : sentences) {
                        if (!sentence.trim().isEmpty()) {
                            sentenceCount++;
                        }
                    }
                    
                    ExtentReport.getTest().log(Status.INFO, "Response message contains " + sentenceCount + " sentences");
                    LogUtils.info("Response message contains " + sentenceCount + " sentences");
                    
                    if (sentenceCount > 6) {
                        String errorMsg = "Response message contains more than 6 sentences: " + sentenceCount;
                        ExtentReport.getTest().log(Status.FAIL, errorMsg);
                        LogUtils.failure(logger, errorMsg);
                        throw new customException(errorMsg);
                    }
                }
                
                ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                LogUtils.info("Performing detailed response validation");
                validateResponseBody.handleResponseBody(response, expectedResponse);
                
                ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                LogUtils.success(logger, "Response body validation passed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Negative test case executed successfully", ExtentColor.GREEN));
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
}