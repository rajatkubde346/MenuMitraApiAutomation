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
import com.menumitra.apiRequest.TableListViewRequest;
import com.menumitra.apiRequest.tableCreateRequest;
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
public class TableListViewTestScript extends APIBase
{
    private TableListViewRequest tablelistviewrequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private Logger logger = LogUtils.getLogger(TableListViewTestScript.class);

    @DataProvider(name = "getTableListViewUrl")
    public static Object[][] getTableListViewUrl() throws customException {
        try {
            LogUtils.info("Starting to read Table List View API endpoint data from Excel");
            
            if(excelSheetPathForGetApis == null || excelSheetPathForGetApis.isEmpty()) {
                String errorMsg = "Excel sheet path is null or empty";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Table List View API endpoint data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length > 0 && "tablelistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if(filteredData.length == 0) {
                String errorMsg = "No matching Table List View API endpoints found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved " + filteredData.length + " Table List View API endpoints");
            return filteredData;

        } catch (Exception e) {
            String errorMsg = "Error while reading Table List View API endpoint data from Excel sheet: " + 
                            (e.getMessage() != null ? e.getMessage() : "Unknown error");
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getTableListViewData")
    public static Object[][] getTableListViewData() throws customException {
        try {
            LogUtils.info("Starting to read table list view test scenario data from Excel");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table list view test scenario data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                        "tablelistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully filtered " + obj.length + " test scenarios for table list view");
            return obj;

        } catch (Exception e) {
            String errorMsg = "Error while reading table list view test scenario data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            ExtentReport.createTest("Table List View Test Script");
            LogUtils.info("=====Starting Table List View Test Script Setup=====");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            LogUtils.info("Getting base URL from environment");
            baseURI = EnviromentChanges.getBaseUrl();
            
            LogUtils.info("Retrieving table list view URL configuration");
            Object[][] tableListViewData = getTableListViewUrl();
            
            if (tableListViewData.length > 0) {
                String endpoint = tableListViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
               
                LogUtils.success(logger, "Successfully constructed Table List View Base URI: " + baseURI);
            } else {
                String errorMsg = "Failed to construct Table List View Base URI - No endpoint data found";
                LogUtils.failure(logger, errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Retrieving authentication tokens");
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                String errorMsg = "Authentication failed - Required tokens not found. Please verify login and OTP verification";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            tablelistviewrequest = new TableListViewRequest();
            LogUtils.success(logger, "Table list view test script Setup completed successfully");

        } catch (Exception e) {
            String errorMsg = "Setup failed: " + e.getMessage();
            LogUtils.exception(logger, "Error during table list view test script setup", e);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableListViewData")
    private void verifyTableListView(String apiName, String testCaseid, String testType, String description,
    String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode) throws customException
    {
        try {
            LogUtils.info("Starting table list view test - TestCase ID: " + testCaseid);
            ExtentReport.createTest("Table List View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsmethod);
            
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            tablelistviewrequest.setOutlet_id(String.valueOf(requestBodyJson.getString("outlet_id")));
            
            response = ResponseUtil.getResponseWithAuth(baseURI, tablelistviewrequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Actual Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Expected Response Status Code: " + statusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + response.asString());
            
            if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
            }
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Validate status code
            if (response.getStatusCode() != expectedStatusCode) {
                String errorMsg = "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                throw new customException(errorMsg);
            }
            
            LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
            
            // Validate response body
            actualResponseBody = new JSONObject(response.asString());
            
            if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                expectedResponse = new JSONObject(expectedResponseBody);
                validateResponseBody.handleResponseBody(response, expectedResponse);
            }
            
            LogUtils.success(logger, "Table list view test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table list view test completed successfully", ExtentColor.GREEN));
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        } catch (Exception e) {
            String errorMsg = "Error in table list view test: " + e.getMessage();
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
