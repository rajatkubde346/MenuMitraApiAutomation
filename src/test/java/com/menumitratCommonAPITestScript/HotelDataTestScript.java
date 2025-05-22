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
import com.menumitra.apiRequest.HotelDataRequest;
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
public class HotelDataTestScript extends APIBase {
    private HotelDataRequest hotelDataRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(HotelDataTestScript.class);

    @DataProvider(name = "getHotelDataUrl")
    public static Object[][] getHotelDataUrl() throws customException {
        try {
            LogUtils.info("Reading Hotel Data API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "gethoteldata".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Hotel Data API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Hotel Data API endpoint data from Excel sheet");
            throw new customException("Error While Reading Hotel Data API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getHotelDataTestData")
    public static Object[][] getHotelDataTestData() throws customException {
        try {
            LogUtils.info("Reading hotel data test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No hotel data test scenario data found in Excel sheet");
                throw new customException("No hotel data test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "gethoteldata".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for hotel data");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading hotel data test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading hotel data test scenario data: " + e.getMessage());
            throw new customException("Error while reading hotel data test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for hotel data test====");
            ExtentReport.createTest("Hotel Data Setup");

            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();

            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            Object[][] hotelDataUrl = getHotelDataUrl();
            if (hotelDataUrl.length > 0) {
                String endpoint = hotelDataUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for hotel data: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No hotel data URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No hotel data URL found in test data");
                throw new customException("No hotel data URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }

            hotelDataRequest = new HotelDataRequest();
            LogUtils.success(logger, "Hotel Data Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Hotel Data Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during hotel data setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during hotel data setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getHotelDataTestData")
    private void getHotelData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting hotel data test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Hotel Data Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Set request payload
            hotelDataRequest.setUser_id(requestBodyJson.getString("user_id"));
            hotelDataRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));

            // Log request details
            ExtentReport.getTest().log(Status.INFO, "Request Payload: " + requestBodyJson.toString(2));
            LogUtils.info("Request Payload: " + requestBodyJson.toString(2));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            
            response = ResponseUtil.getResponseWithAuth(baseURI, hotelDataRequest, httpsmethod, accessToken);

            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Status code validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                // For positive test cases, we don't validate response body
                if (testType.equalsIgnoreCase("positive")) {
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Hotel data retrieved successfully", ExtentColor.GREEN));
                } else {
                    // For negative test cases, validate response body
                    actualResponseBody = new JSONObject(response.asString());
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                    
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
            }
            throw new customException(errorMsg);
        }
    }

    @AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down started===");
            ExtentReport.createTest("Hotel Data Test Teardown");
            
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
