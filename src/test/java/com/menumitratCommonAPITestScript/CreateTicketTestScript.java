package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
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
public class CreateTicketTestScript extends APIBase 
{
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private RequestSpecification request;
    private JSONObject expectedResponse;
    Logger logger = Logger.getLogger(CreateTicketTestScript.class);
    
    /**
     * Data provider for create ticket API endpoint URLs
     */
    @DataProvider(name="getCreateTicketUrl")
    public Object[][] getCreateTicketUrl() throws customException {
        try {
            LogUtils.info("Reading Create Ticket API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "createticket".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Create Ticket API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Create Ticket API endpoint data from Excel sheet");
            throw new customException("Error While Reading Create Ticket API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for create ticket test scenarios
     */
    @DataProvider(name="getCreateTicketData")
    public Object[][] getCreateTicketData() throws customException {
        try {
            LogUtils.info("Reading create ticket test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No create ticket test scenario data found in Excel sheet");
                throw new customException("No create ticket test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "createticket".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.error("Error while reading create ticket test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading create ticket test scenario data: " + e.getMessage());
            throw new customException("Error while reading create ticket test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Create Ticket SetUp");
            ExtentReport.createTest("Create Ticket Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            Object[][] ticketUrl = getCreateTicketUrl();
            if (ticketUrl.length > 0) 
            {
                String endpoint = ticketUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri + "" + url.getPath();
                if(url.getQuery() != null) {
                    baseUri += "?" + url.getQuery();
                }
                LogUtils.info("Create Ticket URL set to: " + baseUri);
            } else {
                LogUtils.error("No create ticket URL found in test data");
                throw new customException("No create ticket URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
            LogUtils.info("Create Ticket Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during create ticket setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during create ticket setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create a ticket
     */
    @Test(dataProvider="getCreateTicketData")
    private void createTicketUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting create ticket test: " + description);
            ExtentReport.createTest("Create Ticket Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting create ticket test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("createticket") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing create ticket request");
                requestBodyJson = new JSONObject(requestBody.replace("\\", "\\\\"));
                
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "multipart/form-data");

                // Set multipart form data
                request.multiPart("user_id", userId);
                request.multiPart("outlet_id", requestBodyJson.getString("outlet_id"));
                request.multiPart("title", requestBodyJson.getString("title"));
                request.multiPart("description", requestBodyJson.getString("description"));
                request.multiPart("status", requestBodyJson.getString("status"));
                
                // Handle attachments if present
                if (requestBodyJson.has("attachment_1") && !requestBodyJson.getString("attachment_1").isEmpty()) {
                    File attachment = new File(requestBodyJson.getString("attachment_1"));
                    if(attachment.exists()) {
                        request.multiPart("attachment_1", attachment);
                    }
                }
                if (requestBodyJson.has("attachment_2") && !requestBodyJson.getString("attachment_2").isEmpty()) {
                    File attachment = new File(requestBodyJson.getString("attachment_2"));
                    if(attachment.exists()) {
                        request.multiPart("attachment_2", attachment);
                    }
                }
                LogUtils.info("Constructing request body");
                ExtentReport.getTest().log(Status.INFO, "Constructing request body");
                LogUtils.info("Sending POST request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending POST request to create ticket");
                response = request.when().post(baseUri).then().extract().response();

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                
                if (response.getStatusCode() == 200) {
                    LogUtils.success(logger, "Ticket created successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Ticket created successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                } 
                else {
                    LogUtils.failure(logger, "Ticket creation failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Ticket creation failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during create ticket test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during create ticket test execution: " + e.getMessage());
        }
    }
    
    
    @DataProvider(name = "getCreateTicketNegativeData")
    public Object[][] getCreateTicketNegativeData() throws customException {
        try {
            LogUtils.info("Reading create ticket negative test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "createticket".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            
            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.error("Error reading create ticket negative test data: " + e.getMessage());
            throw new customException("Error reading create ticket negative test data: " + e.getMessage());
        }
    }
    
    
    @Test(dataProvider = "getCreateTicketNegativeData")
    public void createTicketNegative(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting create ticket negative test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Create Ticket Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Validate API name and test type
            if (!"createticket".equalsIgnoreCase(apiName)) {
                String errorMsg = "Invalid API name for create ticket test: " + apiName;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!"negative".equalsIgnoreCase(testType)) {
                String errorMsg = "Invalid test type for create ticket negative test: " + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            // Request preparation
            requestBodyJson = new JSONObject(requestBodyPayload.replace("\\", "\\\\"));
            
            request = RestAssured.given();
            request.header("Authorization", "Bearer " + accessToken);
            request.contentType("multipart/form-data");
            
            // Set form data
            request.multiPart("user_id", userId);
            request.multiPart("outlet_id", requestBodyJson.getString("outlet_id"));
            request.multiPart("title", requestBodyJson.getString("title"));
            request.multiPart("description", requestBodyJson.getString("description"));
            request.multiPart("status", requestBodyJson.getString("status"));
            
            // Handle attachments if present
            if (requestBodyJson.has("attachment_1") && !requestBodyJson.getString("attachment_1").isEmpty()) {
                File attachment = new File(requestBodyJson.getString("attachment_1"));
                if (attachment.exists()) {
                    request.multiPart("attachment_1", attachment);
                }
            }
            
            // API call
            LogUtils.info("Sending POST request to endpoint: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Sending POST request to endpoint: " + baseUri);
            
            response = request.when().post(baseUri).then().extract().response();
            
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());
            
            // Status code validation
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + statusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
            
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                actualResponseBody = new JSONObject(response.asString());
                expectedResponse = new JSONObject(expectedResponseBody);
                
                // Validate response message sentence count
                if (actualResponseBody.has("message")) {
                    String message = actualResponseBody.getString("message");
                    String[] sentences = message.split("[.!?]+");
                    int sentenceCount = 0;
                    
                    for (String sentence : sentences) {
                        if (!sentence.trim().isEmpty()) {
                            sentenceCount++;
                        }
                    }
                    
                    ExtentReport.getTest().log(Status.INFO, "Response message contains " + sentenceCount + " sentences");
                    LogUtils.info("Response message contains " + sentenceCount + " sentences");
                    
                    if (sentenceCount > 6) {
                        String errorMsg = "Response message contains more than 6 sentences (" + sentenceCount + "), which exceeds the limit";
                        ExtentReport.getTest().log(Status.FAIL, errorMsg);
                        LogUtils.failure(logger, errorMsg);
                        throw new customException(errorMsg);
                    } else {
                        ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount + " sentences");
                        LogUtils.success(logger, "Response message sentence count validation passed: " + sentenceCount + " sentences");
                    }
                }
                
                // Perform detailed response validation
                ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                LogUtils.info("Performing detailed response validation");
                validateResponseBody.handleResponseBody(response, expectedResponse);
                
                ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                LogUtils.success(logger, "Response body validation passed successfully");
                
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

    /**
     * Cleanup method to perform post-test cleanup
     */
    @AfterClass
    private void tearDown() throws customException {
        // No validation needed as per requirements
    }
}