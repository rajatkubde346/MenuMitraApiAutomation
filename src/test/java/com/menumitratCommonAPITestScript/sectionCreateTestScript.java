package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
import com.menumitra.apiRequest.sectionRequest;
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
public class sectionCreateTestScript extends APIBase
{
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedJsonBody; 
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    sectionRequest sectionrequest;
    Logger logger=LogUtils.getLogger(sectionCreateTestScript.class);
    
    


    @DataProvider(name="getSectionCreateURL")
    public Object[][] getSectionCreateURL() throws customException
    {
        try{
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");
            if(readData==null)
            {
                LogUtils.failure(logger, "Error: Getting an error while read Section URL Excel File");
                throw new customException("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectioncreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section URL Excel File", e);
            throw new customException("Error: Getting an error while read Section URL Excel File");
        }
    }

    @DataProvider(name="getSectionCreatePositiveInputData") 
    private Object[][] getSectionCreatePositiveInputData() throws customException {
        try {
            LogUtils.info("Reading positive test scenario data for section create API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis,property.getProperty("CommonAPITestScenario"));
            
            if (testData == null || testData.length == 0)
             {
            	LogUtils.failure(logger, "No Section Create API positive test scenario data found in Excel sheet");
                throw new customException("No Section Create API Positive test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "sectioncreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            // Optional: print to verify
            /*for (Object[] row : obj) {
                System.out.println(Arrays.toString(row));
            }*/
            return obj;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Section Create API positive test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading Section Create API positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name="getverifyOTPInvalidData")
    private Object[][] getverifyOTPInvalidData() throws customException {
        try {
            LogUtils.info("Reading negative test scenario data for verify OTP API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No verify OTP API negative test scenario data found in Excel sheet");
                throw new customException("No verify OTP API negative test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            // Filter for verify OTP API negative test cases
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "sectioncreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            
            return obj;
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read verify OTP API negative test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading verify OTP API negative test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void sectionCreateSetup() throws customException 
    {
        try {
            LogUtils.info("Setting up test environment");
            ExtentReport.createTest("Start Section Create");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set section create URL
            Object[][] sectionCreateData = getSectionCreateURL();
            if (sectionCreateData.length > 0) {
                String endpoint = sectionCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section Create URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section create URL found in test data");
                throw new customException("No section create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            sectionrequest=new sectionRequest();
            LogUtils.info("Section create setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section create setup: " + e.getMessage(), e);
            //ExtentReport.getTest().log(Status.FAIL, "Error during section create setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getSectionCreatePositiveInputData",priority = 1)
    private void verifySectionUsingValidInputData(String apiName, String testCaseId, 
    	    String testType, String description, String httpsMethod, 
    	    String requestBody, String expectedResponseBody, String statusCode ) throws customException
    {
        try
        {
            LogUtils.info("Starting section creation test case: " + testCaseId);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Section Creation Test - " + testCaseId);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if(apiName.contains("sectioncreate") && testType.contains("positive"))
            {
                LogUtils.info("Processing positive test case for section creation");
                requestBodyJson=new JSONObject(requestBody);
                
                LogUtils.info("Setting up request parameters");
                sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
                sectionrequest.setUser_id(String.valueOf(userId));
                sectionrequest.setSection_name(requestBodyJson.getString("section_name"));

                LogUtils.info("Sending POST request to create section");
                response=ResponseUtil.getResponseWithAuth(baseUri,sectionrequest,httpsMethod,accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response received from section create API");
                
                if (response.getStatusCode() == 200) {
                    String responseBody = response.getBody().asString();
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        expectedJsonBody=new JSONObject(expectedResponseBody);
                     
                        validateResponseBody.handleResponseBody(response, expectedJsonBody);
                        LogUtils.success(logger,"Section created successfully");
                        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Section created successfully", ExtentColor.GREEN));
                        ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                    } else {
                        LogUtils.failure(logger, "Section creation failed - Empty response body received");
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Empty response body received", ExtentColor.RED));
                        throw new customException("Response body is empty");
                    }
                } else {
                    LogUtils.failure(logger, "Section creation failed with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Section creation failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                    throw new customException("Expected status code 200 but got " + response.getStatusCode());
                }                
                
            }

        }
        catch(Exception e)
        {
            LogUtils.error("Error during section creation test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during section creation test execution: " + e.getMessage());
        }
    }

    @DataProvider(name="getSectionCreateNegativeData")
    private Object[][] getSectionCreateNegativeData() throws customException {
        try {
            LogUtils.info("Reading negative test scenario data for section create API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No section create API negative test scenario data found in Excel sheet");
                throw new customException("No section create API negative test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            // Filter for section create API negative test cases
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "sectioncreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                LogUtils.failure(logger, "No section create API negative test data found after filtering");
                throw new customException("No section create API negative test data found after filtering");
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            
            return obj;
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read section create API negative test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading section create API negative test scenario data: " + e.getMessage());
        }
    }

    /**
     * Method to count the number of sentences in a string
     * Treats end of sentence markers (., !, ?) followed by space or end of string as sentence boundaries
     * @param text The text to analyze
     * @return The number of sentences
     */
    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        // Pattern to match end of sentences: period, exclamation, or question mark followed by space or end of string
        Pattern pattern = Pattern.compile("[.!?][ $]");
        String[] sentences = pattern.split(text);
        return sentences.length;
    }
    
    @Test(dataProvider = "getSectionCreateNegativeData", priority = 2)
    public void sectionCreateNegativeTest(String apiName, String testCaseId, 
            String testType, String description, String httpsMethod, 
            String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try {
            LogUtils.info("Starting section create negative test case: " + testCaseId);
            ExtentReport.createTest("Section Create Negative Test - " + testCaseId + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);
            
            // Verify this is the right API and test type
            if (!"sectioncreate".equalsIgnoreCase(apiName) || !"negative".equalsIgnoreCase(testType)) {
                String errorMsg = "Invalid test configuration. API Name must be 'sectioncreate' and Test Type must be 'negative'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            // Parse request body
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Set up request parameters
            sectionrequest = new sectionRequest();
            if (requestBodyJson.has("outlet_id")) {
                sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.get("outlet_id")));
            }
            
            if (requestBodyJson.has("user_id")) {
                sectionrequest.setUser_id(String.valueOf(requestBodyJson.get("user_id")));
            } else {
                sectionrequest.setUser_id(String.valueOf(userId));
            }
            
            if (requestBodyJson.has("section_name")) {
                sectionrequest.setSection_name(requestBodyJson.getString("section_name"));
            }
            
            if (requestBodyJson.has("section_id")) {
                sectionrequest.setSection_id(requestBodyJson.getString("section_id"));
            }
            
            // Send request
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            
            // Log response details
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            
            // Parse expected status code
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Start validation - capture both actual and expected values for reporting
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
            }
            else {
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                
                // Validate response body if exists
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    actualResponseBody = new JSONObject(responseBody);
                    
                    // Log actual response body for reporting
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualResponseBody.toString(2));
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedJsonBody = new JSONObject(expectedResponseBody);
                        
                        // Log expected response body for reporting
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedJsonBody.toString(2));
                        
                        // Check response message sentence count if it has a detail field
                        if (actualResponseBody.has("detail")) {
                            String detailMessage = actualResponseBody.getString("detail");
                            int sentenceCount = countSentences(detailMessage);
                            
                            ExtentReport.getTest().log(Status.INFO, "Response message sentence count: " + sentenceCount);
                            
                            if (sentenceCount > 6) {
                                String errorMsg = "Response message contains more than 6 sentences. Found: " + sentenceCount;
                                LogUtils.failure(logger, errorMsg);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            } else {
                                LogUtils.success(logger, "Response message sentence count validation passed: " + sentenceCount);
                                ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount);
                            }
                            
                            // Validate message matches expected
                            if (expectedJsonBody.has("detail")) {
                                String expectedDetail = expectedJsonBody.getString("detail");
                                
                                if (detailMessage.equals(expectedDetail)) {
                                    LogUtils.success(logger, "Response message matches expected message");
                                    ExtentReport.getTest().log(Status.PASS, "Response message matches expected message");
                                } else {
                                    LogUtils.failure(logger, "Response message mismatch - Expected: '" + expectedDetail + "', Actual: '" + detailMessage + "'");
                                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Response message mismatch", ExtentColor.RED));
                                    ExtentReport.getTest().log(Status.FAIL, "Expected: '" + expectedDetail + "'");
                                    ExtentReport.getTest().log(Status.FAIL, "Actual: '" + detailMessage + "'");
                                }
                            }
                        }
                        
                        // Perform full response validation
                        validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    }
                } else {
                    LogUtils.info("Empty response body received");
                    ExtentReport.getTest().log(Status.WARNING, "Empty response body received");
                }
                
                LogUtils.success(logger, "Section create negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Section create negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response for debugging
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            
        } catch (Exception e) {
            String errorMsg = "Error in section create negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }


//@AfterClass
private void tearDown()
{
    try 
    {
        LogUtils.info("===Test environment tear down successfully===");
       
        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
        
        ActionsMethods.logout();
        TokenManagers.clearTokens();
        
    } 
    catch (Exception e) 
    {
        LogUtils.exception(logger, "Error during test environment tear down", e);
        ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
    }
}

}
