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
import com.menumitra.apiRequest.EbillURLRequest;

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
public class EbillUrlTestScript extends APIBase {
    private EbillURLRequest ebillUrlRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(EbillUrlTestScript.class);

    /**
     * Data provider for ebill URL API endpoint URLs
     */
    @DataProvider(name = "getEbillUrl")
    public static Object[][] getEbillUrl() throws customException {
        try {
            LogUtils.info("Reading Ebill URL API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "ebillurl".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Ebill URL API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Ebill URL API endpoint data from Excel sheet");
            throw new customException("Error While Reading Ebill URL API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for ebill URL test scenarios
     */
    @DataProvider(name = "getEbillUrlData")
    public static Object[][] getEbillUrlData() throws customException {
        try {
            LogUtils.info("Reading ebill URL test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No ebill URL test scenario data found in Excel sheet");
                throw new customException("No ebill URL test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "ebillurl".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for ebill URL");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading ebill URL test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading ebill URL test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading ebill URL test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Data provider for ebill URL negative test scenarios
     */
    @DataProvider(name = "getEbillUrlNegativeData")
    public static Object[][] getEbillUrlNegativeData() throws customException {
        try {
            LogUtils.info("Reading ebill URL negative test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No ebill URL negative test scenario data found in Excel sheet");
                throw new customException("No ebill URL negative test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "ebillurl".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " negative test scenarios for ebill URL");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading ebill URL negative test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading ebill URL negative test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading ebill URL negative test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for ebill URL test====");
            ExtentReport.createTest("Ebill URL Setup");

            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();

            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            // Get and set ebill URL
            Object[][] ebillUrlData = getEbillUrl();
            if (ebillUrlData.length > 0) {
                String endpoint = ebillUrlData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for ebill URL: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No ebill URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No ebill URL found in test data");
                throw new customException("No ebill URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            ebillUrlRequest = new EbillURLRequest();
            LogUtils.success(logger, "Ebill URL Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Ebill URL Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during ebill URL setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during ebill URL setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method for positive test cases
     */
    @Test(dataProvider = "getEbillUrlData")
    private void getEbillUrl(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting ebill URL test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Ebill URL Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Initialize ebill URL request with payload from Excel sheet
            ebillUrlRequest.setInvoice_number(requestBodyJson.getString("invoice_number"));

            LogUtils.info("Ebill URL request initialized with payload from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Ebill URL request initialized with payload from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Request Body: " + requestBodyJson.toString(2));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");

            response = ResponseUtil.getResponseWithAuth(baseURI, ebillUrlRequest, httpsmethod, accessToken);

            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Status code validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Ebill URL retrieved successfully", ExtentColor.GREEN));
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

    /**
     * Test method for negative test cases
     */
    @Test(dataProvider = "getEbillUrlNegativeData")
    private void getEbillUrlNegative(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting ebill URL negative test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Ebill URL Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Initialize ebill URL request with payload from Excel sheet
            ebillUrlRequest.setInvoice_number(requestBodyJson.getString("invoice_number"));

            LogUtils.info("Ebill URL request initialized with payload from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Ebill URL request initialized with payload from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Request Body: " + requestBodyJson.toString(2));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");

            response = ResponseUtil.getResponseWithAuth(baseURI, ebillUrlRequest, httpsmethod, accessToken);

            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                actualResponseBody = new JSONObject(response.asString());
                expectedResponse = new JSONObject(expectedResponseBody);

                ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                LogUtils.info("Starting response body validation");
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));

                ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                LogUtils.info("Performing detailed response validation");
                validateResponseBody.handleResponseBody(response, expectedResponse);

                ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                LogUtils.success(logger, "Response body validation passed successfully");
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

   // @AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down started===");
            ExtentReport.createTest("Ebill URL Test Teardown");

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