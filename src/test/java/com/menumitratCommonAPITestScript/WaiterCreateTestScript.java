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
public class WaiterCreateTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private WaiterRequest waiterCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(WaiterCreateTestScript.class);
   
    @BeforeClass
    private void waiterCreateSetUp() throws customException
    {
        try
        {
            LogUtils.info("Waiter Create SetUp");
            ExtentReport.createTest("Waiter Create SetUp");
            ExtentReport.getTest().log(Status.INFO,"Waiter Create SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getWaiterCreateUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No waiter create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No waiter create URL found in test data");
                throw new customException("No waiter create URL found in test data");
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
            
            waiterCreateRequest = new WaiterRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in waiter create setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in waiter create setup: " + e.getMessage());
            throw new customException("Error in waiter create setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getWaiterCreateUrl")
    private Object[][] getWaiterCreateUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading waiter create URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading waiter create URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "waitercreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No waiter create URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved waiter create URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved waiter create URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getWaiterCreateUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getWaiterCreateData") 
    public Object[][] getWaiterCreateData() throws customException {
        try {
            LogUtils.info("Reading waiter create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading waiter create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No waiter create test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "waitercreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid waiter create test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " waiter create test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " waiter create test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getWaiterCreateData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getWaiterCreateData")
    private void verifyWaiterCreate(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Waiter create test execution: " + description);
            ExtentReport.createTest("Waiter Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Waiter create test execution: " + description);

            if(apiName.equalsIgnoreCase("waitercreate"))
            {
                requestBodyJson = new JSONObject(requestBody);

                waiterCreateRequest.setUser_id(String.valueOf(user_id));
                waiterCreateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                waiterCreateRequest.setName(requestBodyJson.getString("name"));
                waiterCreateRequest.setMobile(requestBodyJson.getString("mobile"));
                waiterCreateRequest.setAddress(requestBodyJson.optString("address"));
                waiterCreateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                waiterCreateRequest.setDob(requestBodyJson.optString("dob"));
                waiterCreateRequest.setEmail(requestBodyJson.optString("email"));
                
                LogUtils.info("Constructed waiter create request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed waiter create request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, waiterCreateRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == 200)
                {
                    LogUtils.success(logger, "Waiter create API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Waiter create API executed successfully", ExtentColor.GREEN));
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
            LogUtils.exception(logger, "Error in waiter create test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in waiter create test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in waiter create test: " + e.getMessage());
        }
    }
}
