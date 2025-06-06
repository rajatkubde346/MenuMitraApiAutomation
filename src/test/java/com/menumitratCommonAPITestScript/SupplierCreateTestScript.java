package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
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
public class SupplierCreateTestScript extends APIBase
{
    private com.menumitra.apiRequest.SupplierRequest supplierCreateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(SupplierCreateTestScript.class);

    /**
     * Data provider for supplier create API endpoint URLs
     */
    @DataProvider(name = "getSupplierCreateUrl")
    public static Object[][] getSupplierCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Supplier Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "supplierCreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Supplier Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Supplier Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Supplier Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for supplier create test scenarios
     */
    @DataProvider(name = "getSupplierCreateData")
    public static Object[][] getSupplierCreateData() throws customException {
        try {
            LogUtils.info("Reading supplier create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No supplier create test scenario data found in Excel sheet");
                throw new customException("No supplier create test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "suppliercreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for supplier create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading supplier create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading supplier create test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading supplier create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name = "getSupplierNegativeData")
    public Object[][] getSupplierNegativeData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No supplier create test scenario data found in Excel sheet");
                ExtentReport.getTest().log(Status.ERROR, "No supplier create test scenario data found in Excel sheet");
                throw new customException("No supplier create test scenario data found in Excel sheet");
            }
            List<Object[]> filterData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && "suppliercreate".equalsIgnoreCase(Objects.toString(row[0], ""))
                        && "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filterData.add(row);
                }
            }
            Object[][] obj = new Object[filterData.size()][];
            for (int i = 0; i < filterData.size(); i++) {
                obj[i] = filterData.get(i);
            }
            LogUtils.info("Successfully retrieved " + obj.length + " negative test scenarios for supplier create");
            ExtentReport.getTest().log(Status.INFO, "Successfully retrieved " + obj.length + " negative test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading supplier create negative test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading supplier create negative test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading supplier create negative test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for supplier create test====");
            ExtentReport.createTest("Supplier Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set supplier create URL
            Object[][] supplierCreateData = getSupplierCreateUrl();
            if (supplierCreateData.length > 0) {
                String endpoint = supplierCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for supplier create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No supplier create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No supplier create URL found in test data");
                throw new customException("No supplier create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            supplierCreateRequest = new com.menumitra.apiRequest.SupplierRequest();
            LogUtils.success(logger, "Supplier Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Supplier Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during supplier create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during supplier create setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create supplier
     */
    @Test(dataProvider = "getSupplierCreateData")
    private void createSupplier(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting supplier creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Supplier Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            supplierCreateRequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            supplierCreateRequest.setUser_id(String.valueOf(user_id));
            
            ExtentReport.getTest().log(Status.INFO, "Setting supplier name in request");
            LogUtils.info("Setting supplier name in request");
            supplierCreateRequest.setName(requestBodyJson.getString("name"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting credit limit in request");
            LogUtils.info("Setting credit limit in request");
            supplierCreateRequest.setCredit_limit(requestBodyJson.getString("credit_limit"));
            supplierCreateRequest.setCredit_rating(requestBodyJson.getString("credit_rating"));
            ExtentReport.getTest().log(Status.INFO, "Setting location in request");
            LogUtils.info("Setting location in request");
            supplierCreateRequest.setLocation(requestBodyJson.getString("location"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting owner name in request");
            LogUtils.info("Setting owner name in request");
            supplierCreateRequest.setOwner_name(requestBodyJson.getString("owner_name"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting website in request");
            LogUtils.info("Setting website in request");
            supplierCreateRequest.setWebsite(requestBodyJson.getString("website"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting mobile number 1 in request");
            LogUtils.info("Setting mobile number 1 in request");
            supplierCreateRequest.setMobile_number1(requestBodyJson.getString("mobile_number1"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting mobile number 2 in request");
            LogUtils.info("Setting mobile number 2 in request");
            supplierCreateRequest.setMobille_number2(requestBodyJson.getString("mobille_number2"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting address in request");
            LogUtils.info("Setting address in request");
            supplierCreateRequest.setAddress(requestBodyJson.getString("address"));
            
            supplierCreateRequest.setSupplier_status(requestBodyJson.getString("supplier_status"));
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body prepared");
            LogUtils.info("Final Request Body prepared");

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response =ResponseUtil.getResponseWithAuth(baseURI,supplierCreateRequest,httpsmethod,accessToken);
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
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier created successfully", ExtentColor.GREEN));
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
    
   
    @Test(dataProvider = "getSupplierNegativeData")
    public void supplierCreateNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode) 
            throws customException {
        try {
            LogUtils.info("Starting supplier create negative test case: " + testCaseid);
            ExtentReport.createTest("Supplier Create Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Verify API name and test type
            if (!apiName.equalsIgnoreCase("suppliercreate") || !testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid API name or test type - Expected: 'suppliercreate' and 'negative', Actual: '" 
                        + apiName + "' and '" + testType + "'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Set all the necessary fields from request
            if (requestBodyJson.has("outlet_id")) {
                supplierCreateRequest.setOutlet_id(String.valueOf(requestBodyJson.get("outlet_id")));
            }
            
            // Check if name field is present and not empty
            // This is a mandatory field and should trigger an error if missing
            if (requestBodyJson.has("name") && !requestBodyJson.getString("name").trim().isEmpty()) {
                supplierCreateRequest.setName(requestBodyJson.getString("name"));
            } else {
                // For negative test cases, we might intentionally omit the name field
                // to test the validation error "Supplier name is required"
                LogUtils.info("Name field is missing or empty in request - expecting validation error");
                ExtentReport.getTest().log(Status.INFO, "Name field is missing or empty - expecting validation error");
            }
            
            supplierCreateRequest.setUser_id(String.valueOf(user_id));
            
            if (requestBodyJson.has("name")) {
                supplierCreateRequest.setName(requestBodyJson.getString("name"));
            }
            
            if (requestBodyJson.has("credit_limit")) {
                supplierCreateRequest.setCredit_limit(requestBodyJson.getString("credit_limit"));
            }
            
            if (requestBodyJson.has("credit_rating")) {
                supplierCreateRequest.setCredit_rating(requestBodyJson.getString("credit_rating"));
            }
            
            if (requestBodyJson.has("location")) {
                supplierCreateRequest.setLocation(requestBodyJson.getString("location"));
            }
            
            if (requestBodyJson.has("owner_name")) {
                supplierCreateRequest.setOwner_name(requestBodyJson.getString("owner_name"));
            }
            
            if (requestBodyJson.has("website")) {
                supplierCreateRequest.setWebsite(requestBodyJson.getString("website"));
            }
            
            if (requestBodyJson.has("mobile_number1")) {
                supplierCreateRequest.setMobile_number1(requestBodyJson.getString("mobile_number1"));
            }
            
            if (requestBodyJson.has("mobille_number2")) {
                supplierCreateRequest.setMobille_number2(requestBodyJson.getString("mobille_number2"));
            }
            
            if (requestBodyJson.has("address")) {
                supplierCreateRequest.setAddress(requestBodyJson.getString("address"));
            }
            
            if (requestBodyJson.has("supplier_status")) {
                supplierCreateRequest.setSupplier_status(requestBodyJson.getString("supplier_status"));
            }
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseURI, supplierCreateRequest, httpsmethod, accessToken);
            
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
                
                LogUtils.success(logger, "Supplier create negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier create negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            
        } catch (Exception e) {
            String errorMsg = "Error in supplier create negative test: " + e.getMessage();
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
    
    

   // @AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down started===");
            ExtentReport.createTest("Supplier Create Test Teardown");
            
            LogUtils.info("Logging out user");
            ActionsMethods.logout();
            
            LogUtils.info("Clearing tokens");
            TokenManagers.clearTokens();
            
            LogUtils.info("===Test environment tear down completed successfully===");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}
