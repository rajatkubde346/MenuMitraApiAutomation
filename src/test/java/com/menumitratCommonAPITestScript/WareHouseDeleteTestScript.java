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
import com.menumitra.apiRequest.WareHouseDeleteRequest;
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
public class WareHouseDeleteTestScript extends APIBase {
    private static final Logger logger = Logger.getLogger(WareHouseDeleteTestScript.class);
    private WareHouseDeleteRequest warehouseDeleteRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private String baseURI;
    private String accessToken;
    private int user_id;
    private URL url;
    private JSONObject actualJsonBody;
    private JSONObject expectedJsonBody;
    private static final String SHEET_NAME = "commonAPI";
    private static final String API_NAME = "warehousedelete";
    private static final String SHEET_NAME_TEST_SCENARIO = "CommonAPITestScenario";

    // Test data variables
    private static final String SUCCESS_STATUS = "success";
    private static final String SUCCESS_MESSAGE = "Warehouse deleted successfully";
    private static final String DEFAULT_APP_SOURCE = "android";
    private static final String SUCCESS_STATUS_CODE = "200";

    @BeforeClass
    private void warehouseDeleteSetUp() throws customException {
        try {
            LogUtils.info("Warehouse Delete SetUp");
            ExtentReport.createTest("Warehouse Delete SetUp");
            ExtentReport.getTest().log(Status.INFO, "Warehouse Delete SetUp");

            // Perform login and OTP verification
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            
            // Set base URI
            baseURI = EnviromentChanges.getBaseUrl();
            
            // Get URL from excel
            Object[][] getUrl = getWarehouseDeleteUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No warehouse delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No warehouse delete URL found in test data");
                throw new customException("No warehouse delete URL found in test data");
            }
            
            // Get access token and user ID
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if(accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error in warehouse delete setup: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getWarehouseDeleteUrl")
    private Object[][] getWarehouseDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Warehouse Delete API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Warehouse Delete API endpoint data");
            
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
                    "warehousedelete".equalsIgnoreCase(Objects.toString(row[0], "").trim()) &&
                    row[2] != null && !row[2].toString().trim().isEmpty()) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No warehouse delete URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            LogUtils.info("Successfully retrieved warehouse delete URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved warehouse delete URL data");
            return result;
        } catch (Exception e) {
            String errorMsg = "Error while reading warehouse delete URL data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getWarehouseDeleteData")
    public Object[][] getWarehouseDeleteData() throws customException {
        try {
            LogUtils.info("Reading warehouse delete test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse delete test scenario data");

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
                        "warehousedelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid warehouse delete test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting warehouse delete test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting warehouse delete test data: " + e.getMessage());
            throw new customException("Error in getting warehouse delete test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getWarehouseDeleteData")
    public void warehouseDeleteTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting warehouse delete test case: " + testCaseid);
            ExtentReport.createTest("Warehouse Delete Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase(API_NAME)) {
                // Initialize request object
                warehouseDeleteRequest = new WareHouseDeleteRequest();
                warehouseDeleteRequest.setUserId(user_id);

                // Parse request body from Excel
                if (requestBody != null && !requestBody.trim().isEmpty()) {
                    requestBodyJson = new JSONObject(requestBody);
                    
                    // Set warehouse ID from request body
                    if (requestBodyJson.has("warehouse_id")) {
                        warehouseDeleteRequest.setWarehouseId(requestBodyJson.getInt("warehouse_id"));
                    }

                    // Set app source from request body or use default
                    String appSource = requestBodyJson.optString("app_source", DEFAULT_APP_SOURCE);
                    warehouseDeleteRequest.setAppSource(appSource);
                }

                // Validate required fields
                if (warehouseDeleteRequest.getWarehouseId() == 0) {
                    String errorMsg = "warehouse_id is required but not set in the request";
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Log request details
                LogUtils.info("Request details: " + warehouseDeleteRequest.toString());
                ExtentReport.getTest().log(Status.INFO, "Request details: " + warehouseDeleteRequest.toString());

                // Make API call
                LogUtils.info("Making API call to: " + baseURI);
                response = ResponseUtil.getResponseWithAuth(baseURI, warehouseDeleteRequest, httpsmethod, accessToken);

                // Log response details
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate response status code
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Failed to delete warehouse. Test case: " + testCaseid +
                                    ". Expected status code: " + statusCode +
                                    ", Actual status code: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Parse and validate response
                actualJsonBody = new JSONObject(response.asString());
                expectedJsonBody = new JSONObject(expectedResponseBody);

                // Log response bodies for comparison
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedJsonBody.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));

                // Validate response body
                validateResponseBody.handleResponseBody(response, expectedJsonBody);

                // Log success
                String successMsg = "Warehouse deleted successfully. Test case: " + testCaseid;
                LogUtils.success(logger, successMsg);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Exception in warehouse delete test case: " + testCaseid + ". Error: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
}
