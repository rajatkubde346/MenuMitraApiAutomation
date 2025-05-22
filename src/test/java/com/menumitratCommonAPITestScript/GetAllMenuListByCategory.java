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

import com.menumitra.apiRequest.MenuRequest;
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
public class GetAllMenuListByCategory extends APIBase {
    private MenuRequest menuRequest;
    private Response response;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private JSONObject expectedResponseJson;
    private JSONObject actualJsonBody;
    private Logger logger = LogUtils.getLogger(GetAllMenuListByCategory.class);

    @DataProvider(name="getallmenulistbycategoryurl")
    private Object[][] getallmenulistbycategoryurl() throws customException {
        try {
            LogUtils.info("Reading menu list view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading menu list view URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length > 0 && "getallmenulistbycategory".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No menu list view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved menu list view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved menu list view URL data");
            return filteredData;
        } catch(Exception e) {
            String errorMsg = "Error in getMenuListViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getAllMenuListByCategoryData")
    public static Object[][] getAllMenuListByCategoryData() throws customException {
        try {
            LogUtils.info("Reading getAllMenuListByCategory test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading getAllMenuListByCategory test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No getAllMenuListByCategory test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "getallmenulistbycategory".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No positive test scenarios found for getAllMenuListByCategory";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] result = filteredData.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + result.length + " test scenarios for getAllMenuListByCategory");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved test scenarios");
            return result;
        } catch (Exception e) {
            String errorMsg = "Error while reading getAllMenuListByCategory test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("Menu List View SetUp");
            ExtentReport.createTest("Menu List View SetUp");
            ExtentReport.getTest().log(Status.INFO, "Menu List View SetUp");

            // Login and verify OTP
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            if (baseURI == null || baseURI.trim().isEmpty()) {
                throw new customException("Base URL is null or empty");
            }
            
            // Get endpoint URL
            Object[][] getUrl = getallmenulistbycategoryurl();
            if (getUrl.length > 0 && getUrl[0].length > 2) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                throw new customException("Invalid URL data structure in Excel sheet");
            }
            
            // Get access token
            accessToken = TokenManagers.getJwtToken();
            if(accessToken == null || accessToken.trim().isEmpty()) {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                if(accessToken == null || accessToken.trim().isEmpty()) {
                    throw new customException("Failed to obtain access token after retry");
                }
            }
            
            menuRequest = new MenuRequest();
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error during GetAllMenuListByCategory setup: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getAllMenuListByCategoryData")
    private void getAllMenuListByCategory(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting GetAllMenuListByCategory test case: " + testCaseid);
            ExtentReport.createTest("GetAllMenuListByCategory Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Validate input parameters
            if (requestBodyPayload == null || requestBodyPayload.trim().isEmpty()) {
                throw new customException("Request body payload is null or empty");
            }
            
            // Parse and validate request body
            requestBodyJson = new JSONObject(requestBodyPayload);
            if (!requestBodyJson.has("outlet_id")) {
                throw new customException("outlet_id is required in request body");
            }
            
            String outletId = requestBodyJson.getString("outlet_id");
            if (outletId == null || outletId.trim().isEmpty()) {
                throw new customException("outlet_id cannot be empty");
            }
            
            // Set request parameters
            menuRequest.setOutlet_id(outletId);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Make API call
            response = ResponseUtil.getResponseWithAuth(baseURI, menuRequest, httpsmethod, accessToken);
            
            // Log response
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asPrettyString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());

            // Validate response
            int expectedStatusCode = Integer.parseInt(statusCode);
            if (response.getStatusCode() == expectedStatusCode) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                // Validate response body if expected response is provided
                if (expectedResponseBody != null && !expectedResponseBody.trim().isEmpty()) {
                    actualJsonBody = new JSONObject(response.asString());
                    expectedResponseJson = new JSONObject(expectedResponseBody);
                    
                    // Validate specific fields
                    if (expectedResponseJson.has("detail") && actualJsonBody.has("detail")) {
                        String expectedDetail = expectedResponseJson.getString("detail");
                        String actualDetail = actualJsonBody.getString("detail");
                        
                        if (!expectedDetail.equals(actualDetail)) {
                            throw new customException("Response detail mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                        }
                    }
                }
                
                ExtentReport.getTest().log(Status.PASS, 
                    MarkupHelper.createLabel("GetAllMenuListByCategory test completed successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "GetAllMenuListByCategory test completed successfully");
            } else {
                String errorMsg = "Status code validation failed - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
}
