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
import com.menumitra.apiRequest.SupplierRequest;
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
public class SupplierUpdateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String access;
    private SupplierRequest supplierUpdateRequest;
    private URL url;
    private JSONObject expectedResponseJson;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(SupplierUpdateTestScript.class);
   
    @BeforeClass
    private void supplierUpdateSetUp() throws customException {
        try {
            LogUtils.info("Supplier Update SetUp");
            ExtentReport.createTest("Supplier Update SetUp");
            ExtentReport.getTest().log(Status.INFO, "Supplier Update SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getSupplierUpdateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No supplier update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No supplier update URL found in test data");
                throw new customException("No supplier update URL found in test data");
            }
            
            access = TokenManagers.getJwtToken();
            if(access.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            supplierUpdateRequest = new SupplierRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in supplier update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in supplier update setup: " + e.getMessage());
            throw new customException("Error in supplier update setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getSupplierUpdateUrl")
    private Object[][] getSupplierUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Supplier Update API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Supplier Update API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Supplier Update API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "supplierupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No supplier update URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting supplier update URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting supplier update URL: " + e.getMessage());
            throw new customException("Error in getting supplier update URL: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getSupplierUpdateNegativeData")
    public Object[][] getSupplierUpdateNegativeData() throws customException {
        try {
            LogUtils.info("Reading supplier update negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading supplier update negative test scenario data");
            
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
                        "supplierupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier update negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting supplier update negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting supplier update negative test data: " + e.getMessage());
            throw new customException("Error in getting supplier update negative test data: " + e.getMessage());
        }
    }
    @DataProvider(name = "getSupplierUpdateData")
    public Object[][] getSupplierUpdateData() throws customException {
        try {
            LogUtils.info("Reading supplier update test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading supplier update test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No supplier update test scenario data found in Excel sheet");
                throw new customException("No supplier update test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "supplierupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier update positive test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            LogUtils.info("Successfully retrieved " + result.length + " test scenarios for supplier update");
            return result;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting supplier update positive test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting supplier update positive test data: " + e.getMessage());
            throw new customException("Error in getting supplier update positive test data: " + e.getMessage());
        }
    }
    
    /**
     * Test method to update supplier
     */
    @Test(dataProvider = "getSupplierUpdateData")
    public void updateSupplier(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting supplier update using positive test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Supplier Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Set supplier_id (required for update)
            ExtentReport.getTest().log(Status.INFO, "Setting supplier_id in request");
            LogUtils.info("Setting supplier_id in request");
            supplierUpdateRequest.setSupplier_id(requestBodyJson.getString("supplier_id"));
            
            // Set staff_id (specific to update operation)
            ExtentReport.getTest().log(Status.INFO, "Setting staff_id in request");
            LogUtils.info("Setting staff_id in request");
            if (requestBodyJson.has("supplier_id")) {
                supplierUpdateRequest.setUser_id(requestBodyJson.getString("supplier_id"));
            }
            
            // Set other fields if present in request
            if (requestBodyJson.has("name")) {
                ExtentReport.getTest().log(Status.INFO, "Setting supplier name in request");
                LogUtils.info("Setting supplier name in request");
                supplierUpdateRequest.setName(requestBodyJson.getString("name"));
            }
            
            if (requestBodyJson.has("credit_limit")) {
                ExtentReport.getTest().log(Status.INFO, "Setting credit limit in request");
                LogUtils.info("Setting credit limit in request");
                supplierUpdateRequest.setCredit_limit(requestBodyJson.getString("credit_limit"));
            }
            
            if (requestBodyJson.has("credit_rating")) {
                ExtentReport.getTest().log(Status.INFO, "Setting credit rating in request");
                LogUtils.info("Setting credit rating in request");
                supplierUpdateRequest.setCredit_rating(requestBodyJson.getString("credit_rating"));
            }
            
            if (requestBodyJson.has("location")) {
                ExtentReport.getTest().log(Status.INFO, "Setting location in request");
                LogUtils.info("Setting location in request");
                supplierUpdateRequest.setLocation(requestBodyJson.getString("location"));
            }
            
            if (requestBodyJson.has("owner_name")) {
                ExtentReport.getTest().log(Status.INFO, "Setting owner name in request");
                LogUtils.info("Setting owner name in request");
                supplierUpdateRequest.setOwner_name(requestBodyJson.getString("owner_name"));
            }
            
            if (requestBodyJson.has("website")) {
                ExtentReport.getTest().log(Status.INFO, "Setting website in request");
                LogUtils.info("Setting website in request");
                supplierUpdateRequest.setWebsite(requestBodyJson.getString("website"));
            }
            
            if (requestBodyJson.has("mobile_number1")) {
                ExtentReport.getTest().log(Status.INFO, "Setting mobile number 1 in request");
                LogUtils.info("Setting mobile number 1 in request");
                supplierUpdateRequest.setMobile_number1(requestBodyJson.getString("mobile_number1"));
            }
            
            if (requestBodyJson.has("mobille_number2")) {
                ExtentReport.getTest().log(Status.INFO, "Setting mobile number 2 in request");
                LogUtils.info("Setting mobile number 2 in request");
                supplierUpdateRequest.setMobille_number2(requestBodyJson.getString("mobille_number2"));
            }
            
            if (requestBodyJson.has("address")) {
                ExtentReport.getTest().log(Status.INFO, "Setting address in request");
                LogUtils.info("Setting address in request");
                supplierUpdateRequest.setAddress(requestBodyJson.getString("address"));
            }
            
            if (requestBodyJson.has("supplier_status")) {
                ExtentReport.getTest().log(Status.INFO, "Setting supplier status in request");
                LogUtils.info("Setting supplier status in request");
                supplierUpdateRequest.setSupplier_status(requestBodyJson.getString("supplier_status"));
            }
            
            if (requestBodyJson.has("outlet_id")) {
                ExtentReport.getTest().log(Status.INFO, "Setting supplier status in request");
                LogUtils.info("Setting supplier status in request");
                supplierUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            }
            if (requestBodyJson.has("user_id")) {
                ExtentReport.getTest().log(Status.INFO, "Setting supplier status in request");
                LogUtils.info("Setting supplier status in request");
                supplierUpdateRequest.setOutlet_id(requestBodyJson.getString("user_id"));
            }
            ExtentReport.getTest().log(Status.INFO, "Final Request Body prepared");
            LogUtils.info("Final Request Body prepared");
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + access.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + access.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, supplierUpdateRequest, httpsmethod, access);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());
            
            // Validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualJsonBody = new JSONObject(response.asString());
                
                if (!actualJsonBody.isEmpty()) {
                    expectedResponseJson = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                    LogUtils.info("Starting response body validation");
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponseJson.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponseJson.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualJsonBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualJsonBody.toString(2));
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    validateResponseBody.handleResponseBody(response, expectedResponseJson);
                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier updated successfully", ExtentColor.GREEN));
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
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // Count sentences by looking for period, exclamation, or question mark followed by space or end of string
        String[] sentences = text.split("[.!?]+\\s*");
        return sentences.length;
    }
    
    @Test(dataProvider = "getSupplierUpdateNegativeData")
    public void supplierUpdateNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting supplier update negative test case: " + testCaseid);
            ExtentReport.createTest("Supplier Update Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (!apiName.equalsIgnoreCase("supplierupdate") || !testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid API name or test type: " + apiName + ", " + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Set payload for supplier update request
            if (requestBodyJson.has("supplier_id")) {
                supplierUpdateRequest.setSupplier_id(requestBodyJson.getString("supplier_id"));;
            }
            if (requestBodyJson.has("name")) {
            	supplierUpdateRequest.setName(requestBodyJson.getString("name"));
            }
            
            if (requestBodyJson.has("credit_limit")) {
            	supplierUpdateRequest.setCredit_limit(requestBodyJson.getString("credit_limit"));
            }
            
            if (requestBodyJson.has("credit_rating")) {
            	supplierUpdateRequest.setCredit_rating(requestBodyJson.getString("credit_rating"));
            }
            
            if (requestBodyJson.has("location")) {
            	supplierUpdateRequest.setLocation(requestBodyJson.getString("location"));
            }
            
            if (requestBodyJson.has("owner_name")) {
            	supplierUpdateRequest.setOwner_name(requestBodyJson.getString("owner_name"));
            }
            
            if (requestBodyJson.has("website")) {
            	supplierUpdateRequest.setWebsite(requestBodyJson.getString("website"));
            }
            
            if (requestBodyJson.has("mobile_number1")) {
            	supplierUpdateRequest.setMobile_number1(requestBodyJson.getString("mobile_number1"));
            }
            
            if (requestBodyJson.has("mobille_number2")) {
            	supplierUpdateRequest.setMobille_number2(requestBodyJson.getString("mobille_number2"));
            }
            
            if (requestBodyJson.has("address")) {
            	supplierUpdateRequest.setAddress(requestBodyJson.getString("address"));
            }
            
            if (requestBodyJson.has("supplier_status")) {
            	supplierUpdateRequest.setSupplier_status(requestBodyJson.getString("supplier_status"));
            }
            if (requestBodyJson.has("outlet_id")) {
                ExtentReport.getTest().log(Status.INFO, "Setting supplier status in request");
                LogUtils.info("Setting supplier status in request");
                supplierUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            }
            if (requestBodyJson.has("user_id")) {
                ExtentReport.getTest().log(Status.INFO, "Setting supplier status in request");
                LogUtils.info("Setting supplier status in request");
                supplierUpdateRequest.setUser_id(requestBodyJson.getString("user_id"));
            }
            
            response = ResponseUtil.getResponseWithAuth(baseURI, supplierUpdateRequest, httpsmethod, access);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
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
                actualJsonBody = new JSONObject(response.asString());
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(4));
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedResponseJson = new JSONObject(expectedResponseBody);
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseJson.toString(4));
                    
                    // Validate response message
                    if (expectedResponseJson.has("detail") && actualJsonBody.has("detail")) {
                        String expectedDetail = expectedResponseJson.getString("detail");
                        String actualDetail = actualJsonBody.getString("detail");
                        
                        // Check number of sentences in the response message
                        int sentenceCount = countSentences(actualDetail);
                        if (sentenceCount > 6) {
                            String errorMsg = "Response message contains more than 6 sentences (" + sentenceCount + " found)";
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Response message: " + actualDetail);
                        } else {
                            LogUtils.info("Response message sentence count validation passed: " + sentenceCount + " sentences found");
                            ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount + " sentences found");
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
                    validateResponseBody.handleResponseBody(response, expectedResponseJson);
                }
                
                LogUtils.success(logger, "Supplier update negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier update negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        } catch (Exception e) {
            String errorMsg = "Error in supplier update negative test: " + e.getMessage();
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