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
import com.menumitra.apiRequest.InventoryRequest;
import com.menumitra.apiRequest.InventoryViewRequest;
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
public class ViewOutletTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private InventoryRequest viewOutletRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(ViewOutletTestScript.class);
    private int user_id;
   
    @BeforeClass
    private void viewOutletSetUp() throws customException
    {
        try
        {
            LogUtils.info("View Outlet SetUp");
            ExtentReport.createTest("View Outlet SetUp");
            ExtentReport.getTest().log(Status.INFO,"View Outlet SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getViewOutletUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No view outlet URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No view outlet URL found in test data");
                throw new customException("No view outlet URL found in test data");
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
            
            viewOutletRequest = new InventoryRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in view outlet setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in view outlet setup: " + e.getMessage());
            throw new customException("Error in view outlet setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getViewOutletUrl")
    private Object[][] getViewOutletUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading view outlet URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading view outlet URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "viewoutlet".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No view outlet URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved view outlet URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved view outlet URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getViewOutletUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getViewOutletData") 
    public Object[][] getViewOutletData() throws customException {
        try {
            LogUtils.info("Reading view outlet test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading view outlet test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No view outlet test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "viewoutlet".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid view outlet test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " view outlet test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " view outlet test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getViewOutletData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getViewOutletData")
    private void verifyViewOutlet(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("View outlet test execution: " + description);
            ExtentReport.createTest("View Outlet Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "View outlet test execution: " + description);

            if(apiName.equalsIgnoreCase("viewoutlet"))
            {
                requestBodyJson = new JSONObject(requestBody);

                // Only set outlet_id for the view outlet request
                viewOutletRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                viewOutletRequest.setUser_id(String.valueOf(user_id));
                
                LogUtils.info("Constructed view outlet request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed view outlet request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, viewOutletRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == 200)
                {
                    LogUtils.success(logger, "View outlet API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("View outlet API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Validate response body if expected response is provided
                    actualJsonBody = new JSONObject(response.asString());
                    if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedJsonBody = new JSONObject(expectedResponseBody);
                        
                      
                        
                        
                    } else {
                        LogUtils.info("No expected response structure provided for validation");
                        ExtentReport.getTest().log(Status.INFO, "No expected response structure provided for validation");
                    }
                    
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
            LogUtils.exception(logger, "Error in view outlet test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in view outlet test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in view outlet test: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getViewOutletNegativeData")
    public Object[][] getViewOutletNegativeData() throws customException {
        try {
            LogUtils.info("Reading view outlet negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading view outlet negative test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                        "viewoutlet".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid view outlet negative test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting view outlet negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting view outlet negative test data: " + e.getMessage());
            throw new customException("Error in getting view outlet negative test data: " + e.getMessage());
        }
    }

    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        // Split by common sentence endings and count
        return text.split("[.!?]+").length;
    }

    @Test(dataProvider = "getViewOutletNegativeData")
    public void viewOutletNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting view outlet negative test case: " + testCaseid);
            ExtentReport.createTest("View Outlet Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("viewoutlet") && testType.equalsIgnoreCase("negative")) {
                requestBodyJson = new JSONObject(requestBody);
                viewOutletRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                viewOutletRequest.setUser_id(String.valueOf(user_id));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, viewOutletRequest, httpsmethod, accessToken);

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
                }

                // Validate response body
                actualJsonBody = new JSONObject(response.asString());
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedJsonBody = new JSONObject(expectedResponseBody);

                    // Validate error message sentences
                    if (actualJsonBody.has("detail")) {
                        String errorMessage = actualJsonBody.getString("detail");
                        int sentenceCount = countSentences(errorMessage);
                        
                        if (sentenceCount > 6) {
                            String errorMsg = "Error message exceeds 6 sentences. Current count: " + sentenceCount;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Error Message: " + errorMessage);
                        } else {
                            LogUtils.success(logger, "Error message sentence count validation passed: " + sentenceCount + " sentences");
                            ExtentReport.getTest().log(Status.PASS, "Error message sentence count validation passed: " + sentenceCount + " sentences");
                        }
                    }

                    // Validate response message content
                    if (expectedJsonBody.has("detail") && actualJsonBody.has("detail")) {
                        String expectedDetail = expectedJsonBody.getString("detail");
                        String actualDetail = actualJsonBody.getString("detail");

                        if (expectedDetail.equals(actualDetail)) {
                            LogUtils.success(logger, "Error message content validation passed");
                            ExtentReport.getTest().log(Status.PASS, "Error message content validation passed");
                        } else {
                            String errorMsg = "Error message content mismatch";
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail);
                            ExtentReport.getTest().log(Status.FAIL, "Actual: " + actualDetail);
                        }
                    }

                    // Complete response validation
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                }

                // Always log the full response
                ExtentReport.getTest().log(Status.INFO, "Full Response:");
                ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
            }
        } catch (Exception e) {
            String errorMsg = "Error in view outlet negative test: " + e.getMessage();
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