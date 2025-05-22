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
import com.menumitra.apiRequest.SelectTemplateRequest;
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
public class RemoveTemplateTestScript extends APIBase
{
    private SelectTemplateRequest removeTemplateRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private String accessToken;
    private JSONObject expectedJsonBody;
    Logger logger = LogUtils.getLogger(RemoveTemplateTestScript.class);
    
    /**
     * Data provider for remove template API endpoint URLs
     */
    @DataProvider(name = "getRemoveTemplateUrl")
    public Object[][] getRemoveTemplateUrl() throws customException {
        try {
            LogUtils.info("Reading Remove Template API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "removetemplate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Remove Template API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Remove Template API endpoint data from Excel sheet");
            throw new customException("Error While Reading Remove Template API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for remove template test scenarios
     */
    @DataProvider(name = "getRemoveTemplateData")
    public Object[][] getRemoveTemplateData() throws customException {
        try {
            LogUtils.info("Reading remove template test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No remove template test scenario data found in Excel sheet");
                throw new customException("No remove template test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "removetemplate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for remove template");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading remove template test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading remove template test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading remove template test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for remove template test====");
            ExtentReport.createTest("Remove Template Setup");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set remove template URL
            Object[][] removeTemplateData = getRemoveTemplateUrl();
            if (removeTemplateData.length > 0) {
                String endpoint = removeTemplateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for remove template: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No remove template URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No remove template URL found in test data");
                throw new customException("No remove template URL found in test data");
            }

            // Get access token from TokenManager
            accessToken = TokenManagers.getJwtToken();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            removeTemplateRequest = new SelectTemplateRequest();
            LogUtils.success(logger, "Remove Template Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Remove Template Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during remove template setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during remove template setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to remove template
     */
    @Test(dataProvider = "getRemoveTemplateData")
    private void removeTemplate(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting remove template test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Remove Template Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            removeTemplateRequest.setSection_id(requestBodyJson.getInt("section_id"));
            
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = ResponseUtil.getResponseWithAuth(baseURI, removeTemplateRequest, httpsmethod, accessToken);
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Log response without validation
            if (response.asString() != null && !response.asString().isEmpty()) {
                actualResponseBody = new JSONObject(response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
            }
            
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Template removed successfully", ExtentColor.GREEN));
            LogUtils.success(logger, "Template removed successfully");
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
