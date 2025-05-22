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
import com.menumitra.apiRequest.ChefRequest;
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
public class ChefUpdateTestScript extends APIBase {
    private ChefRequest chefUpdateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private String accessToken;
    private int user_id;
    private JSONObject expectedResponseJson;
    private Logger logger = LogUtils.getLogger(ChefUpdateTestScript.class);

    @DataProvider(name = "getChefUpdateUrl")
    public static Object[][] getChefUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Chef Update API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "chefupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Chef Update API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Chef Update API endpoint data from Excel sheet");
            throw new customException("Error While Reading Chef Update API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getChefUpdateData")
    public static Object[][] getChefUpdateData() throws customException {
        try {
            LogUtils.info("Reading chef update test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No chef update test scenario data found in Excel sheet");
                throw new customException("No chef update test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "chefupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for chef update");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading chef update test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading chef update test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading chef update test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for chef update test====");
            ExtentReport.createTest("Chef Update Setup");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
            
            // Get and set chef update URL
            Object[][] chefUpdateData = getChefUpdateUrl();
            if (chefUpdateData.length > 0) {
                String endpoint = chefUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for chef update: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No chef update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No chef update URL found in test data");
                throw new customException("No chef update URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            chefUpdateRequest = new ChefRequest();
            LogUtils.success(logger, "Chef Update Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Chef Update Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during chef update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during chef update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getChefUpdateData")
    private void updateChef(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting chef update test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Chef Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            chefUpdateRequest.setUpdate_user_id(String.valueOf(user_id));
            chefUpdateRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            chefUpdateRequest.setName(requestBodyJson.getString("name"));
            chefUpdateRequest.setMobile(requestBodyJson.getString("mobile"));
            chefUpdateRequest.setAddress(requestBodyJson.getString("address"));
            chefUpdateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
            chefUpdateRequest.setDob(requestBodyJson.optString("dob", null));
            chefUpdateRequest.setEmail(requestBodyJson.getString("email"));
            chefUpdateRequest.setUser_id(requestBodyJson.getString("user_id"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            
            response = ResponseUtil.getResponseWithAuth(baseURI, chefUpdateRequest, httpsmethod, accessToken);
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            int actualStatusCode = response.getStatusCode();
            
            LogUtils.info("Response received with status code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Response received with status code: " + actualStatusCode);
            
            if (actualStatusCode != expectedStatusCode) {
                String errorMsg = String.format("Status code mismatch - Expected: %d, Actual: %d. Response body: %s", 
                    expectedStatusCode, actualStatusCode, response.getBody().asString());
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException("Error in chef update test: " + errorMsg);
            }
            
            // Log response details
            String responseBody = response.getBody().asString();
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                actualResponseBody = new JSONObject(responseBody);
                LogUtils.info("Response Body: " + actualResponseBody.toString());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString());
                
                // Validate response body only if status code is 200
                if (actualStatusCode == 200) {
                    expectedResponseJson = new JSONObject(expectedResponseBody);
                                    }
                
                LogUtils.success(logger, "Chef update test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Chef update test completed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Full Response: " + response.asPrettyString());
            } else {
                String errorMsg = "Empty response body received";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException("Error in chef update test: " + errorMsg);
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "Error in chef update test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in chef update test: " + e.getMessage());
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in chef update test: " + e.getMessage());
        }
    }
}
