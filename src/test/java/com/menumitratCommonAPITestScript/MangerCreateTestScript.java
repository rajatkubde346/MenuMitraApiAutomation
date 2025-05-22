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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class MangerCreateTestScript extends APIBase {
    private MangerCreateRequest managerCreateRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject expectedJsonBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(MangerCreateTestScript.class);

    @DataProvider(name = "getManagerCreateUrl")
    public Object[][] getManagerCreateUrl() throws Exception {
        try {
            LogUtils.info("Reading Manager Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "managercreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Manager Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Manager Create API endpoint data from Excel sheet");
            throw new Exception("Error While Reading Manager Create API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getManagerCreateData")
    public Object[][] getManagerCreateData() throws Exception {
        try {
            LogUtils.info("Reading manager create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No manager create test scenario data found in Excel sheet");
                throw new Exception("No manager create test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "managercreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for manager create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading manager create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading manager create test scenario data: " + e.getMessage());
            throw new Exception(
                    "Error while reading manager create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws Exception {
        try {
            LogUtils.info("====Starting setup for manager create test====");
            ExtentReport.createTest("Manager Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set manager create URL
            Object[][] managerCreateData = getManagerCreateUrl();
            if (managerCreateData.length > 0) {
                String endpoint = managerCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for manager create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No manager create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No manager create URL found in test data");
                throw new Exception("No manager create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            managerCreateRequest = new MangerCreateRequest();
            LogUtils.success(logger, "Manager Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Manager Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during manager create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during manager create setup: " + e.getMessage());
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getManagerCreateData")
    private void createManager(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws Exception {
        try {
            LogUtils.info("Starting manager creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Manager Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            managerCreateRequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + userId);
            LogUtils.info("Setting user_id in request: " + userId);
            managerCreateRequest.setUser_id(String.valueOf(userId));
            
            ExtentReport.getTest().log(Status.INFO, "Setting manager name in request");
            LogUtils.info("Setting manager name in request");
            managerCreateRequest.setName(requestBodyJson.getString("name"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting mobile number in request");
            LogUtils.info("Setting mobile number in request");
            managerCreateRequest.setMobile(requestBodyJson.getString("mobile_number"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting address in request");
            LogUtils.info("Setting address in request");
            managerCreateRequest.setAddress(requestBodyJson.getString("address"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseURI, managerCreateRequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Report actual vs expected status code
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
            
            if (response.getStatusCode() == expectedStatusCode) {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    expectedJsonBody = new JSONObject(expectedResponseBody);
                                        LogUtils.success(logger, "Successfully created manager");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Successfully created manager", ExtentColor.GREEN));
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                    throw new Exception("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Invalid status code: " + response.getStatusCode());
                throw new Exception("Expected status code " + expectedStatusCode + " but got " + response.getStatusCode());
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during manager creation: " + e.getMessage(), e);
            ExtentReport.getTest().log(Status.FAIL, "Error during manager creation: " + e.getMessage());
            throw new Exception("Error during manager creation: " + e.getMessage());
        }
    }
}
