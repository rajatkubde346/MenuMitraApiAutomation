package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
public class MenuListView extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuRequest menuListViewRequest;
    private URL url;
    Logger logger = LogUtils.getLogger(MenuListView.class);
    private JSONObject expectedResponseBody;
    private JSONObject actualJsonBody;
   
    @BeforeClass
    private void menuListViewSetUp() throws customException
    {
        try
        {
            LogUtils.info("Menu List View SetUp");
            ExtentReport.createTest("Menu List View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Menu List View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getMenuListViewUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
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
            
            menuListViewRequest = new MenuRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in menu list view setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in menu list view setup: " + e.getMessage());
            throw new customException("Error in menu list view setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getMenuListViewUrl")
    private Object[][] getMenuListViewUrl() throws customException
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
                    .filter(row -> "menulistview".equalsIgnoreCase(row[0].toString()))
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
   
    @DataProvider(name = "getMenuListViewData") 
    public Object[][] getMenuListViewData() throws customException {
        try {
            LogUtils.info("Reading menu list view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu list view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No menu list view test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "menulistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu list view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " menu list view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " menu list view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuListViewData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getMenuListViewData")
    private void verifyMenuListView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Menu list view test execution: " + description);
            ExtentReport.createTest("Menu list view test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Menu list view test execution: " + description);

            if(apiName.equalsIgnoreCase("menulistview"))
            {
                requestBodyJson = new JSONObject(requestBody);

                // Only set outlet_id as required
                menuListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                LogUtils.info("Constructed menu list view request"); 
                ExtentReport.getTest().log(Status.INFO, "Constructed menu list view request");

                response = ResponseUtil.getResponseWithAuth(baseURI, menuListViewRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Menu list view API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu list view API executed successfully", ExtentColor.GREEN));
                    // Only display response body without validation
                    ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                }
                else{
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asPrettyString());
                    throw new customException(errorMsg);
                }
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in menu list view test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in menu list view test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in menu list view test: " + e.getMessage());
        }
    }

   
    @DataProvider(name = "getMenuListViewNegativeData")
    public Object[][] getMenuListViewNegativeData() throws customException {
        try {
            LogUtils.info("Reading menu list view negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu list view negative test scenario data");
            
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
                        "menulistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu list view negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting menu list view negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu list view negative test data: " + e.getMessage());
            throw new customException("Error in getting menu list view negative test data: " + e.getMessage());
        }
    }
    
    /**
     * Checks if a text contains more than the specified number of sentences
     * @param text The text to check
     * @param maxSentences Maximum number of allowed sentences
     * @return true if text has more sentences than allowed, false otherwise
     */
    private boolean hasMoreThanMaxSentences(String text, int maxSentences) {
        // Define sentence-ending patterns (., !, ?)
        String sentenceEndPattern = "[.!?]\\s+|[.!?]$";
        Pattern pattern = Pattern.compile(sentenceEndPattern);
        String[] sentences = pattern.split(text);
        
        // Count non-empty sentences
        int sentenceCount = 0;
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                sentenceCount++;
            }
        }
        
        return sentenceCount > maxSentences;
    }
    
    @Test(dataProvider = "getMenuListViewNegativeData")
    public void menuListViewNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponse, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu list view negative test case: " + testCaseid);
            ExtentReport.createTest("Menu List View Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("menulistview") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Set payload for menu list view request based on provided request body
                if (requestBodyJson.has("outlet_id")) {
                    menuListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }
                
               
                
                // Add any other potential parameters for MenuRequest
                
                response = ResponseUtil.getResponseWithAuth(baseURI, menuListViewRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
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
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    	expectedResponseBody = new JSONObject(expectedResponse);
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody.toString(2));
                        
                        // Validate response message
                        if (expectedResponseBody.has("detail") && actualJsonBody.has("detail")) {
                            String expectedDetail = expectedResponseBody.getString("detail");
                            String actualDetail = expectedResponseBody.getString("detail");
                            
                            // Check if message has more than 6 sentences
                            if (hasMoreThanMaxSentences(actualDetail, 6)) {
                                String errorMsg = "Error message contains more than 6 sentences: " + actualDetail;
                                LogUtils.failure(logger, errorMsg);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Sentence count validation failed", ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                            } else {
                                LogUtils.info("Sentence count validation passed (≤ 6 sentences)");
                                ExtentReport.getTest().log(Status.PASS, "Sentence count validation passed (≤ 6 sentences)");
                            }
                            
                            if (expectedDetail.equals(actualDetail)) {
                                LogUtils.info("Error message validation passed: " + actualDetail);
                                ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                            } else {
                                LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail);
                                ExtentReport.getTest().log(Status.FAIL, "Actual: " + actualDetail);
                            }
                        }
                        
                        // Complete response validation
                        validateResponseBody.handleResponseBody(response, expectedResponseBody);
                    }
                    
                    LogUtils.success(logger, "Menu list view negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu list view negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            } else {
                String errorMsg = "Invalid API name or test type. Expected 'menulistview' and 'negative', got: " + apiName + ", " + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            }
        } catch (Exception e) {
            String errorMsg = "Error in menu list view negative test: " + e.getMessage();
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