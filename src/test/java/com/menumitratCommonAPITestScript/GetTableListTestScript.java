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
import com.menumitra.apiRequest.TableRequest;
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
public class GetTableListTestScript extends APIBase
{
    private TableRequest tableListRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody; 
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private Logger logger = LogUtils.getLogger(GetTableListTestScript.class);


    @DataProvider(name = "getTableListUrl")
    private Object[][] getTableListUrl() throws customException {
        try {
            LogUtils.info("Starting to read Table List API endpoint data from Excel");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Table List API endpoint data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.ERROR, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "gettablelistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            LogUtils.success(logger, "Successfully retrieved Login API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully loaded Login API configuration");
            return filteredData;
            
        } catch (Exception e) {
            String errorMsg = "Error while reading Table List API endpoint data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, "Excel Path: " + excelSheetPathForGetApis);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }


    @DataProvider(name = "getTableListData")
    public static Object[][] getTableListData() throws customException {
        try {
            LogUtils.info("Starting to read table list test scenario data from Excel");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table list test scenario data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.ERROR, errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                        "gettablelistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully filtered " + obj.length + " test scenarios for table list view");
            ExtentReport.getTest().log(Status.INFO, "Found " + obj.length + " test scenarios for table list view");
            return obj;

        } catch (Exception e) {
            String errorMsg = "Error while reading table list test scenario data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, "Excel Path: " + excelSheetPathForGetApis);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void TableListViewSetUp() throws customException {
        try {
            LogUtils.info("Starting setup for table list view test");
            ExtentReport.createTest("Table List View Setup");
            
            LogUtils.info("Attempting login");
            ActionsMethods.login();
            LogUtils.info("Login successful, verifying OTP");
            ActionsMethods.verifyOTP();
            
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseURI);
            
            Object[][] tableListData = getTableListUrl();
            LogUtils.info("Successfully retrieved Table List API endpoint data from Excel");
            ExtentReport.getTest().log(Status.INFO, "Successfully read endpoint data from Excel sheet: " + excelSheetPathForGetApis);
            if (tableListData.length > 0) 
            {
                String endpoint = tableListData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Endpoint URL constructed: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "API Endpoint: " + baseURI);
            }
            
            accessToken = TokenManagers.getJwtToken();
            LogUtils.info("JWT Token retrieved successfully");
            tableListRequest = new TableRequest();
            
            LogUtils.info("Table List View setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error during Table List View setup: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, "Setup failed");
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableListData")
    private void verifyTableListView(String apiName,String testCaseid, String testType, String description,
    String httpsmethod,String requestBodyPayload,String expectedResponseBody,String statusCode) throws customException
    {
        try
        {
            LogUtils.info("Starting table list view test - TestCase ID: " + testCaseid);
            ExtentReport.createTest("Table List View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsmethod);

            if(apiName.equalsIgnoreCase("gettablelistview"))
            {
                requestBodyJson = new JSONObject(requestBodyPayload);
                tableListRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                tableListRequest.setSection_id(requestBodyJson.getString("section_id"));

                LogUtils.info("Request Body: " + requestBodyJson.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
                ExtentReport.getTest().log(Status.INFO, "API URL: " + baseURI);

                response = ResponseUtil.getResponseWithAuth(baseURI,tableListRequest,httpsmethod,accessToken);
                LogUtils.info("Response received - Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                
                if(response.getStatusCode()==200) 
                {   
                    LogUtils.info("Response Body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.PASS, "Response Body: " + response.asPrettyString());
                    LogUtils.success(logger, "Table list view retrieved successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table list view retrieved successfully", ExtentColor.GREEN));
                  
                } else {
                    String errorMsg = "Failed to retrieve table list view";
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected Status Code: 200");
                    ExtentReport.getTest().log(Status.FAIL, "Actual Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asString());
                    ExtentReport.getTest().log(Status.FAIL, "API URL: " + baseURI);
                    throw new customException(errorMsg);
                }
            }
            
        }
        catch(Exception e)
        {
            String errorMsg = "Error in table list view test execution";
            LogUtils.error(errorMsg + ": " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Test execution failed");
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "API URL: " + baseURI);
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asString());
            }
            throw new customException(errorMsg + ": " + e.getMessage());
        }
    }
    
    
    @DataProvider(name = "getTableListViewNegativeData")
    public Object[][] getTableListViewNegativeData() throws customException {
        try {
            LogUtils.info("Reading table list view negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading table list view negative test scenario data");
            
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
                        "gettablelistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid table list view negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting table list view negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting table list view negative test data: " + e.getMessage());
            throw new customException("Error in getting table list view negative test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getTableListViewNegativeData")
    public void tableListViewNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting Get table list view negative test case: " + testCaseid);
            ExtentReport.createTest("Get Table List View Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("gettablelistview") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Set payload for table list view request
                tableListRequest.setOutlet_id(Integer.parseInt(requestBodyJson.getString("outlet_id")));
                tableListRequest.setSection_id(requestBodyJson.getString("section_id"));
                
                response = ResponseUtil.getResponseWithAuth(baseURI, tableListRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + statusCode);
                ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
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
                    actualResponseBody = new JSONObject(response.asString());
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    	expectedResponse = new JSONObject(expectedResponseBody);
                        
                        // Validate response message
                        if (expectedResponse.has("detail") && actualResponseBody.has("detail")) {
                            String expectedDetail = expectedResponse.getString("detail");
                            String actualDetail = actualResponseBody.getString("detail");
                            
                            // Check for sentence count
                            int sentenceCount = countSentences(actualDetail);
                            LogUtils.info("Response message sentence count: " + sentenceCount);
                            ExtentReport.getTest().log(Status.INFO, "Response message sentence count: " + sentenceCount);
                            
                            if (sentenceCount > 5) {
                                LogUtils.failure(logger, "Response message has more than 5 sentences: " + sentenceCount);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Response message has more than 5 sentences: " + sentenceCount, ExtentColor.RED));
                            } else {
                                LogUtils.info("Response message sentence count validation passed: " + sentenceCount);
                                ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount);
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
                        validateResponseBody.handleResponseBody(response, expectedResponse);
                    }
                    
                    LogUtils.success(logger, "Table list view negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table list view negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            }
        } catch (Exception e) {
            String errorMsg = "Error in table list view negative test: " + e.getMessage();
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
     * Counts the number of sentences in a text string.
     * Sentences are expected to end with a period, question mark, or exclamation point.
     * 
     * @param text The string to count sentences in
     * @return The number of sentences
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Regex pattern for sentence endings (., !, ?)
        Pattern pattern = Pattern.compile("[.!?]");
        String[] sentences = pattern.split(text);
        
        // Filter out empty strings that might occur with multiple punctuation marks
        int count = 0;
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                count++;
            }
        }
        
        return count;
    }
    
    
    
    
    
    
    
    
    
}
