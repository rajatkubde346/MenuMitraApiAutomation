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
import com.menumitra.apiRequest.OutletRequest;

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
public class OutletSettingsViewTestScript extends APIBase
{
    private OutletRequest outletSettingsRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedJsonBody;
    Logger logger = LogUtils.getLogger(OutletSettingsViewTestScript.class);
    
    /**
     * Data provider for outlet settings view API endpoint URLs
     */
    @DataProvider(name = "getOutletSettingsViewUrl")
    public Object[][] getOutletSettingsViewUrl() throws customException {
        try {
            LogUtils.info("Reading Outlet Settings View API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "outletsettingsview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Outlet Settings View API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Outlet Settings View API endpoint data from Excel sheet");
            throw new customException("Error While Reading Outlet Settings View API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for outlet settings view test scenarios
     */
    @DataProvider(name = "getOutletSettingsViewData")
    public Object[][] getOutletSettingsViewData() throws customException {
        try {
            LogUtils.info("Reading outlet settings view test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No outlet settings view test scenario data found in Excel sheet");
                throw new customException("No outlet settings view test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "outletsettingsview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for outlet settings view");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading outlet settings view test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading outlet settings view test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading outlet settings view test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for outlet settings view test====");
            ExtentReport.createTest("Outlet Settings View Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set outlet settings view URL
            Object[][] outletSettingsViewData = getOutletSettingsViewUrl();
            if (outletSettingsViewData.length > 0) {
                String endpoint = outletSettingsViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for outlet settings view: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No outlet settings view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No outlet settings view URL found in test data");
                throw new customException("No outlet settings view URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            outletSettingsRequest = new OutletRequest();
            LogUtils.success(logger, "Outlet Settings View Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Outlet Settings View Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during outlet settings view setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during outlet settings view setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method for outlet settings view
     */
    @Test(dataProvider = "getOutletSettingsViewData")
    private void outletSettingsView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting outlet settings view test case: " + testCaseid);
            ExtentReport.createTest("Outlet Settings View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("outletsettingsview")) {
                requestBodyJson = new JSONObject(requestBody);
                outletSettingsRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, outletSettingsRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Only show response without validation
                actualResponseBody = new JSONObject(response.asString());
                LogUtils.info("Outlet settings view response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Outlet settings view response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                
                LogUtils.success(logger, "Outlet settings view test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Outlet settings view test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in outlet settings view test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
}
