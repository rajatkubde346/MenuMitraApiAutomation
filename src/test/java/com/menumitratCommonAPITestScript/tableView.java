package com.menumitratCommonAPITestScript;



import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

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

@Listeners(Listener.class)
public class tableView extends APIBase
{
	
    private tableCreateRequest tableViewRequest;
    private io.restassured.response.Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private Logger logger = LogUtils.getLogger(tableView.class);

    @DataProvider(name = "getTableViewUrl")
    public  Object[][] getTableViewUrl() throws customException {
    	try {
            LogUtils.info("=====Reading Login API Endpoint Data=====");
            ExtentReport.getTest().log(Status.INFO, "Reading Login API endpoint configuration");
            Object[][] apiData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            Object[][] filteredData = Arrays.stream(apiData)
                    .filter(row -> "tableview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            LogUtils.success(logger, "Successfully retrieved Login API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully loaded Login API configuration");
            return filteredData;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Login API endpoint data", e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed to read Login API endpoint data: " + e.getMessage(), ExtentColor.RED));
            throw new customException("Error reading Login API endpoint data: " + e.getMessage());
        }
    }

    @DataProvider(name = "getTableViewData")
    public static Object[][] getTableViewData() throws customException {
        try {
            LogUtils.info("Reading positive table view test scenario data");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table view test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : testData) {
                if (row != null && row.length >= 3 &&
                    "tableview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive table view test cases found in test data";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive table view test scenarios");
            return positiveTestData;

        } catch (Exception e) {
            String errorMsg = "Error while reading positive table view test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            ExtentReport.createTest("Table View Test Script");
            LogUtils.info("=====Starting Table View Test Script Setup=====");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            LogUtils.info("Getting base URL from environment");
            baseURI = EnviromentChanges.getBaseUrl();
            
            LogUtils.info("Retrieving table view URL configuration");
            Object[][] tableViewData = getTableViewUrl();
            
            if (tableViewData.length > 0) {
                String endpoint = tableViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
               
                LogUtils.success(logger, "Successfully constructed Table View Base URI: " + baseURI);
            } else {
                String errorMsg = "Failed to construct Table View Base URI - No endpoint data found";
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

            tableViewRequest = new tableCreateRequest();
            LogUtils.success(logger, "Table view test script Setup completed successfully");

        } catch (Exception e) {
            String errorMsg = "Setup failed: " + e.getMessage();
            LogUtils.exception(logger, "Error during table view test script setup", e);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableViewData")
    private void verifyTableView(String apiName, String testCaseid, String testType, String description,
    String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode) throws customException
    {
        try {
            ExtentReport.createTest("Table View Test - " + testCaseid);
            LogUtils.info("=====Starting Table View Test Execution=====");
            LogUtils.info("Test Case ID: " + testCaseid);
            LogUtils.info("Description: " + description);
            
            ExtentReport.getTest().log(Status.INFO, "API URL: " + baseURI);
            
            LogUtils.info("API URL: " + baseURI);
            

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            tableViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            tableViewRequest.setTable_number(requestBodyJson.getString("table_number"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting section_id in request");
            LogUtils.info("Setting section_id in request");
            tableViewRequest.setSection_id(requestBodyJson.getString("section_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Final Request Body: " + requestBodyJson.toString(2));

            
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            response = ResponseUtil.getResponseWithAuth(baseURI,tableViewRequest,httpsmethod,accessToken);
            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validation
            if(response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                
                if(!actualResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                    LogUtils.info("Starting response body validation");
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    //                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table viewed successfully", ExtentColor.GREEN));
                } else {
                    ExtentReport.getTest().log(Status.INFO, "Response body is empty");
                    LogUtils.info("Response body is empty");
                }
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
                throw new customException(errorMsg);
            }

        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            LogUtils.error("Stack trace: " + Arrays.toString(e.getStackTrace()));
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
}

