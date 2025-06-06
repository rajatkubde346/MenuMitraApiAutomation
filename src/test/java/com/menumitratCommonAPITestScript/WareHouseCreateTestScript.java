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
import com.menumitra.apiRequest.WareHouseCreateRequest;
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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class WareHouseCreateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private int user_id;
    private WareHouseCreateRequest warehouseCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private static final String excelSheetPathForGetApis = "src\\test\\resources\\excelsheet\\apiEndpoint.xlsx";
    Logger logger = LogUtils.getLogger(WareHouseCreateTestScript.class);

    @BeforeClass
    private void warehouseCreateSetUp() throws customException {
        try {
            LogUtils.info("Warehouse Create SetUp");
            ExtentReport.createTest("Warehouse Create SetUp");
            ExtentReport.getTest().log(Status.INFO, "Warehouse Create SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getWarehouseCreateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                throw new customException("No warehouse create URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                throw new customException("Failed to get access token");
            }

            warehouseCreateRequest = new WareHouseCreateRequest();
            LogUtils.success(logger, "Warehouse Create SetUp completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Warehouse Create SetUp completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in warehouse create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in warehouse create setup: " + e.getMessage());
            throw new customException("Error in warehouse create setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getWarehouseCreateUrl")
    private Object[][] getWarehouseCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Warehouse Create API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Warehouse Create API endpoint data");
            
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
                    "warehousecreate".equalsIgnoreCase(Objects.toString(row[0], "").trim()) &&
                    row[2] != null && !row[2].toString().trim().isEmpty()) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No warehouse create URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            LogUtils.info("Successfully retrieved warehouse create URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved warehouse create URL data");
            return result;
        } catch (Exception e) {
            String errorMsg = "Error while reading warehouse create URL data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getWarehouseCreateData")
    public Object[][] getWarehouseCreateData() throws customException {
        try {
            LogUtils.info("Reading warehouse create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Total rows in Excel data: " + readExcelData.length);
            List<Object[]> filteredData = new ArrayList<>();

            for (Object[] row : readExcelData) {
                if (row != null) {
                    LogUtils.info("Processing row - API Name: " + Objects.toString(row[0], "null") + 
                                ", Test Type: " + Objects.toString(row[2], "null"));
                    
                    if (row.length >= 3 &&
                            "warehousecreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                            "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                        LogUtils.info("Found matching warehouse create test data");
                        filteredData.add(row);
                    }
                }
            }

            LogUtils.info("Filtered data count: " + filteredData.size());
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid warehouse create test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting warehouse create test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting warehouse create test data: " + e.getMessage());
            throw new customException("Error in getting warehouse create test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getWarehouseCreateData")
    public void warehouseCreateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting warehouse create test case: " + testCaseid);
            LogUtils.info("Test Type: " + testType);
            LogUtils.info("HTTP Method: " + httpsmethod);
            ExtentReport.createTest("Warehouse Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("warehousecreate")) {
                // Create default request body if none provided
                if (requestBody == null || requestBody.trim().isEmpty()) {
                    requestBodyJson = new JSONObject();
                    LogUtils.info("No request body provided, creating default request body");
                } else {
                    LogUtils.info("Raw Request Body received: " + requestBody);
                    requestBodyJson = new JSONObject(requestBody);
                }

                // Log all available keys in request body
                LogUtils.info("Available keys in request body: " + requestBodyJson.keys().toString());

                // Create the request object
                warehouseCreateRequest = new WareHouseCreateRequest();
                warehouseCreateRequest.setUserId(user_id);

                // Set app_source first (most critical field)
                String appSource = requestBodyJson.optString("app_source", 
                                 requestBodyJson.optString("appSource", "android"));
                LogUtils.info("Setting app_source to: " + appSource);
                warehouseCreateRequest.setAppSource(appSource);

                // Set other required fields
                warehouseCreateRequest.setLocation(requestBodyJson.optString("location", "Default Location"));
                warehouseCreateRequest.setAddress(requestBodyJson.optString("address", "Default Address"));
                warehouseCreateRequest.setManagerName(requestBodyJson.optString("manager_name", 
                                                    requestBodyJson.optString("managerName", "Default Manager")));
                warehouseCreateRequest.setManagerMobile(requestBodyJson.optString("manager_mobile",
                                                      requestBodyJson.optString("managerMobile", "1234567890")));
                warehouseCreateRequest.setWarehouseType(requestBodyJson.optString("warehouse_type",
                                                      requestBodyJson.optString("warehouseType", "standard")));
                warehouseCreateRequest.setCapacityUnit(requestBodyJson.optString("capacity_unit",
                                                     requestBodyJson.optString("capacityUnit", "sqft")));
                warehouseCreateRequest.setCapacityValue(requestBodyJson.optInt("capacity_value",
                                                      requestBodyJson.optInt("capacityValue", 100)));
                warehouseCreateRequest.setIsActive(requestBodyJson.optInt("is_active",
                                                 requestBodyJson.optInt("isActive", 1)));

                // Optional field
                String altMobile = requestBodyJson.optString("manager_alternate_mobile",
                                 requestBodyJson.optString("managerAlternateMobile", null));
                if (altMobile != null) {
                    warehouseCreateRequest.setManagerAlternateMobile(altMobile);
                }

                // Validate required fields before making the request
                if (warehouseCreateRequest.getAppSource() == null || warehouseCreateRequest.getAppSource().isEmpty()) {
                    throw new customException("app_source is required but not set in the request");
                }

                // Log the final request that will be sent
                LogUtils.info("Final Request to be sent: " + warehouseCreateRequest.toString());
                ExtentReport.getTest().log(Status.INFO, "Final Request: " + warehouseCreateRequest.toString());

                // Make the API call
                LogUtils.info("Making API call to: " + baseURI);
                LogUtils.info("Using access token: " + accessToken);
                response = ResponseUtil.getResponseWithAuth(baseURI, warehouseCreateRequest, httpsmethod, accessToken);

                // Log response details
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Headers: " + response.getHeaders());
                LogUtils.info("Response Body: " + response.asString());

                // Parse response body
                JSONObject responseBody = new JSONObject(response.asString());
                
                // Check for error messages in response
                if (response.getStatusCode() != 200) {
                    String errorDetail = responseBody.optString("detail", "No error detail provided");
                    LogUtils.error("API Error: " + errorDetail);
                    ExtentReport.getTest().log(Status.ERROR, "API Error: " + errorDetail);
                    
                    // Log additional request details for debugging
                    LogUtils.error("Request URL: " + baseURI);
                    LogUtils.error("Request Method: " + httpsmethod);
                    LogUtils.error("Request Headers: " + response.getHeaders());
                    LogUtils.error("Request Body: " + warehouseCreateRequest.toString());
                    
                    throw new customException("API Error - Status Code: " + response.getStatusCode() + 
                                           ", Error Detail: " + errorDetail);
                }

                // Validate response
                actualJsonBody = responseBody;
                LogUtils.info("Warehouse create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Warehouse create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                // Compare expected vs actual status code
                int expectedStatusCode = Integer.parseInt(statusCode);
                if (response.getStatusCode() != expectedStatusCode) {
                    String errorMsg = "Status code mismatch - Expected: " + expectedStatusCode + 
                                    ", Actual: " + response.getStatusCode() + 
                                    ", Response: " + response.asString();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                LogUtils.success(logger, "Warehouse create test completed successfully");
                ExtentReport.getTest().log(Status.PASS, 
                    MarkupHelper.createLabel("Warehouse create test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in warehouse create test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    // Helper method to find values with different possible key names
    private String findValueCaseInsensitive(JSONObject json, String... possibleKeys) {
        for (String key : possibleKeys) {
            try {
                return json.getString(key);
            } catch (Exception e) {
                // Try next key
                continue;
            }
        }
        LogUtils.info("No matching key found for: " + String.join(", ", possibleKeys));
        return null;
    }

    @AfterClass
    private void tearDown() {
        LogUtils.info("Completing warehouse create test execution");
        ExtentReport.getTest().log(Status.INFO, "Completing warehouse create test execution");
    }
}

