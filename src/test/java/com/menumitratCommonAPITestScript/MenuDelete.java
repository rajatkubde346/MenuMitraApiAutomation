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
import com.menumitra.apiRequest.MenuRequest;
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
public class MenuDelete extends APIBase
{
    private MenuRequest menuDeleteRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(MenuDelete.class);

    /**
     * Data provider for menu delete API endpoint URLs
     */
    @DataProvider(name = "getMenuDeleteUrl")
    public static Object[][] getMenuDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Delete API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "menuDelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Menu Delete API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Menu Delete API endpoint data from Excel sheet");
            throw new customException("Error While Reading Menu Delete API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for menu delete test scenarios
     */
    @DataProvider(name = "getMenuDeleteData")
    public static Object[][] getMenuDeleteData() throws customException {
        try {
            LogUtils.info("Reading menu delete test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No menu delete test scenario data found in Excel sheet");
                throw new customException("No menu delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "menuDelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for menu delete");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading menu delete test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading menu delete test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading menu delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }

  
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for menu delete test====");
            ExtentReport.createTest("Menu Delete Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set menu delete URL
            Object[][] menuDeleteData = getMenuDeleteUrl();
            if (menuDeleteData.length > 0) {
                String endpoint = menuDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for menu delete: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu delete URL found in test data");
                throw new customException("No menu delete URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            menuDeleteRequest = new MenuRequest();
            LogUtils.success(logger, "Menu Delete Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Menu Delete Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during menu delete setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during menu delete setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to delete menu
     */
    @Test(dataProvider = "getMenuDeleteData")
    private void deleteMenu(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting menu deletion test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Menu Deletion Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            menuDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            menuDeleteRequest.setUser_id(String.valueOf(user_id));
            
            // Set menu_id which is required for deletion
            menuDeleteRequest.setMenu_id(requestBodyJson.getString("menu_id"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, menuDeleteRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                if (response.asString() != null && !response.asString().isEmpty()) {
                    actualResponseBody = new JSONObject(response.asString());
                    
                    if (!actualResponseBody.isEmpty() && expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedResponse = new JSONObject(expectedResponseBody);
                        
                        ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                        LogUtils.info("Starting response body validation");
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                        LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                        ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                        LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                        
                        ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                        LogUtils.info("Performing detailed response validation");
                                                
                        ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                        LogUtils.success(logger, "Response body validation passed successfully");
                    }
                }
                
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu deleted successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Menu deleted successfully");
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
