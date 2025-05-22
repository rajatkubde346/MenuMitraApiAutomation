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
public class ChefCreateTestScript extends APIBase {
    private static final String excelSheetPathForGetApis = "src/test/resources/excelsheet/apiEndpoint.xlsx";
    private ChefRequest chefCreateRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject expectedJsonBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(ChefCreateTestScript.class);

    @DataProvider(name = "getChefCreateUrl")
    public Object[][] getChefCreateUrl() throws Exception {
        try {
            LogUtils.info("Reading Chef Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "chefcreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Chef Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Chef Create API endpoint data from Excel sheet");
            throw new Exception("Error While Reading Chef Create API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getChefCreateData")
    public Object[][] getChefCreateData() throws Exception {
        try {
            LogUtils.info("Reading chef create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No chef create test scenario data found in Excel sheet");
                throw new Exception("No chef create test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "chefcreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for chef create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading chef create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading chef create test scenario data: " + e.getMessage());
            throw new Exception(
                    "Error while reading chef create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws Exception {
        try {
            LogUtils.info("====Starting setup for chef create test====");
            ExtentReport.createTest("Chef Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set chef create URL
            Object[][] chefCreateData = getChefCreateUrl();
            if (chefCreateData.length > 0) {
                String endpoint = chefCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for chef create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No chef create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No chef create URL found in test data");
                throw new Exception("No chef create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            chefCreateRequest = new ChefRequest();
            LogUtils.success(logger, "Chef Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Chef Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during chef create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during chef create setup: " + e.getMessage());
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getChefCreateData")
    private void createChef(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting chef creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Chef Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            chefCreateRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            chefCreateRequest.setUser_id(String.valueOf(userId));
            chefCreateRequest.setName(requestBodyJson.getString("name"));
            chefCreateRequest.setMobile(requestBodyJson.getString("mobile"));
            chefCreateRequest.setAddress(requestBodyJson.getString("address"));
            chefCreateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
            chefCreateRequest.setDob(requestBodyJson.optString("dob", null));
            chefCreateRequest.setEmail(requestBodyJson.getString("email"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            
            response = ResponseUtil.getResponseWithAuth(baseURI, chefCreateRequest, httpsmethod, accessToken);
            
            // Log response details
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Only show response without validation
            JSONObject actualResponseBody = new JSONObject(response.asString());
            LogUtils.info("Chef create response received successfully");
            ExtentReport.getTest().log(Status.PASS, "Chef create response received successfully");
            ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
            
            LogUtils.success(logger, "Chef creation test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Chef creation test completed successfully", ExtentColor.GREEN));
            
        } catch (Exception e) {
            LogUtils.exception(logger, "Error in chef create test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in chef create test: " + e.getMessage());
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in chef create test: " + e.getMessage());
        }
    }
}
