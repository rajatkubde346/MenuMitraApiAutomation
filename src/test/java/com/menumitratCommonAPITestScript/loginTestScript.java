package com.menumitratCommonAPITestScript;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.loginRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.HandelConnections;
import com.menumitra.utilityclass.validateResponseBody;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import io.restassured.response.Response;
import java.util.Map;
import java.util.HashMap;


/**
 * Test class for Login API functionality
 * This class extends superclass to inherit common functionality
 */
@Listeners(com.menumitra.utilityclass.Listener.class)
public class loginTestScript extends APIBase 
{

    private String baseUri=null;
    //private String method;
    JSONObject requestBodyJson;
    private URL url;
    private loginRequest loginrequest;
    JSONObject actualResponseBody;
    JSONObject expectedResponse;
    private Response response;
    Logger logger=LogUtils.getLogger(loginTestScript.class);
    @DataProvider(name="getLoginUrl")
    private Object[][] getLoginUrl() throws customException
    {
        try {
            LogUtils.info("=====Reading Login API Endpoint Data=====");
            ExtentReport.getTest().log(Status.INFO, "Reading Login API endpoint configuration");
            Object[][] apiData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            Object[][] filteredData = Arrays.stream(apiData)
                    .filter(row -> "login".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            LogUtils.success(logger, "Successfully retrieved Login API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully loaded Login API configuration");
            return filteredData;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Login API endpoint data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to read Login API endpoint data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Login API endpoint data: " + e.getMessage());
        }
    }

    /**
     * Data provider for positive test scenarios
     * @return Test data array for positive scenarios
     * @throws customException if data reading fails
     */
    @DataProvider(name="getPositiveInputData") 
    private Object[][] getPositiveInputData() throws customException {
        try {
            LogUtils.info("=====Reading Login API Positive Test Data=====");
            ExtentReport.getTest().log(Status.INFO, "Loading positive test scenarios for Login API");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis,property.getProperty("CommonAPITestScenario"));
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No positive test data found for Login API");
                ExtentReport.getTest().log(Status.WARNING, MarkupHelper.createLabel("No positive test scenarios found", ExtentColor.AMBER));
                throw new customException("No Login API positive test data found");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                if (row != null && row.length >= 3 &&
                    "login".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.success(logger, "Successfully loaded " + obj.length + " positive test scenarios");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Loaded " + obj.length + " positive test scenarios", ExtentColor.GREEN));
            return obj;
            
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Login API positive test data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to load positive test data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Login API positive test data: " + e.getMessage());
        }
    }


    /**
     * Data provider for negative test scenarios
     */
    @DataProvider(name="getNegativeInputData")
    private Object[][] getNegativeInputData() throws customException {
        try {
            LogUtils.info("=====Reading Login API Negative Test Data=====");
            ExtentReport.getTest().log(Status.INFO, "Loading negative test scenarios for Login API");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No negative test data found for Login API");
                ExtentReport.getTest().log(Status.WARNING, MarkupHelper.createLabel("No negative test scenarios found", ExtentColor.AMBER));
                throw new customException("No Login API negative test data found");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "login".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            
            LogUtils.success(logger, "Successfully loaded " + obj.length + " negative test scenarios");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Loaded " + obj.length + " negative test scenarios", ExtentColor.GREEN));
            return obj;
            
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Login API negative test data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to load negative test data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Login API negative test data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void LoginAPiTestSetup() throws customException 
    {
        try {
            LogUtils.info("=====Starting Login API Test Setup=====");
            ExtentReport.createTest("Login API Test Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing Login API test environment");
            
            loginrequest = new loginRequest();

            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI configured: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            Object[][] loginUrlData = getLoginUrl();
            if (loginUrlData.length > 0) {
                String endpoint = loginUrlData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.success(logger, "Login endpoint configured: " + baseUri);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Login endpoint configured successfully", ExtentColor.GREEN));
            } else {
                LogUtils.failure(logger, "Failed to configure Login endpoint - No URL found");
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("No Login URL found in test data", ExtentColor.RED));
                throw new customException("Login URL configuration failed");
            }

            LogUtils.success(logger, "Login API test setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test setup completed successfully", ExtentColor.GREEN));

        } catch (Exception e) {
            LogUtils.exception(logger, "Login API test setup failed", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test setup failed: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Login API setup error: " + e.getMessage());
        }
    }
    
    

    @Test(dataProvider = "getPositiveInputData",priority = 1)
    private void verifyloginUsingValidInputData(String apiName,String testCaseid, String testType, String description,
            String httpsmethod,String requestBody,String expectedResponseBody,String statusCode ) throws customException
    {
        try
        {
            LogUtils.info("=====Starting Login API Positive Test Case: " + testCaseid + "=====");
            ExtentReport.createTest("Login API Positive Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if(apiName.contains("login") && testType.contains("positive"))
            {
                requestBodyJson = new JSONObject(requestBody);
                loginrequest.setMobile(requestBodyJson.getString("mobile"));
                
                LogUtils.info("Sending request with mobile: " + requestBodyJson.getString("mobile"));
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
                
                response = ResponseUtil.getResponse(baseUri, loginrequest, httpsmethod);
                LogUtils.info("Response received - Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                
                if (response.getStatusCode() == 200) {
                    String responseBody = response.getBody().asString();
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        expectedResponse = new JSONObject(expectedResponseBody);
                        //
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        
                        LogUtils.success(logger, "Login API validation successful");
                        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Login API validation successful", ExtentColor.GREEN));
                        ExtentReport.getTest().log(Status.INFO, "Expected Response: " + expectedResponseBody);
                        ExtentReport.getTest().log(Status.INFO, "Actual Response: " + responseBody);
                    } else {
                        LogUtils.failure(logger, "Empty response body received");
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Empty response body received", ExtentColor.RED));
                        throw new customException("Response body is empty");
                    }
                } else {
                    LogUtils.failure(logger, "Invalid status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Invalid status code: " + response.getStatusCode(), ExtentColor.RED));
                    throw new customException("Expected status code 200 but got " + response.getStatusCode());
                }
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Login API test execution failed", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Login verification failed: " + e.getMessage());
        }
    }

    

    /**
     * Test method for negative scenarios
     */
    @Test(dataProvider = "getNegativeInputData", priority = 2)
    private void verifyLoginUsingInvalidInputData(String apiName, String testCaseId, 
        String testType, String description, String httpsMethod, 
        String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try {
            LogUtils.info("=====Starting Login API Negative Test Case: " + testCaseId + "=====");
            ExtentReport.createTest("Login API Negative Test - " + testCaseId);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.contains("login") && testType.contains("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                expectedResponse = new JSONObject(expectedResponseBody);
                
                LogUtils.info("Sending request with test data: " + requestBodyJson.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
                
                loginrequest.setMobile(requestBodyJson.getString("mobile"));
                response = ResponseUtil.getResponse(baseUri, loginrequest, httpsMethod);
                
                LogUtils.info("Response received - Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                
                switch (testCaseId) {
                    case "login_002":
                        LogUtils.info("Validating empty mobile number scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    case "login_003":
                        LogUtils.info("Validating mobile number length scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    case "login_004":
                        LogUtils.info("Validating special characters scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    case "login_005":
                        LogUtils.info("Validating invalid characters scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    case "login_006":
                        LogUtils.info("Validating unregistered number scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                        break;
                        
                    default:
                        LogUtils.info("Validating general negative scenario");
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                }
                
                LogUtils.success(logger, "Negative test case " + testCaseId + " executed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test case executed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.INFO, "Expected Response: " + expectedResponseBody);
                ExtentReport.getTest().log(Status.INFO, "Actual Response: " + response.asPrettyString());
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "Negative test case " + testCaseId + " failed", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test case failed: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Negative test case " + testCaseId + " failed: " + e.getMessage());
        }
    }

   
}