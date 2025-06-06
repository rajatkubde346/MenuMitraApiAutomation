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
import com.menumitra.apiRequest.StaffListViewRequest;
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
public class SupplierListViewTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private SupplierListViewRequest supplierListViewRequest;
    private URL url;
    private JSONObject expectedResponseJson;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(SupplierListViewTestScript.class);
   
    @BeforeClass
    private void supplierListViewSetUp() throws customException
    {
        try
        {
            LogUtils.info("Supplier List View SetUp");
            ExtentReport.createTest("Supplier List View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Supplier List View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getSupplierListViewUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No supplier list view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No supplier list view URL found in test data");
                throw new customException("No supplier list view URL found in test data");
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
            
            supplierListViewRequest = new SupplierListViewRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in supplier list view setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in supplier list view setup: " + e.getMessage());
            throw new customException("Error in supplier list view setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getSupplierListViewUrl")
    private Object[][] getSupplierListViewUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading supplier list view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading supplier list view URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "supplierlistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No supplier list view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved supplier list view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved supplier list view URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getSupplierListViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getSupplierListViewPositiveData") 
    public Object[][] getSupplierListViewPositiveData() throws customException {
        try {
            LogUtils.info("Reading supplier list view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading supplier list view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No supplier list view test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "supplierlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier list view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " supplier list view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " supplier list view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getSupplierListViewPositiveData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getSupplierListViewPositiveData")
    private void verifySupplierListViewUsingPositiveData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Supplier list view test execution: " + description);
            ExtentReport.createTest("Supplier list view test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Supplier list view test execution: " + description);

            if(apiName.equalsIgnoreCase("supplierlistview"))
            {
                requestBodyJson = new JSONObject(requestBody);

                supplierListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                LogUtils.info("Constructed supplier list view request"); 
                ExtentReport.getTest().log(Status.INFO, "Constructed supplier list view request");

                response = ResponseUtil.getResponseWithAuth(baseURI, supplierListViewRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Supplier list view API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier list view API executed successfully", ExtentColor.GREEN));
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
            LogUtils.exception(logger, "Error in supplier list view test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in supplier list view test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in supplier list view test: " + e.getMessage());
        }
    }
    
    
    @DataProvider(name = "getSupplierListViewNegativeData")
    public Object[][] getSupplierListViewNegativeData() throws customException {
        try {
            LogUtils.info("Reading supplier list view negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading supplier list view negative test scenario data");
            
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
                        "supplierlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier list view negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting supplier list view negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting supplier list view negative test data: " + e.getMessage());
            throw new customException("Error in getting supplier list view negative test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getSupplierListViewNegativeData")
    public void supplierListViewNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting supplier list view negative test case: " + testCaseid);
            ExtentReport.createTest("Supplier List View Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("supplierlistview") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Set payload for supplier list view request
                if (requestBodyJson.has("outlet_id")) {
                    supplierListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }
                
                response = ResponseUtil.getResponseWithAuth(baseURI, supplierListViewRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Report actual vs expected status code
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
                    
                    // Report actual response body
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedResponseJson = new JSONObject(expectedResponseBody);
                        
                        // Report expected response body
                        ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseJson.toString(2));
                        
                        // Validate response message
                        if (expectedResponseJson.has("detail") && actualJsonBody.has("detail")) {
                            String expectedDetail = expectedResponseJson.getString("detail");
                            String actualDetail = actualJsonBody.getString("detail");
                            
                            // Validate sentence count in response message
                            int sentenceCount = countSentences(actualDetail);
                            if (sentenceCount > 6) {
                                LogUtils.failure(logger, "Response message contains more than 6 sentences: " + sentenceCount);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Response message validation failed: Contains more than 6 sentences (" + sentenceCount + ")", ExtentColor.RED));
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
                        validateResponseBody.handleResponseBody(response, expectedResponseJson);
                    }
                    
                    LogUtils.success(logger, "Supplier list view negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier list view negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            } else {
                String errorMsg = "Invalid API name or test type - Expected: supplierlistview/negative, Actual: " + apiName + "/" + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            }
        } catch (Exception e) {
            String errorMsg = "Error in supplier list view negative test: " + e.getMessage();
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
     * Count the number of sentences in a string
     * @param text The text to count sentences in
     * @return The number of sentences
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Split by common sentence ending punctuation followed by space or end of string
        String[] sentences = text.split("[.!?]+\\s*");
        
        // Count non-empty sentences
        int count = 0;
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                count++;
            }
        }
        
        return count;
    }
    
}
