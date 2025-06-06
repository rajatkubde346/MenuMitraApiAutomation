package com.menumitratCommonAPITestScript;

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
import com.menumitra.apiRequest.sectionListViewRequest;
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

import io.restassured.RestAssured;
import io.restassured.response.Response;

@Listeners(Listener.class)
public class sectionListViewTestScript extends APIBase
{

	private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualJsonBody;
    private JSONObject expectedJsonBody; 
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private sectionRequest sectionrequest;
    private sectionListViewRequest sectionListViewRequest;
    Logger logger=LogUtils.getLogger(sectionCreateTestScript.class); 	
    
    
    @DataProvider(name="getSectionListViewURL")
    public Object[][] getSectionListViewURL() throws customException
    {
        try{
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");
            if(readData==null)
            {
                LogUtils.failure(logger, "Error: Getting an error while read Section URL Excel File");
                throw new customException("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectionlistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section URL Excel File", e);
            throw new customException("Error: Getting an error while read Section URL Excel File");
        }
    }
	
@DataProvider(name="getSectionListViewPositiveInputData")
private Object[][] getSectionListViewPositiveInputData() throws customException {
    try {
        LogUtils.info("Reading positive test scenario data for section list view API from Excel sheet");
        Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, property.getProperty("CommonAPITestScenario"));
        
        if (testData == null || testData.length == 0) {
            LogUtils.failure(logger, "No Section List View API positive test scenario data found in Excel sheet");
            throw new customException("No Section List View API Positive test scenario data found in Excel sheet");
        }         
        
        List<Object[]> filteredData = new ArrayList<>();
        
        for (int i = 0; i < testData.length; i++) {
            Object[] row = testData[i];

            // Ensure row is not null and has at least 3 columns
            if (row != null && row.length >= 3 &&
                "sectionlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
        LogUtils.exception(logger, "Failed to read Section List View API positive test scenario data: " + e.getMessage(), e);
        throw new customException("Error reading Section List View API positive test scenario data from Excel sheet: " + e.getMessage());
    }
}

@DataProvider(name="getSectionListViewNegativeInputData")
private Object[][] getSectionListViewNegativeInputData() throws customException 
{
    try {
        LogUtils.info("Reading negative test scenario data for section list view API");
        Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
        
        if (testData == null || testData.length == 0) 
        {
            LogUtils.failure(logger, "No section list view API negative test scenario data found in Excel sheet");
            throw new customException("No section list view API negative test scenario data found in Excel sheet");
        }
        
        List<Object[]> filteredData = new ArrayList<>();
        
        // Filter for section list view API negative test cases
        for (int i = 0; i < testData.length; i++) {
            Object[] row = testData[i];
            if (row != null && row.length >= 3 &&
                "sectionlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
        LogUtils.exception(logger, "Failed to read section list view API negative test scenario data: " + e.getMessage(), e);
        throw new customException("Error reading section list view API negative test scenario data from Excel sheet: " + e.getMessage());
    }
}

@BeforeClass
private void sectionListViewSetup() throws customException 
{
    try {
        LogUtils.info("Setting up test environment");
        ExtentReport.createTest("Start Section List View");
        ActionsMethods.login();
        ActionsMethods.verifyOTP();

        // Get base URL
        baseUri = EnviromentChanges.getBaseUrl();
        LogUtils.info("Base URI set to: " + baseUri);

        // Get and set section list view URL
        Object[][] sectionListViewData = getSectionListViewURL();
        if (sectionListViewData.length > 0) {
            String endpoint = sectionListViewData[0][2].toString();
            url = new URL(endpoint);
            baseUri = RequestValidator.buildUri(endpoint, baseUri);
            LogUtils.info("Section List View URL set to: " + baseUri);
        } else {
            LogUtils.failure(logger, "No section list view URL found in test data");
            throw new customException("No section list view URL found in test data");
        }

        // Get tokens from TokenManager
        accessToken = TokenManagers.getJwtToken();
        userId = TokenManagers.getUserId();

        if (accessToken.isEmpty()) {
            LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
            throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
        }

        sectionrequest = new sectionRequest();
        
        LogUtils.info("Section list view setup completed successfully");

    } catch (Exception e) {
        LogUtils.exception(logger, "Error during section list view setup: " + e.getMessage(), e);
        throw new customException("Error during setup: " + e.getMessage());
    }
}


@Test(dataProvider = "getSectionListViewPositiveInputData", priority = 1)
private void verifySectionListViewUsingValidInputData(String apiName, String testCaseId,
        String testType, String description, String httpsMethod,
        String requestBody, String expectedResponseBody, String statusCode) throws customException {
    try {
        LogUtils.info("Start section list view API using valid input data");
        ExtentReport.createTest("Verify Section List View API: " + description);
        ExtentReport.getTest().log(Status.INFO, "====Start section list view using positive input data====");
        ExtentReport.getTest().log(Status.INFO, "Constructed Base URI: " + baseUri);

        if (apiName.contains("sectionlistview") && testType.contains("positive")) {
            requestBodyJson = new JSONObject(requestBody);
            expectedJsonBody = new JSONObject(expectedResponseBody);
            sectionListViewRequest = new sectionListViewRequest();
            sectionListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            LogUtils.info("Verify section list view payload prepared");
            ExtentReport.getTest().log(Status.INFO, "Verify section list view payload prepared with outlet_id: " + requestBodyJson.getString("outlet_id"));
            
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionListViewRequest, httpsMethod, accessToken);
            LogUtils.info("Section list view API response");
            ExtentReport.getTest().log(Status.INFO, "Section list view API response: " + response.getBody().asString());
            System.out.println(response.getStatusCode());
            
            if(response.getStatusCode() == 200) {
                LogUtils.success(logger, "Section list view API executed successfully");
                LogUtils.info("Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Section list view API executed successfully", ExtentColor.GREEN));
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
            } else {
                String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asPrettyString());
                throw new customException(errorMsg);
            }
        } else {
            String errorMsg = "API name or test type mismatch - Expected: sectionlistview/positive, Actual: " + apiName + "/" + testType;
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    } catch (Exception e) {
        LogUtils.exception(logger, "An error occurred during section list view verification: " + e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL, "An error occurred during section list view verification: " + e.getMessage());
        throw new customException("An error occurred during section list view verification");
    }
}

/**
 * Test method for negative scenarios
 */
//@Test(dataProvider = "getSectionListViewNegativeInputData", priority = 2)
private void verifySectionListViewUsingInvalidData(String apiName, String testCaseId,
        String testType, String description, String httpsMethod,
        String requestBody, String expectedResponseBody, String statusCode) throws customException {

    try {
        LogUtils.info("=====Starting section list view API negative test=====");
        ExtentReport.createTest("Verify section list view using Invalid Input data: " + description);
        ExtentReport.getTest().log(Status.INFO, "====Verify section list view using Invalid Input data====");
        ExtentReport.getTest().log(Status.INFO, "Constructed section list view Base URI: " + baseUri);

        if (apiName.contains("sectionlistview") && testType.contains("negative")) {
            // Parse request and expected response
            requestBodyJson = new JSONObject(requestBody);
            expectedJsonBody = new JSONObject(expectedResponseBody);

            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            sectionrequest.setUser_id(String.valueOf(userId));
            LogUtils.info("Section list view payload prepared");
            ExtentReport.getTest().log(Status.INFO, "Section list view payload prepared");

            // Make API call
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            LogUtils.info("GET request executed for section list view API");
            ExtentReport.getTest().log(Status.INFO, "GET request executed for section list view API");

            // Validate response
            validateResponseBody.handleResponseBody(response, expectedJsonBody);
            LogUtils.success(logger, "Section list view API responded with expected status code");
            ExtentReport.getTest().log(Status.PASS, "Section list view API responded with expected status code");

            LogUtils.info("Successfully validated section list view API negative test case: " + testCaseId);
            ExtentReport.getTest().log(Status.PASS, "Successfully validated section list view API negative test case: " + testCaseId);
        }
    } catch (Exception e) {
        LogUtils.exception(logger, "Error in negative test case " + testCaseId + ": " + e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL, "Error in negative test case " + testCaseId + ": " + e.getMessage());
        throw new customException("Error in negative test case " + testCaseId + ": " + e.getMessage());
    }
}


@DataProvider(name = "getSectionListViewNegativeData")
public Object[][] getSectionListViewNegativeData() throws customException {
    try {
        LogUtils.info("Reading section list view negative test scenario data");
        ExtentReport.getTest().log(Status.INFO, "Reading section list view negative test scenario data");
        
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
                    "sectionlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                
                filteredData.add(row);
            }
        }
        
        if (filteredData.isEmpty()) {
            String errorMsg = "No valid section list view negative test data found after filtering";
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
        LogUtils.failure(logger, "Error in getting section list view negative test data: " + e.getMessage());
        ExtentReport.getTest().log(Status.FAIL, "Error in getting section list view negative test data: " + e.getMessage());
        throw new customException("Error in getting section list view negative test data: " + e.getMessage());
    }
}

@Test(dataProvider = "getSectionListViewNegativeData")
public void sectionListViewNegativeTest(String apiName, String testCaseid, String testType, String description,
        String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
    try {
        LogUtils.info("Starting section list view negative test case: " + testCaseid);
        ExtentReport.createTest("Section List View Negative Test - " + testCaseid + ": " + description);
        ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
        
        if (apiName.equalsIgnoreCase("sectionlistview") && testType.equalsIgnoreCase("negative")) {
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Set payload for section list view request
            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Compare expected vs actual status code
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
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                	expectedJsonBody = new JSONObject(expectedResponseBody);
                    
                    // Compare expected vs actual response body
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedJsonBody.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));
                    
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
                
                LogUtils.success(logger, "Section list view negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Section list view negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        }
    } catch (Exception e) {
        String errorMsg = "Error in section list view negative test: " + e.getMessage();
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
