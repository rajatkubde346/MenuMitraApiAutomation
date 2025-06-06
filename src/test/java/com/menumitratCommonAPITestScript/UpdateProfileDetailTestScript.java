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
import com.menumitra.apiRequest.UpdateProfileDetailRequest;
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
public class UpdateProfileDetailTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private UpdateProfileDetailRequest updateProfileDetailRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(UpdateProfileDetailTestScript.class);
   
    @BeforeClass
    private void updateProfileDetailSetUp() throws customException
    {
        try
        {
            LogUtils.info("Update Profile Detail SetUp");
            ExtentReport.createTest("Update Profile Detail SetUp");
            ExtentReport.getTest().log(Status.INFO,"Update Profile Detail SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getUpdateProfileDetailUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No update profile detail URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No update profile detail URL found in test data");
                throw new customException("No update profile detail URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger,"Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            updateProfileDetailRequest = new UpdateProfileDetailRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in update profile detail setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in update profile detail setup: " + e.getMessage());
            throw new customException("Error in update profile detail setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getUpdateProfileDetailUrl")
    private Object[][] getUpdateProfileDetailUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading update profile detail URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading update profile detail URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "updateprofiledetail".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No update profile detail URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved update profile detail URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved update profile detail URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getUpdateProfileDetailUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getUpdateProfileDetailData") 
    public Object[][] getUpdateProfileDetailData() throws customException {
        try {
            LogUtils.info("Reading update profile detail test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading update profile detail test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No update profile detail test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "updateprofiledetail".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid update profile detail test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " update profile detail test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " update profile detail test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getUpdateProfileDetailData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getUpdateProfileDetailData")
    private void verifyUpdateProfileDetail(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Update profile detail test execution: " + description);
            ExtentReport.createTest("Update Profile Detail Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Update profile detail test execution: " + description);

            if(apiName.equalsIgnoreCase("updateprofiledetail"))
            {
                requestBodyJson = new JSONObject(requestBody);

                updateProfileDetailRequest.setUpdate_user_id(String.valueOf(user_id));
                updateProfileDetailRequest.setUser_id(requestBodyJson.getString("user_id"));
                updateProfileDetailRequest.setName(requestBodyJson.getString("name"));
                updateProfileDetailRequest.setEmail(requestBodyJson.getString("email"));
                updateProfileDetailRequest.setMobile_number(requestBodyJson.getString("mobile_number"));
                updateProfileDetailRequest.setDob(requestBodyJson.getString("dob"));
                updateProfileDetailRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                
                LogUtils.info("Constructed update profile detail request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed update profile detail request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, updateProfileDetailRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Update profile detail API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update profile detail API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Only show response body without validation
                    actualJsonBody = new JSONObject(response.asString());
                    LogUtils.info("Response received successfully");
                    LogUtils.info("Response Body: " + actualJsonBody.toString());
                    ExtentReport.getTest().log(Status.PASS, "Response received successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response Body: " + actualJsonBody.toString());
                    
                    // Make sure to use Status.PASS for the response to show in the report
                    ExtentReport.getTest().log(Status.PASS, "Full Response:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    
                    // Add a screenshot or additional details that might help visibility
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                }
                else{
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    LogUtils.info("Response Body: " + response.asString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asPrettyString());
                    throw new customException(errorMsg);
                }
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in update profile detail test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in update profile detail test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in update profile detail test: " + e.getMessage());
        }
    }
    
    
    @DataProvider(name = "getUpdateProfileDetailNegativeData")
    public Object[][] getUpdateProfileDetailNegativeData() throws customException {
        try {
            LogUtils.info("Reading update profile detail negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading update profile detail negative test scenario data");
            
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
                        "updateprofiledetail".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid update profile detail negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting update profile detail negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting update profile detail negative test data: " + e.getMessage());
            throw new customException("Error in getting update profile detail negative test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUpdateProfileDetailNegativeData")
    public void updateProfileDetailNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting update profile detail negative test case: " + testCaseid);
            ExtentReport.createTest("Update Profile Detail Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("updateprofiledetail") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Set request parameters
                updateProfileDetailRequest.setUpdate_user_id(String.valueOf(user_id));
                if (requestBodyJson.has("user_id")) {
                    updateProfileDetailRequest.setUser_id(requestBodyJson.getString("user_id"));
                }
                if (requestBodyJson.has("name")) {
                    updateProfileDetailRequest.setName(requestBodyJson.getString("name"));
                }
                if (requestBodyJson.has("email")) {
                    updateProfileDetailRequest.setEmail(requestBodyJson.getString("email"));
                }
                if (requestBodyJson.has("mobile_number")) {
                    updateProfileDetailRequest.setMobile_number(requestBodyJson.getString("mobile_number"));
                }
                if (requestBodyJson.has("dob")) {
                    updateProfileDetailRequest.setDob(requestBodyJson.getString("dob"));
                }
                if (requestBodyJson.has("aadhar_number")) {
                    updateProfileDetailRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                }
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, updateProfileDetailRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                int expectedStatusCode = Integer.parseInt(statusCode);
                
                // Validate status code
                if (response.getStatusCode() != expectedStatusCode) {
                    String errorMsg = "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected Status Code: " + expectedStatusCode);
                    ExtentReport.getTest().log(Status.FAIL, "Actual Status Code: " + response.getStatusCode());
                } else {
                    LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                    
                    // Validate response body
                    actualJsonBody = new JSONObject(response.asString());
                    
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedJsonBody = new JSONObject(expectedResponseBody);
                        
                        // Validate response message length (6 sentences maximum)
                        if (actualJsonBody.has("message")) {
                            String message = actualJsonBody.getString("message");
                            int sentenceCount = message.split("[.!?]+").length;
                            
                            if (sentenceCount > 6) {
                                String errorMsg = "Response message exceeds 6 sentences limit. Current count: " + sentenceCount;
                                LogUtils.failure(logger, errorMsg);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                                ExtentReport.getTest().log(Status.FAIL, "Message: " + message);
                            } else {
                                LogUtils.success(logger, "Response message length validation passed. Sentence count: " + sentenceCount);
                                ExtentReport.getTest().log(Status.PASS, "Response message length validation passed. Sentence count: " + sentenceCount);
                            }
                        }
                        
                        // Validate response message content
                        if (expectedJsonBody.has("message") && actualJsonBody.has("message")) {
                            String expectedMessage = expectedJsonBody.getString("message");
                            String actualMessage = actualJsonBody.getString("message");
                            
                            if (expectedMessage.equals(actualMessage)) {
                                LogUtils.success(logger, "Response message validation passed");
                                ExtentReport.getTest().log(Status.PASS, "Response message validation passed");
                            } else {
                                String errorMsg = "Response message mismatch - Expected: " + expectedMessage + ", Actual: " + actualMessage;
                                LogUtils.failure(logger, errorMsg);
                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            }
                        }
                        
                        // Complete response validation
                        validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    }
                    
                    LogUtils.success(logger, "Update profile detail negative test completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update profile detail negative test completed successfully", ExtentColor.GREEN));
                }
                
                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            }
        } catch (Exception e) {
            String errorMsg = "Error in update profile detail negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
}