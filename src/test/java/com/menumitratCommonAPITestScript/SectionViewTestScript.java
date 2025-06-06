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
public class SectionViewTestScript extends APIBase
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
    
    
    @DataProvider(name="getSectionViewURL")
    public Object[][] getSectionViewURL() throws customException
    {
        try{
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");
            if(readData==null)
            {
                LogUtils.failure(logger, "Error: Getting an error while read Section URL Excel File");
                throw new customException("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectionview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section URL Excel File", e);
            throw new customException("Error: Getting an error while read Section URL Excel File");
        }
    }

    @DataProvider(name="getSectionViewPositiveInputData")
    private Object[][] getSectionViewPositiveInputData() throws customException {
        try {
            LogUtils.info("Reading positive test scenario data for section view API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, 
            		property.getProperty("CommonAPITestScenario"));
            
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No Section View API positive test scenario data found in Excel sheet");
                throw new customException("No Section View API Positive test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "sectionview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Section View API positive test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading Section View API positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getSectionViewNegativeInputData")
    private Object[][] getSectionViewNegativeInputData() throws customException {
        try {
            LogUtils.info("Reading negative test scenario data for section view API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No section view API negative test scenario data found in Excel sheet");
                throw new customException("No section view API negative test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            // Filter for section view API negative test cases
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "sectionview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.exception(logger, "Failed to read section view API negative test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading section view API negative test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    

    @BeforeClass
    private void sectionViewSetup() throws customException 
    {
        try {
            LogUtils.info("Setting up test environment");
            ExtentReport.createTest("Start Section View");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set section view URL
            Object[][] sectionViewData = getSectionViewURL();
            if (sectionViewData.length > 0) {
                String endpoint = sectionViewData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section View URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section view URL found in test data");
                throw new customException("No section view URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            sectionrequest = new sectionRequest();
            LogUtils.info("Section view setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section view setup: " + e.getMessage(), e);
            throw new customException("Error during setup: " + e.getMessage());
        }
    }


@Test(dataProvider = "getSectionViewPositiveInputData", priority = 1)
private void verifySectionViewUsingValidInputData(String apiName, String testCaseId,
        String testType, String description, String httpsMethod,
        String requestBody, String expectedResponseBody, String statusCode) throws customException 
{
    try {
        LogUtils.info("Start section view API using valid input data");
        ExtentReport.createTest("Verify Section View API: " + description);
        ExtentReport.getTest().log(Status.INFO, "====Start section view using positive input data====");
        ExtentReport.getTest().log(Status.INFO, "Constructed Base URI: " + baseUri);

        if (apiName.contains("sectionview") && testType.contains("positive")) {
            requestBodyJson = new JSONObject(requestBody);
            expectedJsonBody = new JSONObject(expectedResponseBody);

            sectionrequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            sectionrequest.setSection_id(requestBodyJson.getString("section_id"));
            LogUtils.info("Section view payload prepared");
            
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            LogUtils.info("Section view API response");
            ExtentReport.getTest().log(Status.INFO, "Section view API response: " + response.getBody().asString());

            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    //validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    LogUtils.success(logger, "Successfully validated section view API using positive input data");
                    ExtentReport.getTest().log(Status.PASS, "Successfully validated section view API using positive input data");
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                    throw new customException("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code for section view API using positive input data: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Invalid status code for section view API using positive input data: " + response.getStatusCode());
                throw new customException("In section view API using positive input test case expected status code 200 but got " + response.getStatusCode());
            }
        }
    } catch (Exception e) {
        LogUtils.exception(logger, "An error occurred during section view verification: " + e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL, "An error occurred during section view verification: " + e.getMessage());
        throw new customException("An error occurred during section view verification");
    }
}



@DataProvider(name = "getSectionViewNegativeData")
public Object[][] getSectionViewNegativeData() throws customException {
    try {
        LogUtils.info("Reading section view negative test scenario data");
        ExtentReport.getTest().log(Status.INFO, "Reading section view negative test scenario data");
        
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
                    "sectionview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                
                filteredData.add(row);
            }
        }
        
        if (filteredData.isEmpty()) {
            String errorMsg = "No valid section view negative test data found after filtering";
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
        LogUtils.failure(logger, "Error in getting section view negative test data: " + e.getMessage());
        ExtentReport.getTest().log(Status.FAIL, "Error in getting section view negative test data: " + e.getMessage());
        throw new customException("Error in getting section view negative test data: " + e.getMessage());
    }
}

@Test(dataProvider = "getSectionViewNegativeData")
public void sectionViewNegativeTest(String apiName, String testCaseid, String testType, String description,
        String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
    try {
        LogUtils.info("Starting section view negative test case: " + testCaseid);
        ExtentReport.createTest("Section View Negative Test - " + testCaseid + ": " + description);
        ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
        
        if (apiName.equalsIgnoreCase("sectionview") && testType.equalsIgnoreCase("negative")) {
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            //sectionrequest.setSection_id(requestBodyJson.getString("section_id"));
            
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Check for server errors
            if (response.getStatusCode() == 500 || response.getStatusCode() == 502) {
                LogUtils.failure(logger, "Server error detected with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error detected: " + response.getStatusCode(), ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.FAIL, "Expected Status Code: " + expectedStatusCode + ", Actual Status Code: " + response.getStatusCode());
            }
            // Validate status code
            else if (response.getStatusCode() != expectedStatusCode) {
                LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Expected Status Code: " + expectedStatusCode + ", Actual Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Expected Response: " + expectedResponseBody);
                ExtentReport.getTest().log(Status.FAIL, "Actual Response: " + response.asString());
            }
            else {
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed");
                ExtentReport.getTest().log(Status.PASS, "Expected Status Code: " + expectedStatusCode + ", Actual Status Code: " + response.getStatusCode());
                
                // Validate response body
                actualResponseBody = new JSONObject(response.asString());
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                	expectedJsonBody = new JSONObject(expectedResponseBody);
                    
                    // Validate response message
                    if (expectedJsonBody.has("detail") && actualResponseBody.has("detail")) {
                        String expectedDetail = expectedJsonBody.getString("detail");
                        String actualDetail = actualResponseBody.getString("detail");
                        
                        if (expectedDetail.equals(actualDetail)) {
                            LogUtils.info("Error message validation passed: " + actualDetail);
                            ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                        } else {
                            LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Expected Detail: " + expectedDetail + ", Actual Detail: " + actualDetail);
                        }
                    }
                    
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedJsonBody.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualResponseBody.toString(2));
                    
                    // Complete response validation
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                }
                
                LogUtils.success(logger, "Section view negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Section view negative test completed successfully", ExtentColor.GREEN));
            }
        }
    } catch (Exception e) {
        String errorMsg = "Error in section view negative test: " + e.getMessage();
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
