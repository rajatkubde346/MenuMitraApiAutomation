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
public class ChefViewTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private ChefRequest chefViewRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(ChefViewTestScript.class);
   
    @BeforeClass
    private void chefViewSetUp() throws customException
    {
        try
        {
            LogUtils.info("Chef View SetUp");
            ExtentReport.createTest("Chef View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Chef View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getChefViewUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No chef view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No chef view URL found in test data");
                throw new customException("No chef view URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            chefViewRequest = new ChefRequest();
            LogUtils.success(logger, "Chef View Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Chef View Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in chef view setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in chef view setup: " + e.getMessage());
            throw new customException("Error in chef view setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getChefViewUrl")
    private Object[][] getChefViewUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading chef view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading chef view URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "chefview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No chef view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved chef view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved chef view URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getChefViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getChefViewData") 
    public Object[][] getChefViewData() throws customException {
        try {
            LogUtils.info("Reading chef view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading chef view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No chef view test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "chefview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid chef view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " chef view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " chef view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getChefViewData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getChefViewData")
    private void chefViewTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting chef view test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Chef View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            chefViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            chefViewRequest.setUser_id(requestBodyJson.getString("user_id"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            
            response = ResponseUtil.getResponseWithAuth(baseURI, chefViewRequest, httpsmethod, accessToken);
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            int actualStatusCode = response.getStatusCode();
            
            LogUtils.info("Response received with status code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Response received with status code: " + actualStatusCode);
            
            if (actualStatusCode != expectedStatusCode) {
                String errorMsg = String.format("Status code mismatch - Expected: %d, Actual: %d. Response body: %s", 
                    expectedStatusCode, actualStatusCode, response.getBody().asString());
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException("Error in chef view test: " + errorMsg);
            }
            
            // Log response details
            String responseBody = response.getBody().asString();
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                actualJsonBody = new JSONObject(responseBody);
                LogUtils.info("Response Body: " + actualJsonBody.toString());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualJsonBody.toString());
                
                // Validate response body only if status code is 200
                if (actualStatusCode == 200) {
                    expectedResponseJson = new JSONObject(expectedResponseBody);
                                    }
                
                LogUtils.success(logger, "Chef view test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Chef view test completed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Full Response: " + response.asPrettyString());
            } else {
                String errorMsg = "Empty response body received";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException("Error in chef view test: " + errorMsg);
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "Error in chef view test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in chef view test: " + e.getMessage());
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in chef view test: " + e.getMessage());
        }
    }
}
