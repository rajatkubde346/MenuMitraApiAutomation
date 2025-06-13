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
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class ActivityLogTestScript extends APIBase 
{
 ;
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
            LogUtils.info("Reading activity log test scenario data from Excel");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No activity log test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : testData) {
                if (row != null && row.length >= 3 && 
                    "activitylog".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }

            return result;
        } catch (Exception e) {
            LogUtils.error("Error reading activity log test scenario data: " + e.getMessage());
            throw new customException("Error reading activity log test scenario data: " + e.getMessage());
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

            if (apiName.equalsIgnoreCase("activitylog")) {
                LogUtils.info("Processing activity log request");
                Map<String,String> data = new HashMap<String, String>();
                data.put("user_id", String.valueOf(userId));
                
                LogUtils.info("Constructing request body");
                ExtentReport.getTest().log(Status.INFO, "Constructing request body");
                LogUtils.info("Sending POST request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending POST request for activity log");
                response = ResponseUtil.getResponseWithAuth(baseUri, data, httpsmethod, accessToken); 

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());

                int expectedStatusCode = Integer.parseInt(statusCode);
                if (response.getStatusCode() != expectedStatusCode) {
                    String errorMsg = "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    throw new customException(errorMsg);
                }

                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());

                // Validate response body
                if (response.getBody() != null && !response.getBody().asString().isEmpty()) {
                    actualResponseBody = new JSONObject(response.getBody().asString());
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedJsonBody = new JSONObject(expectedResponseBody);
                        validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    }
                    
                    LogUtils.success(logger, "Activity log test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Activity log test completed successfully", ExtentColor.GREEN));
                } else {
                    String errorMsg = "Response body is empty";
                    LogUtils.error(errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, errorMsg);
                    throw new customException(errorMsg);
                }
            } else {
                String errorMsg = "Invalid API name: " + apiName;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error in activity log test: " + e.getMessage();
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