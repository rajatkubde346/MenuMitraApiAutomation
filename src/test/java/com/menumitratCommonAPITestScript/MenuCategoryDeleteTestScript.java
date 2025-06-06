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
import com.menumitra.apiRequest.MenuCategoryRequest;
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
public class MenuCategoryDeleteTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuCategoryRequest menuCategoryDeleteRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(MenuCategoryDeleteTestScript.class);
   
    @BeforeClass
    private void menuCategoryDeleteSetUp() throws customException
    {
        try
        {
            LogUtils.info("Menu Category Delete SetUp");
            ExtentReport.createTest("Menu Category Delete SetUp");
            ExtentReport.getTest().log(Status.INFO,"Menu Category Delete SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getMenuCategoryDeleteUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu category delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu category delete URL found in test data");
                throw new customException("No menu category delete URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger,"Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            menuCategoryDeleteRequest = new MenuCategoryRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in menu category delete setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in menu category delete setup: " + e.getMessage());
            throw new customException("Error in menu category delete setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getMenuCategoryDeleteUrl")
    private Object[][] getMenuCategoryDeleteUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading menu category delete URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category delete URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "menucategorydelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No menu category delete URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved menu category delete URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved menu category delete URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getMenuCategoryDeleteUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getMenuCategoryDeleteData") 
    public Object[][] getMenuCategoryDeleteData() throws customException {
        try {
            LogUtils.info("Reading menu category delete test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category delete test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No menu category delete test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "menucategorydelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu category delete test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " menu category delete test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " menu category delete test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuCategoryDeleteData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getMenuCategoryDeleteData")
    private void verifyMenuCategoryDelete(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Menu category delete test execution: " + description);
            ExtentReport.createTest("Menu Category Delete Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Menu category delete test execution: " + description);

            if(apiName.equalsIgnoreCase("menucategorydelete"))
            {
                requestBodyJson = new JSONObject(requestBody);
                expectedJsonBody=new JSONObject(expectedResponseBody);
                menuCategoryDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                menuCategoryDeleteRequest.setMenu_cat_id(requestBodyJson.getString("menu_cat_id"));
                menuCategoryDeleteRequest.setUser_id(String.valueOf(user_id));
                
                LogUtils.info("Constructed menu category delete request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed menu category delete request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, menuCategoryDeleteRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Menu category delete API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu category delete API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Validate response body if expected response is provided
                    actualJsonBody = new JSONObject(response.asString());
                    if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedJsonBody = new JSONObject(expectedResponseBody);
                        
                        //validateResponseBody.handleResponseBody(response, expectedJsonBody);
                        
                      
                    }
                    
                    // Make sure to use Status.PASS for the response to show in the report
                    ExtentReport.getTest().log(Status.PASS, "Full Response:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    
                    // Add a screenshot or additional details that might help visibility
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                }
                else{
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    LogUtils.info("Response Body: " + response.asString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asPrettyString());
                    throw new customException(errorMsg);
                }
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in menu category delete test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in menu category delete test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in menu category delete test: " + e.getMessage());
        }
    }
    @DataProvider(name = "getMenuCategoryListDeleteNegativeData")
    public Object[][] getMenuCategoryListDeleteNegativeData() throws customException {
        try {
            LogUtils.info("Reading menu category list delete negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category list delete negative test scenario data");

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
                        "menucategorydelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu category list delete negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting menu category list delete negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu category list delete negative test data: " + e.getMessage());
            throw new customException("Error in getting menu category list delete negative test data: " + e.getMessage());
        }
    }

    private boolean validateResponseMessageSentences(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        // Split the message into sentences using regex
        String[] sentences = message.split("[.!?]+");
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                sentenceCount++;
            }
        }
        
        return sentenceCount <= 6;
    }

    @Test(dataProvider = "getMenuCategoryListDeleteNegativeData")
    public void menuCategoryListDeleteNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu category list delete negative test case: " + testCaseid);
            ExtentReport.createTest("Menu Category List Delete Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("menucategorydelete") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                expectedJsonBody = new JSONObject(expectedResponseBody);

                // Set request parameters
                menuCategoryDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                menuCategoryDeleteRequest.setMenu_cat_id(requestBodyJson.getString("menu_cat_id"));
                menuCategoryDeleteRequest.setUser_id(String.valueOf(user_id));
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, menuCategoryDeleteRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                int expectedStatusCode = Integer.parseInt(statusCode);

                // Validate status code
                if (response.getStatusCode() != expectedStatusCode) {
                    String errorMsg = "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected Status Code: " + expectedStatusCode);
                    ExtentReport.getTest().log(Status.FAIL, "Actual Status Code: " + response.getStatusCode());
                } else {
                    LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                }

                // Validate response body
                actualJsonBody = new JSONObject(response.asString());
                
                // Validate response message sentences
                if (actualJsonBody.has("message")) {
                    String message = actualJsonBody.getString("message");
                    if (!validateResponseMessageSentences(message)) {
                        String errorMsg = "Response message contains more than 6 sentences";
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        ExtentReport.getTest().log(Status.FAIL, "Message: " + message);
                    } else {
                        LogUtils.success(logger, "Response message sentence count validation passed");
                        ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed");
                    }
                }

                // Validate response body structure
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                }

                // Log full response details
                ExtentReport.getTest().log(Status.INFO, "Full Response Details:");
                ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
                ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedJsonBody.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));

                LogUtils.success(logger, "Menu category list delete negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu category list delete negative test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in menu category list delete negative test: " + e.getMessage();
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