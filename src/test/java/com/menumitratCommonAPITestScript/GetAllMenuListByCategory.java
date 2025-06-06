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

import com.menumitra.apiRequest.MenuRequest;
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
public class GetAllMenuListByCategory extends APIBase {
    private MenuRequest menuRequest;
    private Response response;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private JSONObject expectedResponseJson;
    private JSONObject actualJsonBody;
    private Logger logger = LogUtils.getLogger(GetAllMenuListByCategory.class);

    
    @DataProvider(name="getallmenulistbycategoryurl")
    private Object[][] getallmenulistbycategoryurl() throws customException
    {
        try
        {
            LogUtils.info("Reading menu list view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading menu list view URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "getallmenulistbycategory".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No menu list view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved menu list view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved menu list view URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getMenuListViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   

    /**
     * Data provider for getAllMenuListByCategory test scenarios
     */
    @DataProvider(name = "getAllMenuListByCategoryData")
    public static Object[][] getAllMenuListByCategoryData() throws customException {
        try {
            LogUtils.info("Reading getAllMenuListByCategory test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No getAllMenuListByCategory test scenario data found in Excel sheet");
                throw new customException("No getAllMenuListByCategory test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "getallmenulistbycategory".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for getAllMenuListByCategory");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading getAllMenuListByCategory test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading getAllMenuListByCategory test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading getAllMenuListByCategory test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
    	try
        {
            LogUtils.info("Menu List View SetUp");
            ExtentReport.createTest("Menu List View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Menu List View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getallmenulistbycategoryurl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                System.out.println(url);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                System.out.println(baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu list view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu list view URL found in test data");
                throw new customException("No menu list view URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger,"Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            menuRequest = new MenuRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        
        } catch (Exception e) {
            LogUtils.failure(logger, "Error during GetAllMenuListByCategory setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during GetAllMenuListByCategory setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to get all menu list by category
     */
    @Test(dataProvider = "getAllMenuListByCategoryData")
    private void getAllMenuListByCategory(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting GetAllMenuListByCategory test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("GetAllMenuListByCategory Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Only outlet_id is required
            menuRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, menuRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation of status code only
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                // Add response to report without validating body
              
                
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("GetAllMenuListByCategory executed successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "GetAllMenuListByCategory executed successfully");
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
    
    
    @DataProvider(name = "getGetAllMenuListByCategoryNegativeData")
    public Object[][] getGetAllMenuListByCategoryNegativeData() throws customException {
        try {
            LogUtils.info("Reading getAllMenuListByCategory negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading getAllMenuListByCategory negative test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "getallmenulistbycategory".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid getAllMenuListByCategory negative test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            return result;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting getAllMenuListByCategory negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting getAllMenuListByCategory negative test data: " + e.getMessage());
            throw new customException("Error in getting getAllMenuListByCategory negative test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getGetAllMenuListByCategoryNegativeData")
    public void getAllMenuListByCategoryNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting getAllMenuListByCategory negative test case: " + testCaseid);
            ExtentReport.createTest("GetAllMenuListByCategory Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("getallmenulistbycategory") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Set payload for getAllMenuListByCategory request
                if (requestBodyJson.has("outlet_id")) {
                    menuRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }
               
                
                response = ResponseUtil.getResponseWithAuth(baseURI, menuRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Compare actual and expected status codes
                ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
                ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
                
                // Check for server errors
                if (response.getStatusCode() == 500 || response.getStatusCode() == 502) {
                    LogUtils.failure(logger, "Server error detected with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error detected: " + response.getStatusCode(), ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
                // Validate status code
                else if (response.getStatusCode() != expectedStatusCode) {
                    LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                }
                else {
                    LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                    
                    // Validate response body
                    actualJsonBody = new JSONObject(response.asString());
                    
                    // Log actual response body
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(4));
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedResponseJson = new JSONObject(expectedResponseBody);
                        
                        // Log expected response body
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseJson.toString(4));
                        
                        // Validate response message
                        if (expectedResponseJson.has("detail") && actualJsonBody.has("detail")) {
                            String expectedDetail = expectedResponseJson.getString("detail");
                            String actualDetail = actualJsonBody.getString("detail");
                            
                            // Validate that response message doesn't have more than 6 sentences
                            String[] sentences = actualDetail.split("[.!?]+");
                            int sentenceCount = sentences.length;
                            
                            if (sentenceCount > 6) {
                                String errorMsg = "Error message contains more than 6 sentences. Found: " + sentenceCount + " sentences";
                                LogUtils.failure(logger, errorMsg);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Response message: " + actualDetail);
                            } else {
                                LogUtils.info("Response message sentence validation passed. Found: " + sentenceCount + " sentences");
                                ExtentReport.getTest().log(Status.PASS, "Response message sentence validation passed. Found: " + sentenceCount + " sentences");
                            }
                            
                            if (expectedDetail.equals(actualDetail)) {
                                LogUtils.info("Error message validation passed: " + actualDetail);
                                ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                            } else {
                                LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail + ", Actual: " + actualDetail);
                            }
                        }
                        
                        // Complete response validation
                        validateResponseBody.handleResponseBody(response, expectedResponseJson);
                    }
                    
                    LogUtils.success(logger, "GetAllMenuListByCategory negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("GetAllMenuListByCategory negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            } else {
                String errorMsg = "Invalid API name or test type: API=" + apiName + ", TestType=" + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error in getAllMenuListByCategory negative test: " + e.getMessage();
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