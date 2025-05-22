package com.menumitratCommonAPITestScript;

import java.io.File;

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

import com.menumitra.apiRequest.staffRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;

import io.restassured.RestAssured;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Test class for Staff Creation API functionality
 */
@Listeners(Listener.class)
public class StaffCreateTestScript extends APIBase {

    private staffRequest staffCreateRequest;
    private Response response;
   
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private String deviceToken;
    Logger logger=LogUtils.getLogger(StaffCreateTestScript.class);
    RequestSpecification request;

    /**
     * Data provider for staff create API endpoint URLs
     */
    @DataProvider(name = "getStaffCreateUrl")
    public static Object[][] getStaffCreateUrl() throws Exception {
        try {
            LogUtils.info("Reading Staff Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "staffcreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Staff Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Staff Create API endpoint data from Excel sheet");
            throw new Exception("Error While Reading Staff Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for staff create test scenarios
     */
    @DataProvider(name = "getStaffCreateData")
    public static Object[][] getStaffCreateData() throws Exception {
        try {
            LogUtils.info("Reading staff create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No staff create test scenario data found in Excel sheet");
                throw new Exception("No staff create test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "staffcreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for staff create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading staff create test scenario data: " + e.getMessage());
            throw new Exception(
                    "Error while reading staff create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws Exception {
        try {
            LogUtils.info("====Starting setup for staff create test====");
            ExtentReport.createTest("Staff Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseUri);
           
            // Get and set staff create URL
            Object[][] staffCreateData = getStaffCreateUrl();
            if (staffCreateData.length > 0) {
                String endpoint = staffCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Constructed base URI for staff create: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseUri);
            } else {
                LogUtils.failure(logger, "No staff create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No staff create URL found in test data");
                throw new Exception("No staff create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            staffCreateRequest = new staffRequest();
            LogUtils.success(logger, "Staff Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Staff Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during staff create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during staff create setup: " + e.getMessage());
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create staff member
     */
    @Test(dataProvider = "getStaffCreateData")
    private void createStaff(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws Exception {
        try {
            LogUtils.info("Starting staff creation test case: " + testCaseid);
            ExtentReport.createTest("Staff Creation Test - " + testCaseid);
            
            // Request preparation
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            staffCreateRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            staffCreateRequest.setUser_id(userId);
            staffCreateRequest.setName(requestBodyJson.getString("name"));
            staffCreateRequest.setMobile(requestBodyJson.getString("mobile_number"));
            staffCreateRequest.setAddress(requestBodyJson.getString("address"));
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseUri, staffCreateRequest, httpsmethod, accessToken);
            
            // Log response
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Mark test as passed
            LogUtils.success(logger, "Staff creation test completed");
            ExtentReport.getTest().log(Status.PASS, "Staff creation test completed");
            
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during staff creation: " + e.getMessage(), e);
            ExtentReport.getTest().log(Status.FAIL, "Error during staff creation: " + e.getMessage());
            throw new Exception("Error during staff creation: " + e.getMessage());
        }
    }

    private void tearDown()
    {
        try 
        {
            LogUtils.info("===Test environment tear down successfully===");
           
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
            
            ActionsMethods.logout();
            TokenManagers.clearTokens();
            
        } 
        catch (Exception e) 
        {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}
