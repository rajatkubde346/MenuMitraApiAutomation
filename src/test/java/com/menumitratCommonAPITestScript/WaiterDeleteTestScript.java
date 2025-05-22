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
import com.menumitra.apiRequest.WaiterRequest;
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
public class WaiterDeleteTestScript extends APIBase
{
    private WaiterRequest waiterDeleteRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private JSONObject expectedResponseJson;
    private Logger logger = LogUtils.getLogger(WaiterDeleteTestScript.class);

    /**
     * Data provider for waiter delete API endpoint URLs
     */
    @DataProvider(name = "getWaiterDeleteUrl")
    public static Object[][] getWaiterDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Waiter Delete API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "waiterDelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Waiter Delete API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Waiter Delete API endpoint data from Excel sheet");
            throw new customException("Error While Reading Waiter Delete API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for waiter delete test scenarios
     */
    @DataProvider(name = "getWaiterDeleteData")
    public static Object[][] getWaiterDeleteData() throws customException {
        try {
            LogUtils.info("Reading waiter delete test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No waiter delete test scenario data found in Excel sheet");
                throw new customException("No waiter delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "waiterDelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for waiter delete");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading waiter delete test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading waiter delete test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading waiter delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }

  
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for waiter delete test====");
            ExtentReport.createTest("Waiter Delete Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set waiter delete URL
            Object[][] waiterDeleteData = getWaiterDeleteUrl();
            if (waiterDeleteData.length > 0) {
                String endpoint = waiterDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for waiter delete: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No waiter delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No waiter delete URL found in test data");
                throw new customException("No waiter delete URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            waiterDeleteRequest = new WaiterRequest();
            LogUtils.success(logger, "Waiter Delete Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Waiter Delete Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during waiter delete setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during waiter delete setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to delete waiter
     */
    @Test(dataProvider = "getWaiterDeleteData")
    private void deleteWaiter(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting waiter deletion test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Waiter Deletion Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            waiterDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            waiterDeleteRequest.setUser_id(requestBodyJson.getString("user_id"));
            
            // Set waiter_id which is required for deletion
            waiterDeleteRequest.setupdate_user_id(requestBodyJson.getString("update_user_id"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, waiterDeleteRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Status code validation only
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                // Log response without validation
                if (response.asString() != null && !response.asString().isEmpty()) {
                    actualResponseBody = new JSONObject(response.asString());
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
                }
                
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Waiter deleted successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Waiter deleted successfully");
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
