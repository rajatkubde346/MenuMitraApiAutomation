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
import com.menumitra.apiRequest.WareHouseUpdateRequest;
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
public class WareHouseUpdateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private int user_id;
    private WareHouseUpdateRequest warehouseUpdateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private JSONObject expectedResponseJson;
    private static final String excelSheetPathForGetApis = "src\\test\\resources\\excelsheet\\apiEndpoint.xlsx";
    private static final String SHEET_NAME_COMMON_API = "commonAPI";
    private static final String SHEET_NAME_TEST_SCENARIO = "CommonAPITestScenario";
    private static final String API_NAME = "warehouseupdate";
    private static final String TEST_TYPE = "positive";
    Logger logger = LogUtils.getLogger(WareHouseUpdateTestScript.class);

    @BeforeClass
    private void warehouseUpdateSetUp() throws customException {
        try {
            String setupMessage = "Warehouse Update SetUp";
            LogUtils.info(setupMessage);
            ExtentReport.createTest(setupMessage);
            ExtentReport.getTest().log(Status.INFO, setupMessage);

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getWarehouseUpdateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No warehouse update URL found in test data";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                String errorMsg = "Failed to get access token";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            warehouseUpdateRequest = new WareHouseUpdateRequest();
            LogUtils.success(logger, setupMessage + " completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(setupMessage + " completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            String errorMsg = "Error in Warehouse Update SetUp: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getWarehouseUpdateUrl")
    private Object[][] getWarehouseUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Warehouse Update API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Warehouse Update API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
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
                    "warehouseupdate".equalsIgnoreCase(Objects.toString(row[0], "").trim()) &&
                    row[2] != null && !row[2].toString().trim().isEmpty()) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No warehouse update URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            LogUtils.info("Successfully retrieved warehouse update URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved warehouse update URL data");
            return result;
        } catch (Exception e) {
            String errorMsg = "Error while reading warehouse update URL data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getWarehouseUpdateData")
    public Object[][] getWarehouseUpdateData() throws customException {
        try {
            String message = "Reading warehouse update test scenario data";
            LogUtils.info(message);
            ExtentReport.getTest().log(Status.INFO, message);

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, SHEET_NAME_TEST_SCENARIO);
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    API_NAME.equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    TEST_TYPE.equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid warehouse update test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            String errorMsg = "Error in getting warehouse update test data: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getWarehouseUpdateData")
    public void warehouseUpdateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            String testMessage = "Warehouse Update Test - " + testCaseid;
            LogUtils.info("Starting " + testMessage);
            ExtentReport.createTest(testMessage);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (API_NAME.equalsIgnoreCase(apiName)) {
                // Validate request body
                if (requestBody == null || requestBody.trim().isEmpty()) {
                    String errorMsg = "Request body cannot be empty for test case: " + testCaseid;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                try {
                    requestBodyJson = new JSONObject(requestBody);
                    
                    // Ensure warehouse_id is present in the request
                    if (!requestBodyJson.has("warehouse_id")) {
                        requestBodyJson.put("warehouse_id", 1); // Default warehouse ID
                        LogUtils.info("Added default warehouse_id: 1 to request");
                    }
                } catch (Exception e) {
                    String errorMsg = "Invalid JSON request body for test case: " + testCaseid + ". Error: " + e.getMessage();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Validate required fields before setting parameters
                validateRequiredFields(requestBodyJson, testCaseid);
                
                // Set request parameters
                setRequestParameters(requestBodyJson);

                LogUtils.info("Request Body: " + requestBodyJson);
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson);

                // Validate baseURI before making request
                if (baseURI == null || baseURI.trim().isEmpty()) {
                    String errorMsg = "Base URI is not properly initialized";
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                response = ResponseUtil.getResponseWithAuth(baseURI, warehouseUpdateRequest, httpsmethod, accessToken);

                logResponse(response);
                validateStatusCode(response, statusCode);
                validateResponseBody(response, expectedResponseBody);

                // Enhanced success logging
                if (response.getStatusCode() == 200) {
                    String successMsg = String.format("Warehouse update test case %s passed successfully with status code 200", testCaseid);
                    LogUtils.success(logger, successMsg);
                    
                    // Log success details
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Response Status Code: 200", ExtentColor.GREEN));
                    
                    // Log response details in a structured way
                    JSONObject responseJson = new JSONObject(response.asString());
                    logSuccessResponse(responseJson, testCaseid);
                    
                    // Add response to report
                    ExtentReport.getTest().log(Status.INFO, "Full Response Details:");
                    ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
                }
            }
        } catch (Exception e) {
            handleTestException(e, testCaseid);
        }
    }

    private void validateRequiredFields(JSONObject requestBody, String testCaseid) throws customException {
        List<String> requiredFields = Arrays.asList(
            "warehouse_id",
            "app_source",
            "location",
            "address",
            "manager_name",
            "manager_mobile",
            "warehouse_type",
            "capacity_unit",
            "capacity_value",
            "is_active"
        );

        List<String> missingFields = new ArrayList<>();
        for (String field : requiredFields) {
            if (!requestBody.has(field) || requestBody.isNull(field) || requestBody.get(field).toString().trim().isEmpty()) {
                missingFields.add(field);
            }
        }

        if (!missingFields.isEmpty()) {
            String errorMsg = "Missing or empty required fields for test case " + testCaseid + ": " + String.join(", ", missingFields);
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    private void setRequestParameters(JSONObject requestBody) {
        try {
            warehouseUpdateRequest.setUserId(user_id);
            
            // Set required fields with validation
            warehouseUpdateRequest.setWarehouseId(validateAndGetInt(requestBody, "warehouse_id"));
            warehouseUpdateRequest.setAppSource(validateAndGetString(requestBody, "app_source"));
            warehouseUpdateRequest.setLocation(validateAndGetString(requestBody, "location"));
            warehouseUpdateRequest.setAddress(validateAndGetString(requestBody, "address"));
            warehouseUpdateRequest.setManagerName(validateAndGetString(requestBody, "manager_name"));
            warehouseUpdateRequest.setManagerMobile(validateAndGetString(requestBody, "manager_mobile"));
            warehouseUpdateRequest.setWarehouseType(validateAndGetString(requestBody, "warehouse_type"));
            warehouseUpdateRequest.setCapacityUnit(validateAndGetString(requestBody, "capacity_unit"));
            warehouseUpdateRequest.setCapacityValue(validateAndGetInt(requestBody, "capacity_value"));
            warehouseUpdateRequest.setIsActive(validateAndGetInt(requestBody, "is_active"));

            // Set optional fields
            String altMobile = requestBody.optString("manager_alternate_mobile", null);
            if (altMobile != null && !altMobile.isEmpty()) {
                warehouseUpdateRequest.setManagerAlternateMobile(altMobile);
            }
        } catch (Exception e) {
            String errorMsg = "Error setting request parameters: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new RuntimeException(errorMsg);
        }
    }

    private String validateAndGetString(JSONObject json, String key) {
        String value = json.optString(key, "").trim();
        if (value.isEmpty()) {
            throw new RuntimeException("Required field '" + key + "' is empty or missing");
        }
        return value;
    }

    private int validateAndGetInt(JSONObject json, String key) {
        if (!json.has(key)) {
            throw new RuntimeException("Required field '" + key + "' is missing");
        }
        return json.getInt(key);
    }

    private void logResponse(Response response) {
        String statusCode = "Response Status Code: " + response.getStatusCode();
        String responseBody = "Response Body: " + response.asString();
        
        LogUtils.info(statusCode);
        LogUtils.info(responseBody);
        ExtentReport.getTest().log(Status.INFO, statusCode);
        ExtentReport.getTest().log(Status.INFO, responseBody);
    }

    private void validateStatusCode(Response response, String expectedStatusCode) throws customException {
        int expectedCode = Integer.parseInt(expectedStatusCode);
        int actualCode = response.getStatusCode();
        
        if (actualCode != expectedCode) {
            String errorMsg = String.format("Status code mismatch - Expected: %d, Actual: %d", 
                expectedCode, actualCode);
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        } else if (actualCode == 200) {
            String successMsg = "Status code validation passed: 200 OK";
            LogUtils.success(logger, successMsg);
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
        }
    }

    private void validateResponseBody(Response response, String expectedResponseBody) throws customException {
        try {
            actualJsonBody = new JSONObject(response.asString());
            
            if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                expectedResponseJson = new JSONObject(expectedResponseBody);
                validateResponseBody.handleResponseBody(response, expectedResponseJson);
                
                if (response.getStatusCode() == 200) {
                    // Log successful validation
                    String successMsg = "Response body validation passed successfully";
                    LogUtils.success(logger, successMsg);
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
                    
                    // Compare and log specific fields if needed
                    compareResponseFields(actualJsonBody, expectedResponseJson);
                }
            }
        } catch (Exception e) {
            String errorMsg = "Error validating response body: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    private void compareResponseFields(JSONObject actual, JSONObject expected) {
        try {
            StringBuilder comparison = new StringBuilder();
            comparison.append("\n=== Response Field Comparison ===\n");
            
            for (String key : expected.keySet()) {
                if (actual.has(key)) {
                    comparison.append(String.format("%-20s: %s (Expected) = %s (Actual)%n", 
                        key, expected.get(key), actual.get(key)));
                }
            }
            
            comparison.append("===============================\n");
            LogUtils.info(comparison.toString());
            ExtentReport.getTest().log(Status.INFO, comparison.toString());
        } catch (Exception e) {
            LogUtils.error("Error while comparing response fields: " + e.getMessage());
        }
    }

    private void logSuccessResponse(JSONObject responseJson, String testCaseid) {
        try {
            // Create a formatted success message
            StringBuilder successDetails = new StringBuilder();
            successDetails.append("\n=== Successful Response Details ===\n");
            successDetails.append("Test Case ID: ").append(testCaseid).append("\n");
            successDetails.append("Status Code: 200 (Success)\n");
            successDetails.append("Timestamp: ").append(java.time.LocalDateTime.now()).append("\n");
            
            // Add response data
            successDetails.append("\nResponse Data:\n");
            for (String key : responseJson.keySet()) {
                successDetails.append(String.format("%-20s: %s%n", key, responseJson.get(key)));
            }
            successDetails.append("\n===============================\n");

            // Log the formatted message
            LogUtils.info(successDetails.toString());
            ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Response Details", ExtentColor.BLUE));
            ExtentReport.getTest().log(Status.INFO, successDetails.toString());
            
            // Add success badge
            ExtentReport.getTest().log(Status.PASS, 
                MarkupHelper.createLabel("✓ Test Case " + testCaseid + " Executed Successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            LogUtils.error("Error while logging success response: " + e.getMessage());
        }
    }

    private void handleTestException(Exception e, String testCaseid) throws customException {
        String errorMsg = "Error in warehouse update test case " + testCaseid + ": " + e.getMessage();
        LogUtils.exception(logger, errorMsg, e);
        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
        if (response != null) {
            ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
        }
        throw new customException(errorMsg);
    }

    @AfterClass
    private void tearDown() {
        LogUtils.info("Warehouse Update Test Script completed");
        ExtentReport.getTest().log(Status.INFO, "Warehouse Update Test Script completed");
    }
}
