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
import com.menumitra.apiRequest.UpdateOutletRequest;
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
public class UpdateOutletTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private UpdateOutletRequest updateOutletRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(UpdateOutletTestScript.class);
   
    @BeforeClass
    private void updateOutletSetUp() throws customException
    {
        try
        {
            LogUtils.info("Update Outlet SetUp");
            ExtentReport.createTest("Update Outlet SetUp");
            ExtentReport.getTest().log(Status.INFO,"Update Outlet SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getUpdateOutletUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No update outlet URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No update outlet URL found in test data");
                throw new customException("No update outlet URL found in test data");
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
            
            updateOutletRequest = new UpdateOutletRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in update outlet setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in update outlet setup: " + e.getMessage());
            throw new customException("Error in update outlet setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getUpdateOutletUrl")
    private Object[][] getUpdateOutletUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading update outlet URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading update outlet URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "updateoutlet".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No update outlet URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved update outlet URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved update outlet URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getUpdateOutletUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getUpdateOutletData") 
    public Object[][] getUpdateOutletData() throws customException {
        try {
            LogUtils.info("Reading update outlet test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading update outlet test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No update outlet test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "updateoutlet".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid update outlet test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " update outlet test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " update outlet test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getUpdateOutletData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getUpdateOutletData")
    private void verifyUpdateOutlet(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Update outlet test execution: " + description);
            ExtentReport.createTest("Update Outlet Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Update outlet test execution: " + description);

            if(apiName.equalsIgnoreCase("updateoutlet"))
            {
                requestBodyJson = new JSONObject(requestBody.replace("\\","\\\\"));

               
                LogUtils.info("Constructed update outlet request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed update outlet request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                
                // Initialize the UpdateOutletRequest object with extracted values
                updateOutletRequest = new UpdateOutletRequest();
                updateOutletRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                updateOutletRequest.setUser_id(String.valueOf(user_id));
                updateOutletRequest.setName(requestBodyJson.getString("name"));
                updateOutletRequest.setOutlet_type(requestBodyJson.getString("outlet_type"));
                updateOutletRequest.setFssainumber(requestBodyJson.getString("fssainumber"));
                updateOutletRequest.setGstnumber(requestBodyJson.getString("gstnumber"));
                updateOutletRequest.setMobile(requestBodyJson.getString("mobile"));
                updateOutletRequest.setVeg_nonveg(requestBodyJson.getString("veg_nonveg"));
                updateOutletRequest.setService_charges(requestBodyJson.getString("service_charges"));
                updateOutletRequest.setGst(requestBodyJson.getString("gst"));
                updateOutletRequest.setAddress(requestBodyJson.getString("address"));
                updateOutletRequest.setIs_open(requestBodyJson.getBoolean("is_open"));
                updateOutletRequest.setUpi_id(requestBodyJson.optString("upi_id"));
                updateOutletRequest.setWebsite(requestBodyJson.getString("website"));
                updateOutletRequest.setWhatsapp(requestBodyJson.getString("whatsapp"));
                updateOutletRequest.setFacebook(requestBodyJson.getString("facebook"));
                updateOutletRequest.setInstagram(requestBodyJson.getString("instagram"));
                updateOutletRequest.setGoogle_business_link(requestBodyJson.getString("google_business_link"));
                updateOutletRequest.setGoogle_review(requestBodyJson.getString("google_review"));
                
                LogUtils.info("Update outlet request payload created successfully");
                ExtentReport.getTest().log(Status.INFO, "Update outlet request payload created successfully");
                response = ResponseUtil.getResponseWithAuth(baseURI, updateOutletRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == 200)
                {
                    LogUtils.success(logger, "Update outlet API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update outlet API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                   
                    
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
            LogUtils.exception(logger, "Error in update outlet test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in update outlet test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in update outlet test: " + e.getMessage());
        }
    }
    
    
    @DataProvider(name = "getUpdateOutletNegativeData")
    public Object[][] getUpdateOutletNegativeData() throws customException {
        try {
            LogUtils.info("Reading update outlet negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading update outlet negative test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No update outlet test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "updateoutlet".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid update outlet negative test data found after filtering";
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
            String errorMsg = "Error in getting update outlet negative test data: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, "Error in getting update outlet negative test data: " + e.getMessage());
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getUpdateOutletNegativeData")
    public void updateOutletNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting update outlet negative test case: " + testCaseid);
            ExtentReport.createTest("Update Outlet Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Verify API name and test type
            if (!apiName.equalsIgnoreCase("updateoutlet")) {
                String errorMsg = "Invalid API name: " + apiName + ". Expected 'updateoutlet'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            if (!testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid test type: " + testType + ". Expected 'negative'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

            // Set up request parameters
            updateOutletRequest = new UpdateOutletRequest();
            updateOutletRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            updateOutletRequest.setUser_id(String.valueOf(user_id));
            updateOutletRequest.setName(requestBodyJson.getString("name"));
            updateOutletRequest.setOutlet_type(requestBodyJson.getString("outlet_type"));
            updateOutletRequest.setFssainumber(requestBodyJson.getString("fssainumber"));
            updateOutletRequest.setGstnumber(requestBodyJson.getString("gstnumber"));
            updateOutletRequest.setMobile(requestBodyJson.getString("mobile"));
            updateOutletRequest.setVeg_nonveg(requestBodyJson.getString("veg_nonveg"));
            updateOutletRequest.setService_charges(requestBodyJson.getString("service_charges"));
            updateOutletRequest.setGst(requestBodyJson.getString("gst"));
            updateOutletRequest.setAddress(requestBodyJson.getString("address"));
            updateOutletRequest.setIs_open(requestBodyJson.getBoolean("is_open"));
            updateOutletRequest.setUpi_id(requestBodyJson.optString("upi_id"));
            updateOutletRequest.setWebsite(requestBodyJson.getString("website"));
            updateOutletRequest.setWhatsapp(requestBodyJson.getString("whatsapp"));
            updateOutletRequest.setFacebook(requestBodyJson.getString("facebook"));
            updateOutletRequest.setInstagram(requestBodyJson.getString("instagram"));
            updateOutletRequest.setGoogle_business_link(requestBodyJson.getString("google_business_link"));
            updateOutletRequest.setGoogle_review(requestBodyJson.getString("google_review"));

            response = ResponseUtil.getResponseWithAuth(baseURI, updateOutletRequest, httpsmethod, accessToken);

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
            } else {
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());

                // Validate response body
                actualJsonBody = new JSONObject(response.asString());

                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedJsonBody = new JSONObject(expectedResponseBody);

                    // Validate response message length
                    if (actualJsonBody.has("message")) {
                        String message = actualJsonBody.getString("message");
                        int sentenceCount = countSentences(message);
                        
                        if (sentenceCount > 6) {
                            String errorMsg = "Response message exceeds 6 sentences. Current count: " + sentenceCount;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            throw new customException(errorMsg);
                        } else {
                            LogUtils.success(logger, "Message sentence count validation passed: " + sentenceCount + " sentences");
                            ExtentReport.getTest().log(Status.PASS, "Message sentence count validation passed: " + sentenceCount + " sentences");
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
                            LogUtils.failure(logger, "Response message mismatch - Expected: " + expectedMessage + ", Actual: " + actualMessage);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Response message mismatch", ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedMessage + ", Actual: " + actualMessage);
                        }
                    }

                    // Complete response validation
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                }

                LogUtils.success(logger, "Update outlet negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update outlet negative test completed successfully", ExtentColor.GREEN));
            }

            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());

        } catch (Exception e) {
            String errorMsg = "Error in update outlet negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    // Helper method to count sentences in a message
    private int countSentences(String message) {
        if (message == null || message.trim().isEmpty()) {
            return 0;
        }
        // Split by common sentence endings and count
        String[] sentences = message.split("[.!?]+");
        return sentences.length;
    }
}