package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class ActivityLogTestScript extends APIBase 
{
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private RequestSpecification request;
    private JSONObject expectedJsonBody;
    Logger logger = Logger.getLogger(ActivityLogTestScript.class);
    
    /**
     * Data provider for activity log API endpoint URLs
     */
    @DataProvider(name="getActivityLogUrl")
    public Object[][] getActivityLogUrl() throws customException {
        try {
            LogUtils.info("Reading Activity Log API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "activitylog".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Activity Log API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Activity Log API endpoint data from Excel sheet");
            throw new customException("Error While Reading Activity Log API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for activity log test scenarios
     */
    @DataProvider(name="getActivityLogData")
    public Object[][] getActivityLogData() throws customException {
        try {
            LogUtils.info("Reading activity log test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No activity log test scenario data found in Excel sheet");
                throw new customException("No activity log test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "activitylog".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch(Exception e) {
            LogUtils.error("Error while reading activity log test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading activity log test scenario data: " + e.getMessage());
            throw new customException("Error while reading activity log test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Activity Log SetUp");
            ExtentReport.createTest("Activity Log Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            Object[][] activityLogUrl = getActivityLogUrl();
            if (activityLogUrl.length > 0) 
            {
                String endpoint = activityLogUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri + "" + url.getPath();
                if(url.getQuery() != null) {
                    baseUri += "?" + url.getQuery();
                }
                LogUtils.info("Activity Log URL set to: " + baseUri);
            } else {
                LogUtils.error("No activity log URL found in test data");
                throw new customException("No activity log URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
           
            LogUtils.info("Activity Log Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during activity log setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during activity log setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method for activity log
     */
    @Test(dataProvider="getActivityLogData")
    private void activityLogTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting activity log test: " + description);
            ExtentReport.createTest("Activity Log Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Starting activity log test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("activitylog") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing activity log request");
                JSONObject requestJson = new JSONObject();
                requestJson.put("user_id", userId);
                
                LogUtils.info("Request body: " + requestJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request body: " + requestJson.toString());
                
                LogUtils.info("Sending POST request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending POST request for activity log");
                response = ResponseUtil.getResponseWithAuth(baseUri, requestJson, httpsmethod, accessToken);

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Activity log request successful");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Activity log request successful", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Response Body: " + response.asPrettyString());
                } 
                else {
                    LogUtils.failure(logger, "Activity log request failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Activity log request failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during activity log test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during activity log test execution: " + e.getMessage());
        }
    }
}
