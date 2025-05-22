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
import com.menumitra.apiRequest.TableRequest;
import com.menumitra.apiRequest.TableUpdateRequest;
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
public class GetAvailableTablesTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private TableUpdateRequest availableTablesRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(GetAvailableTablesTestScript.class);
   
    @BeforeClass
    private void getAvailableTablesSetUp() throws customException
    {
        try
        {
            LogUtils.info("Get Available Tables SetUp");
            ExtentReport.createTest("Get Available Tables SetUp");
            ExtentReport.getTest().log(Status.INFO,"Get Available Tables SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getAvailableTablesUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No available tables URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No available tables URL found in test data");
                throw new customException("No available tables URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            availableTablesRequest = new TableUpdateRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in get available tables setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in get available tables setup: " + e.getMessage());
            throw new customException("Error in get available tables setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getAvailableTablesUrl")
    private Object[][] getAvailableTablesUrl() throws customException {
        try {
            LogUtils.info("Reading Get Available Tables API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Get Available Tables API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Get Available Tables API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "getavailabletables".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No available tables URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting available tables URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting available tables URL: " + e.getMessage());
            throw new customException("Error in getting available tables URL: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getAvailableTablesData")
    public Object[][] getAvailableTablesData() throws customException {
        try {
            LogUtils.info("Reading positive available tables test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive available tables test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "No available tables test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "getavailabletables".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }
            
            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive available tables test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive available tables test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive available tables test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive available tables test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getAvailableTablesData")
    public void getAvailableTablesTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting get available tables test case: " + testCaseid);
            ExtentReport.createTest("Get Available Tables Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("getavailabletables")) {
                requestBodyJson = new JSONObject(requestBody);
                availableTablesRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                availableTablesRequest.setUser_id(String.valueOf(user_id));
                availableTablesRequest.setSection_id(requestBodyJson.getString("section_id"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, availableTablesRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Validate status code
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                
                // Only show response without validation
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("Available tables response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Available tables response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                
                LogUtils.success(logger, "Get available tables test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Get available tables test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in get available tables test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    /**
     * Validate if a message contains more than the maximum allowed number of sentences
     * @param message The message to validate
     * @param maxSentences Maximum allowed sentences
     * @return Error message if validation fails, null if validation passes
     */
    private String validateSentenceCount(String message, int maxSentences) {
        if (message == null || message.trim().isEmpty()) {
            return null; // Empty message, no sentences
        }
        
        String[] sentences = message.split("[.!?]+");
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                sentenceCount++;
            }
        }
        
        if (sentenceCount > maxSentences) {
            return "Response message exceeds maximum allowed sentences - Found: " + sentenceCount + 
                   ", Maximum allowed: " + maxSentences;
        }
        
        return null; // Validation passed
    }
}
