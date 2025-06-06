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
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.WareHouseListRequest;
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
public class WareHouseListTestScript extends APIBase {
    private Response response;
    private String baseURI;
    private String accessToken;
    private int user_id;
    private WareHouseListRequest warehouseListRequest;
    private URL url;
    private static final String excelSheetPathForGetApis = "src\\test\\resources\\excelsheet\\apiEndpoint.xlsx";
    Logger logger = LogUtils.getLogger(WareHouseListTestScript.class);

    @BeforeClass
    private void warehouseListSetUp() throws customException {
        try {
            LogUtils.info("Warehouse List SetUp");
            ExtentReport.createTest("Warehouse List SetUp");
            ExtentReport.getTest().log(Status.INFO, "Warehouse List SetUp");

            // Login and verify OTP
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            // Get URL from excel
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null || readExcelData.length == 0) {
                throw new customException("No warehouse list URL found in test data");
            }

            // Find the warehouselist endpoint
            String endpoint = null;
            for (int i = 0; i < readExcelData.length; i++) {
                if (readExcelData[i] != null && readExcelData[i].length > 2 
                    && "warehouselist".equalsIgnoreCase(Objects.toString(readExcelData[i][0], ""))) {
                    endpoint = Objects.toString(readExcelData[i][2], "");
                    break;
                }
            }

            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new customException("No warehouse list URL found in test data");
            }

            url = new URL(endpoint);
            baseURI = RequestValidator.buildUri(endpoint, baseURI);
            LogUtils.info("Constructed base URI: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);

            // Get access token and user ID
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                throw new customException("Failed to get access token");
            }

            warehouseListRequest = new WareHouseListRequest();
            LogUtils.success(logger, "Warehouse List SetUp completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Warehouse List SetUp completed successfully");
        } catch (Exception e) {
            LogUtils.failure(logger, "Warehouse List SetUp failed: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Warehouse List SetUp failed: " + e.getMessage());
            throw new customException("Warehouse List SetUp failed: " + e.getMessage());
        }
    }

    @DataProvider(name = "getWarehouseListData")
    public Object[][] getWarehouseListData() throws customException {
        try {
            LogUtils.info("Reading warehouse list test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading warehouse list test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "warehouselist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid warehouse list test data found after filtering";
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
            String errorMsg = "Error while reading warehouse list test scenario data: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getWarehouseListData")
    public void warehouseListTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting warehouse list test case: " + testCaseid);
            LogUtils.info("Test Type: " + testType);
            LogUtils.info("HTTP Method: " + httpsmethod);
            ExtentReport.createTest("Warehouse List Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("warehouselist")) {
                // Set required parameters
                warehouseListRequest = new WareHouseListRequest();
                warehouseListRequest.setUserId(user_id);
                warehouseListRequest.setAppSource("android"); // Default app source

                // If request body is provided, override default values
                if (requestBody != null && !requestBody.trim().isEmpty()) {
                    JSONObject requestBodyJson = new JSONObject(requestBody);
                    if (requestBodyJson.has("app_source")) {
                        warehouseListRequest.setAppSource(requestBodyJson.getString("app_source"));
                    }
                }

                // Log request details
                LogUtils.info("Request details - UserId: " + warehouseListRequest.getUserId() + 
                            ", AppSource: " + warehouseListRequest.getAppSource());
                ExtentReport.getTest().log(Status.INFO, "Request details - UserId: " + warehouseListRequest.getUserId() + 
                                         ", AppSource: " + warehouseListRequest.getAppSource());

                // Make API call
                LogUtils.info("Making API call to: " + baseURI);
                response = ResponseUtil.getResponseWithAuth(baseURI, warehouseListRequest, httpsmethod, accessToken);

                // Log response details
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate response
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Test case " + testCaseid + " passed");
                    ExtentReport.getTest().log(Status.PASS, "Test case " + testCaseid + " passed");
                } else {
                    String errorMsg = "Test case " + testCaseid + " failed. Expected status code: " + 
                                    statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, errorMsg);
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            String errorMsg = "Test case " + testCaseid + " failed: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    private Object[][] getWarehouseListUrl() throws customException {
        try {
            LogUtils.info("Reading Warehouse List API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Warehouse List API endpoint data");
            
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
                    "warehouselist".equalsIgnoreCase(Objects.toString(row[0], "").trim()) &&
                    row[2] != null && !row[2].toString().trim().isEmpty()) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No warehouse list URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            LogUtils.info("Successfully retrieved warehouse list URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved warehouse list URL data");
            return result;
        } catch (Exception e) {
            String errorMsg = "Error while reading warehouse list URL data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }
}
