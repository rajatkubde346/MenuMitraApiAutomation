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
public class sectionUpdateTestScript extends APIBase
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
    Logger logger=LogUtils.getLogger(sectionCreateTestScript.class);;


    @DataProvider(name="getSectionUpdateURL")
    public Object[][] getSectionUpdateURL() throws customException
    {
        try{
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");
            if(readData==null)
            {
                LogUtils.error("Error: Getting an error while read Section URL Excel File");
                throw new customException("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectionupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.error("Error: Getting an error while read Section URL Excel File");
            throw new customException("Error: Getting an error while read Section URL Excel File");
        }
    }


    @DataProvider(name="getSectionUpdatePositiveInputData")
    private Object[][] getSectionUpdatePositiveInputData() throws customException {
        try {
            LogUtils.info("Reading positive test scenario data for section update API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, property.getProperty("CommonAPITestScenario"));
            
            if (testData == null || testData.length == 0)
            {
                LogUtils.failure(logger, "No Section Update API positive test scenario data found in Excel sheet");
                throw new customException("No Section Update API Positive test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "sectionupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.exception(logger, "Failed to read Section Update API positive test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading Section Update API positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name="getSectionUpdateNegativeInputData")
    private Object[][] getSectionUpdateNegativeInputData() throws customException {
        try {
            LogUtils.info("Reading negative test scenario data for section update API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No section update API negative test scenario data found in Excel sheet");
                throw new customException("No section update API negative test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            // Filter for section update API negative test cases
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "sectionupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
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
            LogUtils.exception(logger, "Failed to read section update API negative test scenario data: " + e.getMessage(), e);
            throw new customException("Error reading section update API negative test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void sectionUpdateSetup() throws customException 
    {
        try {
            LogUtils.info("Setting up test environment for section update");
            ExtentReport.createTest("Start Section Update");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set section update URL
            Object[][] sectionUpdateData = getSectionUpdateURL();
            if (sectionUpdateData.length > 0) {
                String endpoint = sectionUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section Update URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section update URL found in test data");
                throw new customException("No section update URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            sectionrequest = new sectionRequest();
            LogUtils.info("Section update setup completed successfully");
            ExtentReport.getTest().log(Status.INFO, "Section update setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section update setup: " + e.getMessage(), e);
            throw new customException("Error during setup: " + e.getMessage());
        }
    }



@Test(dataProvider = "getSectionUpdatePositiveInputData", priority = 1)
private void verifySectionUpdateUsingValidInputData(String apiName, String testCaseId, 
    String testType, String description, String httpsMethod, 
    String requestBody, String expectedResponseBody, String statusCode) throws customException
{
    try
    {
        LogUtils.info("start section update api using valid input data");
        ExtentReport.createTest("Verify Section Update APi: "+description);
        ExtentReport.getTest().log(Status.INFO,"====start section update by Using Positive Input Data===");
        ExtentReport.getTest().log(Status.INFO,"Constructed Base URI: "+baseUri);
        if(apiName.contains("sectionupdate") && testType.contains("positive"))
        {
            requestBodyJson=new JSONObject(requestBody);
            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            sectionrequest.setUser_id(String.valueOf(userId));
            sectionrequest.setSection_name(requestBodyJson.getString("section_name"));
            sectionrequest.setSection_id(String.valueOf(requestBodyJson.getInt("section_id")));
            
            // Log the HTTP method being used
            LogUtils.info("Using HTTP method: " + httpsMethod);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsMethod);
            
            // Log request body for debugging
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + sectionrequest.toString());
            
            response=ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            
            // Log response status code immediately after request
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("section update api response ");
            ExtentReport.getTest().log(Status.INFO,"section update api response: "+response.getBody().asString());
            
            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    expectedJsonBody=new JSONObject(expectedResponseBody);
                    
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    LogUtils.success(logger,"Successfully Validate Section Update Api By using Positive Input data");
                    ExtentReport.getTest().log(Status.PASS,"Successfully Validate Section Update Api By using Positive Input data");
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                    throw new customException("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code to check section update api using positive input data: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Invalid status code to check section update api using positive input data: " + response.getStatusCode());
                throw new customException("In section update api using positive input test case Expected status code 200 but got " + response.getStatusCode());
            }                
            
        }

    }
    catch(Exception e)
    {
        LogUtils.exception(logger, "An error occurred during section update verification: "+e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL,"An error occurred during section update verification: "+e.getMessage());
        throw new customException("An error occurred during section update verification");
    }
}

@Test(dataProvider = "getSectionUpdateNegativeInputData", priority = 2)
private void verifySectionCreateUsingInvalidData(String apiName, String testCaseId, 
    String testType, String description, String httpsMethod, 
    String requestBody, String expectedResponseBody, String statusCode) throws customException 
    {
    
    try {
        LogUtils.info("=====Starting section create API negative test=====");
        ExtentReport.createTest("Verify section create using Invalid Input data: "+description);
        ExtentReport.getTest().log(Status.INFO,"====Verify section create using Valid Input data====");
        ExtentReport.getTest().log(Status.INFO,"Constructed section create Base URI: " + baseUri);
        if (apiName.contains("sectionupdate") && testType.contains("negative")) 
        {
            // Parse request and expected response
            requestBodyJson = new JSONObject(requestBody);
            expectedJsonBody = new JSONObject(expectedResponseBody);
            
            sectionrequest = new sectionRequest();
            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            sectionrequest.setUser_id(String.valueOf(userId));
            sectionrequest.setSection_name(requestBodyJson.getString("section_name"));
            sectionrequest.setSection_id(String.valueOf(requestBodyJson.getInt("section_id")));

            
            LogUtils.info("Section create payload prepared ");
            ExtentReport.getTest().log(Status.INFO, "Section create payload prepared ");
            
            // Make API call
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            LogUtils.info("POST request executed for section create API");
            ExtentReport.getTest().log(Status.INFO, "POST request executed for section create API");
           
            // Validate response based on test case
            switch (testCaseId)
             {
                case "sectioncreate_002": 
                        validateResponseBody.handleResponseBody(response, expectedJsonBody);
                        LogUtils.success(logger,"Section create API responded with status code 200");
                        ExtentReport.getTest().log(Status.PASS, "Section create API responded with status code 200");
                    break;
                    
                
              
                default:
                    
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    LogUtils.success(logger,"Section create API responded with status code 200");
                    ExtentReport.getTest().log(Status.PASS, "Section create API responded with status code 200");
            }
            
            LogUtils.info("Successfully validated section create API negative test case: " + testCaseId);
            ExtentReport.getTest().log(Status.PASS, "Successfully validated section create API negative test case: " + testCaseId);
        }
    } catch (Exception e) {
        LogUtils.exception(logger, "Error in negative test case " + testCaseId + ": " + e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL, "Error in negative test case " + testCaseId + ": " + e.getMessage());
        throw new customException("Error in negative test case " + testCaseId + ": " + e.getMessage());
    }
}



@DataProvider(name = "getSectionUpdateNegativeData")
public Object[][] getSectionUpdateNegativeData() throws customException {
    try {
        LogUtils.info("Reading section update negative test scenario data");
        ExtentReport.getTest().log(Status.INFO, "Reading section update negative test scenario data");
        
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
                    "sectionupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                
                filteredData.add(row);
            }
        }
        
        if (filteredData.isEmpty()) {
            String errorMsg = "No valid section update negative test data found after filtering";
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
        LogUtils.failure(logger, "Error in getting section update negative test data: " + e.getMessage());
        ExtentReport.getTest().log(Status.FAIL, "Error in getting section update negative test data: " + e.getMessage());
        throw new customException("Error in getting section update negative test data: " + e.getMessage());
    }
}

@Test(dataProvider = "getSectionUpdateNegativeData")
public void sectionUpdateNegativeTest(String apiName, String testCaseid, String testType, String description,
        String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
    try {
        LogUtils.info("Starting section update negative test case: " + testCaseid);
        ExtentReport.createTest("Section Update Negative Test - " + testCaseid + ": " + description);
        ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
        
        if (apiName.equalsIgnoreCase("sectionupdate") && testType.equalsIgnoreCase("negative")) {
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            sectionrequest.setUser_id(String.valueOf(userId));
            sectionrequest.setSection_name(requestBodyJson.getString("section_name"));
            sectionrequest.setSection_id(String.valueOf(requestBodyJson.getInt("section_id")));
            
            // Get the response
            response = ResponseUtil.getResponseWithAuth(baseUri,sectionrequest ,httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Log expected vs actual status code
            LogUtils.info("Expected Status Code: " + expectedStatusCode);
            LogUtils.info("Actual Status Code: " + response.getStatusCode());
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
                actualResponseBody = new JSONObject(response.asString());
                
                // Log expected vs actual response body
                LogUtils.info("Expected Response Body: " + expectedResponseBody);
                LogUtils.info("Actual Response Body: " + actualResponseBody.toString());
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualResponseBody.toString());
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedJsonBody = new JSONObject(expectedResponseBody);
                    
                    // Validate response message if detail field exists
                    if (expectedJsonBody.has("detail") && actualResponseBody.has("detail")) {
                        String expectedDetail = expectedJsonBody.getString("detail");
                        String actualDetail = actualResponseBody.getString("detail");
                        
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
                
                LogUtils.success(logger, "Section update negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Section update negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        }
    } catch (Exception e) {
        String errorMsg = "Error in section update negative test: " + e.getMessage();
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
//    try 
//    {
//        LogUtils.info("===Test environment tear down successfully===");
//       
//        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
//        
//        ActionsMethods.logout();
//        TokenManagers.clearTokens();
//        
//    } 
//    catch (Exception e) 
//    {
//        LogUtils.exception(logger, "Error during test environment tear down", e);
//        ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
//    }
}

}
