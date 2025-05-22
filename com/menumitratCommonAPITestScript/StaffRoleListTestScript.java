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
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class StaffRoleListTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private WaiterRequest staffRoleListRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private JSONObject expectedJsonBody;
    Logger logger = LogUtils.getLogger(StaffRoleListTestScript.class);
   
    @BeforeClass
    private void staffRoleListSetUp() throws customException
    {
        try
        {
            LogUtils.info("Staff Role List SetUp");
            ExtentReport.createTest("Staff Role List SetUp");
            ExtentReport.getTest().log(Status.INFO,"Staff Role List SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getStaffRoleListUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No staff role list URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No staff role list URL found in test data");
                throw new customException("No staff role list URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            staffRoleListRequest = new WaiterRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in staff role list setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in staff role list setup: " + e.getMessage());
            throw new customException("Error in staff role list setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getStaffRoleListUrl")
    private Object[][] getStaffRoleListUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Role List API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Staff Role List API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Staff Role List API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "staffrolelist".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No staff role list URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting staff role list URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting staff role list URL: " + e.getMessage());
            throw new customException("Error in getting staff role list URL: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getStaffRoleListData")
    public Object[][] getStaffRoleListData() throws customException {
        try {
            LogUtils.info("Reading staff role list test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading staff role list test scenario data");
            
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
                        "staffrolelist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid staff role list test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting staff role list test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting staff role list test data: " + e.getMessage());
            throw new customException("Error in getting staff role list test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getStaffRoleListData")
    public void staffRoleListTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff role list test case: " + testCaseid);
            ExtentReport.createTest("Staff Role List Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("staffrolelist")) {
                requestBodyJson = new JSONObject(requestBody);
                staffRoleListRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, staffRoleListRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Validate status code
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                
                // Validate response body
                actualJsonBody = new JSONObject(response.asString());
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedJsonBody = new JSONObject(expectedResponseBody);
                    boolean isValid = validateResponseBody.validate(expectedJsonBody, actualJsonBody);
                    
                    if (isValid) {
                        LogUtils.success(logger, "Response body validation passed");
                        ExtentReport.getTest().log(Status.PASS, "Response body validation passed");
                    } else {
                        String errorMsg = "Response body validation failed";
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        throw new customException(errorMsg);
                    }
                }
                
                LogUtils.info("Staff role list response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Staff role list response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                
                LogUtils.success(logger, "Staff role list test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff role list test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in staff role list test: " + e.getMessage();
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