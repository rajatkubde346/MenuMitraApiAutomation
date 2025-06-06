package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.menumitra.apiRequest.WareHouseViewRequest;
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
public class WareHouseViewTestScript extends APIBase {
    private Response response;
    private String baseURI;
    private String accessToken;
    private int user_id;
    private WareHouseViewRequest warehouseViewRequest;
    private URL url;
    private static final String excelSheetPathForGetApis = "src\\test\\resources\\excelsheet\\apiEndpoint.xlsx";
    Logger logger = LogUtils.getLogger(WareHouseViewTestScript.class);

    @BeforeClass
    private void warehouseViewSetUp() throws customException {
        try {
            LogUtils.info("Warehouse View SetUp");
            ExtentReport.createTest("Warehouse View SetUp");
            ExtentReport.getTest().log(Status.INFO, "Warehouse View SetUp");

            // Login and verify OTP
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            // Get URL from excel
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null || readExcelData.length == 0) {
                throw new customException("No warehouse view URL found in test data");
            }

            // Find the warehouseview endpoint
            String endpoint = null;
            for (int i = 0; i < readExcelData.length; i++) {
                if (readExcelData[i] != null && readExcelData[i].length > 2 
                    && "warehouseview".equalsIgnoreCase(Objects.toString(readExcelData[i][0], "").trim()) &&
                    readExcelData[i][2] != null && !readExcelData[i][2].toString().trim().isEmpty()) {
                    endpoint = Objects.toString(readExcelData[i][2], "");
                    break;
                }
            }

            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new customException("No warehouse view URL found in test data");
            }

            url = new URL(endpoint);
            baseURI = RequestValidator.buildUri(endpoint, baseURI);
            LogUtils.info("Constructed base URI: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                throw new customException("Failed to get access token");
            }

            LogUtils.success(logger, "Warehouse View SetUp completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Warehouse View SetUp completed successfully");

        } catch (Exception e) {
            String errorMsg = "Error in warehouse view setup: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getWarehouseViewData")
    public Object[][] getWarehouseViewData() throws customException {
        try {
            LogUtils.info("Reading warehouse view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse view test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            // Filter only positive test cases for warehouse view
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "warehouseview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid warehouse view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            return result;
            
        } catch (Exception e) {
            String errorMsg = "Error while reading warehouse view test scenario data: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getWarehouseViewData")
    public void warehouseViewTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting warehouse view test case: " + testCaseid);
            LogUtils.info("Test Type: " + testType);
            LogUtils.info("HTTP Method: " + httpsmethod);
            ExtentReport.createTest("Warehouse View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("warehouseview")) {
                // Initialize request object
                warehouseViewRequest = new WareHouseViewRequest();
                warehouseViewRequest.setUserId(user_id);
                warehouseViewRequest.setAppSource("android"); // Default app source

                // Parse request body if provided
                if (requestBody != null && !requestBody.trim().isEmpty()) {
                    JSONObject requestBodyJson = new JSONObject(requestBody);
                    
                    // Set warehouse ID from request body
                    if (requestBodyJson.has("warehouse_id")) {
                        warehouseViewRequest.setWarehouseId(requestBodyJson.getInt("warehouse_id"));
                    } else {
                        warehouseViewRequest.setWarehouseId(1); // Default warehouse ID if not provided
                    }

                    // Override app source if provided
                    if (requestBodyJson.has("app_source")) {
                        warehouseViewRequest.setAppSource(requestBodyJson.getString("app_source"));
                    }
                }

                // Log request details
                LogUtils.info("Request details - UserId: " + warehouseViewRequest.getUserId() + 
                            ", AppSource: " + warehouseViewRequest.getAppSource() +
                            ", WarehouseId: " + warehouseViewRequest.getWarehouseId());
                ExtentReport.getTest().log(Status.INFO, "Request details - UserId: " + warehouseViewRequest.getUserId() + 
                                         ", AppSource: " + warehouseViewRequest.getAppSource() +
                                         ", WarehouseId: " + warehouseViewRequest.getWarehouseId());

                // Make API call
                LogUtils.info("Making API call to: " + baseURI);
                response = ResponseUtil.getResponseWithAuth(baseURI, warehouseViewRequest, httpsmethod, accessToken);

                // Log response details
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate response status code
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Warehouse view test case passed: " + testCaseid);
                    ExtentReport.getTest().log(Status.PASS, "Warehouse view test case passed: " + testCaseid);
                } else {
                    String errorMsg = "Warehouse view test case failed: " + testCaseid + 
                                    ". Expected status code: " + statusCode + 
                                    ", Actual status code: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, errorMsg);
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            String errorMsg = "Error in warehouse view test case: " + testCaseid + " - " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }
}
