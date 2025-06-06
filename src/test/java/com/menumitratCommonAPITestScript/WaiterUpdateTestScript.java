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
import com.menumitra.apiRequest.WaiterRequest;
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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class WaiterUpdateTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private WaiterRequest waiterUpdateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(WaiterUpdateTestScript.class);
   
    @BeforeClass
    private void waiterUpdateSetUp() throws customException
    {
        try
        {
            LogUtils.info("Waiter Update SetUp");
            ExtentReport.createTest("Waiter Update SetUp");
            ExtentReport.getTest().log(Status.INFO,"Waiter Update SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getWaiterUpdateUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No waiter update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No waiter update URL found in test data");
                throw new customException("No waiter update URL found in test data");
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
            
            waiterUpdateRequest = new WaiterRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in waiter update setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in waiter update setup: " + e.getMessage());
            throw new customException("Error in waiter update setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getWaiterUpdateUrl")
    private Object[][] getWaiterUpdateUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading waiter update URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading waiter update URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "waiterupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No waiter update URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved waiter update URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved waiter update URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getWaiterUpdateUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getWaiterUpdateData") 
    public Object[][] getWaiterUpdateData() throws customException {
        try {
            LogUtils.info("Reading waiter update test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading waiter update test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No waiter update test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "waiterupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid waiter update test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " waiter update test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " waiter update test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getWaiterUpdateData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getWaiterUpdateData")
    private void verifyWaiterUpdate(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Waiter update test execution: " + description);
            ExtentReport.createTest("Waiter Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Waiter update test execution: " + description);

            if(apiName.equalsIgnoreCase("waiterupdate"))
            {
                requestBodyJson = new JSONObject(requestBody);

                waiterUpdateRequest.setUser_id(requestBodyJson.getString("user_id"));
                waiterUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                waiterUpdateRequest.setupdate_user_id(String.valueOf(user_id));
                waiterUpdateRequest.setName(requestBodyJson.getString("name"));
                waiterUpdateRequest.setMobile(requestBodyJson.getString("mobile"));
                waiterUpdateRequest.setAddress(requestBodyJson.optString("address", ""));
                waiterUpdateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                waiterUpdateRequest.setDob(requestBodyJson.optString("dob"));
                waiterUpdateRequest.setEmail(requestBodyJson.optString("email"));
                
                LogUtils.info("Constructed waiter update request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed waiter update request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, waiterUpdateRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == 200)
                {
                    LogUtils.success(logger, "Waiter update API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Waiter update API executed successfully", ExtentColor.GREEN));
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
            LogUtils.exception(logger, "Error in waiter update test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in waiter update test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in waiter update test: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getWaiterUpdateNegativeData")
    public Object[][] getWaiterUpdateNegativeData() throws customException {
        try {
            LogUtils.info("Reading waiter update negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading waiter update negative test scenario data");

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
                    "waiterupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid waiter update negative test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting waiter update negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting waiter update negative test data: " + e.getMessage());
            throw new customException("Error in getting waiter update negative test data: " + e.getMessage());
        }
    }

    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        // Split by common sentence endings and count
        return text.split("[.!?]+").length;
    }

    @Test(dataProvider = "getWaiterUpdateNegativeData")
    public void waiterUpdateNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting waiter update negative test case: " + testCaseid);
            ExtentReport.createTest("Waiter Update Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Validate API name and test type
            if (!"waiterupdate".equalsIgnoreCase(apiName)) {
                String errorMsg = "Invalid API name: " + apiName + ". Expected: waiterupdate";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            if (!"negative".equalsIgnoreCase(testType)) {
                String errorMsg = "Invalid test type: " + testType + ". Expected: negative";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            requestBodyJson = new JSONObject(requestBody);
            waiterUpdateRequest.setUser_id(requestBodyJson.getString("user_id"));
            waiterUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            waiterUpdateRequest.setupdate_user_id(String.valueOf(user_id));
            waiterUpdateRequest.setName(requestBodyJson.getString("name"));
            waiterUpdateRequest.setMobile(requestBodyJson.getString("mobile"));
            waiterUpdateRequest.setAddress(requestBodyJson.optString("address", ""));
            waiterUpdateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
            waiterUpdateRequest.setDob(requestBodyJson.optString("dob", ""));
            waiterUpdateRequest.setEmail(requestBodyJson.optString("email", ""));

            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

            response = ResponseUtil.getResponseWithAuth(baseURI, waiterUpdateRequest, httpsmethod, accessToken);

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
                throw new customException(errorMsg);
            }

            // Validate response body
            actualJsonBody = new JSONObject(response.asString());
            if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                JSONObject expectedResponseJson = new JSONObject(expectedResponseBody);

                // Validate response message length
                if (actualJsonBody.has("detail")) {
                    String detail = actualJsonBody.getString("detail");
                    int sentenceCount = countSentences(detail);
                    
                    if (sentenceCount > 6) {
                        String errorMsg = "Response message exceeds maximum allowed sentences. Found: " + sentenceCount + ", Maximum allowed: " + 6;
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        throw new customException(errorMsg);
                    }
                    
                    LogUtils.info("Response message sentence count: " + sentenceCount);
                    ExtentReport.getTest().log(Status.INFO, "Response message sentence count: " + sentenceCount);
                }

                // Validate response message content
                if (expectedResponseJson.has("detail") && actualJsonBody.has("detail")) {
                    String expectedDetail = expectedResponseJson.getString("detail");
                    String actualDetail = actualJsonBody.getString("detail");

                    if (!expectedDetail.equals(actualDetail)) {
                        String errorMsg = "Response message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail;
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        throw new customException(errorMsg);
                    }
                }
            }

            LogUtils.success(logger, "Waiter update negative test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Waiter update negative test completed successfully", ExtentColor.GREEN));

        } catch (Exception e) {
            String errorMsg = "Error in waiter update negative test: " + e.getMessage();
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