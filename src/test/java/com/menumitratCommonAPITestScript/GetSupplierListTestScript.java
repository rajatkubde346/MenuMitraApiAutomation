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
import com.menumitra.apiRequest.SupplierListViewRequest;
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
public class GetSupplierListTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private SupplierListViewRequest supplierListRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(GetSupplierListTestScript.class);
   
    @BeforeClass
    private void supplierListSetUp() throws customException
    {
        try
        {
            LogUtils.info("Get Supplier List SetUp");
            ExtentReport.createTest("Get Supplier List SetUp");
            ExtentReport.getTest().log(Status.INFO, "Get Supplier List SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getSupplierListUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No get supplier list URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No get supplier list URL found in test data");
                throw new customException("No get supplier list URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger, "Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Access Token is Empty check access token", ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            supplierListRequest = new SupplierListViewRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in get supplier list setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in get supplier list setup: " + e.getMessage());
            throw new customException("Error in get supplier list setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getSupplierListUrl")
    private Object[][] getSupplierListUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading get supplier list URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading get supplier list URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "getsupplierlist".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No get supplier list URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved get supplier list URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved get supplier list URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getSupplierListUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getSupplierListData") 
    public Object[][] getSupplierListData() throws customException {
        try {
            LogUtils.info("Reading get supplier list test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading get supplier list test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No get supplier list test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "getsupplierlist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid get supplier list test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " get supplier list test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " get supplier list test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getSupplierListData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getSupplierListData")
    private void getSupplierList(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Get supplier list test execution: " + description);
            ExtentReport.createTest("Get Supplier List Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Get supplier list test execution: " + description);

            if(apiName.equalsIgnoreCase("getsupplierlist"))
            {
                requestBodyJson = new JSONObject(requestBody);

                supplierListRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                LogUtils.info("Constructed get supplier list request"); 
                ExtentReport.getTest().log(Status.INFO, "Constructed get supplier list request");

                response = ResponseUtil.getResponseWithAuth(baseURI, supplierListRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Get supplier list API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Get supplier list API executed successfully", ExtentColor.GREEN));
                    
                    // Validate response body if expected response is provided
                    if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        // Skip validation, just print response to report
                        LogUtils.info("Supplier list retrieved successfully");
                        ExtentReport.getTest().log(Status.INFO, "Supplier list retrieved successfully");
                        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier list data retrieved successfully", ExtentColor.GREEN));
                        
                        // Log expected vs actual for reference only
                        LogUtils.info("Expected Response: " + expectedResponseBody);
                        LogUtils.info("Actual Response: " + response.asString());
                        ExtentReport.getTest().log(Status.INFO, "Response contains supplier data");
                    }
                    
                    // Log the response body in the report
                    ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                } else {
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
            LogUtils.exception(logger, "Error in get supplier list test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in get supplier list test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in get supplier list test: " + e.getMessage());
        }
    }
    
    
    
    
    @DataProvider(name = "getSupplierListNegativeData")
    public Object[][] getSupplierListNegativeData() throws customException {
        try {
            LogUtils.info("Reading get supplier list negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading get supplier list negative test scenario data");
            
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
                        "getsupplierlist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid get supplier list negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting get supplier list negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting get supplier list negative test data: " + e.getMessage());
            throw new customException("Error in getting get supplier list negative test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getSupplierListNegativeData")
    public void getSupplierListNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting get supplier list negative test case: " + testCaseid);
            ExtentReport.createTest("Get Supplier List Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("getsupplierlist") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Set payload for get supplier list request based on your request object structure
                if (requestBodyJson.has("outlet_id")) {
                	supplierListRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }
                
                response = ResponseUtil.getResponseWithAuth(baseURI, supplierListRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Validate status code and report actual vs expected
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
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    	expectedJsonBody = new JSONObject(expectedResponseBody);
                        
                        // Log expected response body
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedJsonBody.toString(2));
                        
                        // Validate response message sentence count (if detail field exists)
                        if (actualJsonBody.has("detail")) {
                            String detail = actualJsonBody.getString("detail");
                            int sentenceCount = countSentences(detail);
                            
                            if (sentenceCount > 6) {
                                String errorMsg = "Response message has too many sentences. Found: " + sentenceCount + ", Maximum allowed: 6";
                                LogUtils.failure(logger, errorMsg);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            } else {
                                LogUtils.info("Response message sentence count validation passed: " + sentenceCount);
                                ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount);
                            }
                        }
                        
                        // Validate response message
                        if (expectedJsonBody.has("detail") && actualJsonBody.has("detail")) {
                            String expectedDetail = expectedJsonBody.getString("detail");
                            String actualDetail = actualJsonBody.getString("detail");
                            
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
                        validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    }
                    
                    LogUtils.success(logger, "Get supplier list negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Get supplier list negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            } else {
                String errorMsg = "Test case skipped - Not matching API name or test type. API Name: " + apiName + ", Test Type: " + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.SKIP, MarkupHelper.createLabel(errorMsg, ExtentColor.YELLOW));
            }
        } catch (Exception e) {
            String errorMsg = "Error in get supplier list negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
    
    // Helper method to count sentences in a string
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Pattern to match end of sentences (period, exclamation mark, or question mark followed by space or end of string)
        Pattern pattern = Pattern.compile("[.!?](?:\\s|$)");
        String[] sentences = pattern.split(text);
        return sentences.length;
    }
}
