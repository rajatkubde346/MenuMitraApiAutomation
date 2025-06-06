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
import com.menumitra.apiRequest.MangerCreateRequest;
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
public class MangerCreateTestScript extends APIBase {
    private MangerCreateRequest mangerCreateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(MangerCreateTestScript.class);

    @DataProvider(name = "getMangerCreateUrl")
    public static Object[][] getMangerCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Manger Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> "mangercreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Manger Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Manger Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Manger Create API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getMangerCreateData")
    public static Object[][] getMangerCreateData() throws customException {
        try {
            LogUtils.info("Reading manger create test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No manger create test scenario data found in Excel sheet");
                throw new customException("No manger create test scenario data found in Excel sheet");
            }
            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 2 &&
                        "mangercreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for manger create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading manger create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading manger create test scenario data: " + e.getMessage());
            throw new customException("Error while reading manger create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name = "getMangerCreateNegativeData")
    public static Object[][] getMangerCreateNegativeData() throws customException {
        try {
            LogUtils.info("Reading manger create negative test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                throw new customException("Error fetching data from Excel sheet - Data is null");
            }
            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                        "mangercreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            if (filteredData.isEmpty()) {
                throw new customException("No valid manger create negative test data found");
            }
            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            throw new customException("Error in getting manger create negative test data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for manger create test====");
            ExtentReport.createTest("Manger Create Setup");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            Object[][] mangerCreateData = getMangerCreateUrl();
            if (mangerCreateData.length > 0) {
                String endpoint = mangerCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for manger create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                throw new customException("No manger create URL found in test data");
            }
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            mangerCreateRequest = new MangerCreateRequest();
            LogUtils.success(logger, "Manger Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Manger Create Setup completed successfully");
        } catch (Exception e) {
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getMangerCreateData")
    private void createManger(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting manger create test case: " + testCaseid);
            ExtentReport.createTest("Manger Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            requestBodyJson = new JSONObject(requestBodyPayload);
            mangerCreateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            mangerCreateRequest.setUser_id(requestBodyJson.getString("user_id"));
            mangerCreateRequest.setName(requestBodyJson.getString("name"));
            mangerCreateRequest.setMobile(requestBodyJson.getString("mobile"));
            mangerCreateRequest.setAddress(requestBodyJson.getString("address"));
            mangerCreateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));

            // Log payload and request body
            ExtentReport.getTest().log(Status.INFO, "Payload: " + requestBodyPayload);
            LogUtils.info("Payload: " + requestBodyPayload);

            response = ResponseUtil.getResponseWithAuth(baseURI, mangerCreateRequest, httpsmethod, accessToken);

            // Log response body
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

         // Validation
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                
                if (!actualResponseBody.isEmpty()) {
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
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory created successfully", ExtentColor.GREEN));
                } else {
                    ExtentReport.getTest().log(Status.INFO, "Response body is empty");
                    LogUtils.info("Response body is empty");
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
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getMangerCreateNegativeData")
    public void mangerCreateNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting manger create negative test case: " + testCaseid);
            ExtentReport.createTest("Manger Create Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            requestBodyJson = new JSONObject(requestBodyPayload);
            mangerCreateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            mangerCreateRequest.setUser_id(requestBodyJson.getString("user_id"));
            mangerCreateRequest.setName(requestBodyJson.getString("name"));
            mangerCreateRequest.setMobile(requestBodyJson.getString("mobile"));
            mangerCreateRequest.setAddress(requestBodyJson.getString("address"));
            mangerCreateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));

            // Log payload and request body
            ExtentReport.getTest().log(Status.INFO, "Payload: " + requestBodyPayload);
            LogUtils.info("Payload: " + requestBodyPayload);

            response = ResponseUtil.getResponseWithAuth(baseURI, mangerCreateRequest, httpsmethod, accessToken);

            // Log response body
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validate status code
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                expectedResponse = new JSONObject(expectedResponseBody);

                // Log actual and expected response
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));

                // Validate response body
                validateResponseBody.handleResponseBody(response, expectedResponse);
                ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                LogUtils.success(logger, "Response body validation passed successfully");
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    //@AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down started===");
            ExtentReport.createTest("Manger Create Test Teardown");
            ActionsMethods.logout();
            TokenManagers.clearTokens();
            LogUtils.info("===Test environment tear down completed successfully===");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}