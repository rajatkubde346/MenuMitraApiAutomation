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
import com.menumitra.apiRequest.ForceCancelOrderRequest;
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
public class ForceCancelOrderTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private ForceCancelOrderRequest forceCancelOrderRequest;
    private URL url;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(ForceCancelOrderTestScript.class);
    private int user_id;
    private JSONObject expectedJsonBody;
    @BeforeClass
    private void forceCancelOrderSetUp() throws customException
    {
        try
        {
            LogUtils.info("Force Cancel Order SetUp");
            ExtentReport.createTest("Force Cancel Order SetUp");
            ExtentReport.getTest().log(Status.INFO,"Force Cancel Order SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getForceCancelOrderUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No force cancel order URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No force cancel order URL found in test data");
                throw new customException("No force cancel order URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            forceCancelOrderRequest = new ForceCancelOrderRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in force cancel order setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in force cancel order setup: " + e.getMessage());
            throw new customException("Error in force cancel order setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getForceCancelOrderUrl")
    private Object[][] getForceCancelOrderUrl() throws customException {
        try {
            LogUtils.info("Reading Force Cancel Order API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Force Cancel Order API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Force Cancel Order API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "forcecancelorder".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No force cancel order URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting force cancel order URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting force cancel order URL: " + e.getMessage());
            throw new customException("Error in getting force cancel order URL: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getForceCancelOrderData")
    public Object[][] getForceCancelOrderData() throws customException {
        try {
            LogUtils.info("Reading force cancel order test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading force cancel order test scenario data");
            
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
                        "forcecancelorder".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid force cancel order test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting force cancel order test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting force cancel order test data: " + e.getMessage());
            throw new customException("Error in getting force cancel order test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getForceCancelOrderData")
    public void forceCancelOrderTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting force cancel order test case: " + testCaseid);
            ExtentReport.createTest("Force Cancel Order Test - " + testCaseid);
            
            requestBodyJson = new JSONObject(requestBody);
            forceCancelOrderRequest.setOrder_id(requestBodyJson.getString("order_id"));
            forceCancelOrderRequest.setUser_id(String.valueOf(user_id));
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseURI, forceCancelOrderRequest, httpsmethod, accessToken);
            
            // Log response
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Mark test as passed
            LogUtils.success(logger, "Force cancel order test completed");
            ExtentReport.getTest().log(Status.PASS, "Force cancel order test completed");
            
        } catch (Exception e) {
            String errorMsg = "Error in force cancel order test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
}
