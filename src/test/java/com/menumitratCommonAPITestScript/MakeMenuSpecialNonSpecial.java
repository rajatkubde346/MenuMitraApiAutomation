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
public class MakeMenuSpecialNonSpecial extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuRequest menuRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    private int userId;
    Logger logger = LogUtils.getLogger(MakeMenuSpecialNonSpecial.class);
   
    @BeforeClass
    private void makeMenuSpecialNonSpecialSetUp() throws customException
    {
        try
        {
            LogUtils.info("Make Menu Special Non-Special SetUp");
            ExtentReport.createTest("Make Menu Special Non-Special SetUp");
            ExtentReport.getTest().log(Status.INFO,"Make Menu Special Non-Special SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getMakeMenuSpecialNonSpecialUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No make menu special/non-special URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No make menu special/non-special URL found in test data");
                throw new customException("No make menu special/non-special URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId=TokenManagers.getUserId();
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
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in make menu special/non-special setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in make menu special/non-special setup: " + e.getMessage());
            throw new customException("Error in make menu special/non-special setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getMakeMenuSpecialNonSpecialUrl")
    private Object[][] getMakeMenuSpecialNonSpecialUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading make menu special/non-special URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading make menu special/non-special URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "makemenuspecialnonspecial".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No make menu special/non-special URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved make menu special/non-special URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved make menu special/non-special URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getMakeMenuSpecialNonSpecialUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getMakeMenuSpecialNonSpecialData")
    public Object[][] getMakeMenuSpecialNonSpecialData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory category create test scenario data found in Excel sheet");
                ExtentReport.getTest().log(Status.ERROR, "No inventory category create test scenario data found in Excel sheet");
                throw new customException("No inventory category create test scenario data found in Excel sheet");
            }
            List<Object[]> filterData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && "makemenuspecialnonspecial".equalsIgnoreCase(Objects.toString(row[0], ""))
                        && "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filterData.add(row);
                }
            }
            Object[][] obj = new Object[filterData.size()][];
            for (int i = 0; i < filterData.size(); i++) {
                obj[i] = filterData.get(i);
            }
            LogUtils.info("Successfully retrieved " + obj.length + " negative test scenarios for inventory category create");
            ExtentReport.getTest().log(Status.INFO, "Successfully retrieved " + obj.length + " negative test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory category create negative test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory category create negative test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory category create negative test scenario data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getMakeMenuSpecialNonSpecialData")
    private void verifyMakeMenuSpecialNonSpecial(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Make menu special/non-special test execution: " + description);
            ExtentReport.createTest("Make Menu Special/Non-Special Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Make menu special/non-special test execution: " + description);

            if(apiName.equalsIgnoreCase("makemenuspecialnonspecial"))
            {
                requestBodyJson = new JSONObject(requestBody);

                menuRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                menuRequest.setMenu_id(requestBodyJson.getString("menu_id"));
                menuRequest.setUser_id(String.valueOf(userId));
                
                LogUtils.info("Constructed make menu special/non-special request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed make menu special/non-special request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, menuRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Make menu special/non-special API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Make menu special/non-special API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Validate response body if expected response is provided
                    actualJsonBody = new JSONObject(response.asString());
                    if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedJsonBody = new JSONObject(expectedResponseBody);
                        
                        // Log response information to report without validation
                        LogUtils.info("Response received successfully");
                        LogUtils.info("Response Body: " + actualJsonBody.toString());
                        ExtentReport.getTest().log(Status.PASS, "Response received successfully");
                        ExtentReport.getTest().log(Status.PASS, "Expected response structure available in test data");
                        ExtentReport.getTest().log(Status.PASS, "Response Body: " + actualJsonBody.toString());
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
            LogUtils.exception(logger, "Error in make menu special/non-special test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in make menu special/non-special test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in make menu special/non-special test: " + e.getMessage());
        }
    }
    
    
    
    @DataProvider(name = "getMakeMenuSpecialNonSpecialNegativeData")
    public Object[][] getMakeMenuSpecialNonSpecialNegativeData() throws customException {
        try {
            LogUtils.info("Reading Make Menu Special/Non-Special negative test scenario data");
            //ExtentReport.getTest().log(Status.INFO, "Reading Make Menu Special/Non-Special negative test scenario data");
            
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
                        "makemenuspecialnonspecial".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid Make Menu Special/Non-Special negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting Make Menu Special/Non-Special negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Make Menu Special/Non-Special negative test data: " + e.getMessage());
            throw new customException("Error in getting Make Menu Special/Non-Special negative test data: " + e.getMessage());
        }
    }
    
    /**
     * Counts the number of sentences in a text string
     * @param text The text to count sentences in
     * @return The number of sentences
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Basic sentence counting - splitting by common sentence terminators
        // This is a simple implementation and may need to be refined for complex texts
        String[] sentences = text.split("[.!?]+");
        
        // Filter out empty strings that might result from consecutive terminators
        int count = 0;
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                count++;
            }
        }
        
        return count;
    }
    
    @Test(dataProvider = "getMakeMenuSpecialNonSpecialNegativeData")
    public void makeMenuSpecialNonSpecialNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting Make Menu Special/Non-Special negative test case: " + testCaseid);
            ExtentReport.createTest("Make Menu Special/Non-Special Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Validate API name and test type
            if (!apiName.equalsIgnoreCase("makemenuspecialnonspecial")) {
                String errorMsg = "Invalid API name: " + apiName + ". Expected: makemenuspecialnonspecial";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid test type: " + testType + ". Expected: negative";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Set request parameters from JSON
            menuRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            menuRequest.setMenu_id(requestBodyJson.getString("menu_id"));
            menuRequest.setUser_id(String.valueOf(userId));
            
            
            response = ResponseUtil.getResponseWithAuth(baseURI, menuRequest, httpsmethod, accessToken);
            
            int actualStatusCode = response.getStatusCode();
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            LogUtils.info("Response Status Code: " + actualStatusCode);
            LogUtils.info("Expected Status Code: " + expectedStatusCode);
            LogUtils.info("Response Body: " + response.asString());
            
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Check for server errors
            if (actualStatusCode == 500 || actualStatusCode == 502) {
                LogUtils.failure(logger, "Server error detected with status code: " + actualStatusCode);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error detected: " + actualStatusCode, ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
            }
            // Validate status code
            else if (actualStatusCode != expectedStatusCode) {
                LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
            }
            else {
                LogUtils.success(logger, "Status code validation passed: " + actualStatusCode);
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + actualStatusCode);
                
                // Validate response body
                actualJsonBody = new JSONObject(response.asString());
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                	expectedJsonBody = new JSONObject(expectedResponseBody);
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedJsonBody.toString(2));
                    
                    // Validate response message sentence count
                    if (actualJsonBody.has("detail")) {
                        String detailMessage = actualJsonBody.getString("detail");
                        int sentenceCount = countSentences(detailMessage);
                        
                        if (sentenceCount > 6) {
                            String errorMsg = "Response message contains more than 6 sentences: " + sentenceCount;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Message: " + detailMessage);
                        } else {
                            LogUtils.success(logger, "Response message sentence count validation passed: " + sentenceCount);
                            ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount);
                        }
                        
                        // Validate response message content
                        if (expectedJsonBody.has("detail")) {
                            String expectedDetail = expectedJsonBody.getString("detail");
                            
                            if (expectedDetail.equals(detailMessage)) {
                                LogUtils.info("Error message validation passed: " + detailMessage);
                                ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + detailMessage);
                            } else {
                                LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + detailMessage);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail + ", Actual: " + detailMessage);
                            }
                        }
                    }
                    
                    // Complete response validation
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                }
                
                LogUtils.success(logger, "Make Menu Special/Non-Special negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Make Menu Special/Non-Special negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            
        } catch (Exception e) {
            String errorMsg = "Error in Make Menu Special/Non-Special negative test: " + e.getMessage();
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