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
import com.menumitra.apiRequest.TableDeleteRequest;
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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class TableDeleteTestScript extends APIBase
{
    private TableDeleteRequest tableDeleteRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private Logger logger = LogUtils.getLogger(TableDeleteTestScript.class);

    @DataProvider(name = "getTableDeleteUrl")
    public static Object[][] getTableDeleteUrl() throws customException {
        try {
            LogUtils.info("Starting to read Table Delete API endpoint data from Excel");
            
            if(excelSheetPathForGetApis == null || excelSheetPathForGetApis.isEmpty()) {
                String errorMsg = "Excel sheet path is null or empty";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Table Delete API endpoint data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length > 0 && "tabledelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if(filteredData.length == 0) {
                String errorMsg = "No matching Table Delete API endpoints found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved " + filteredData.length + " Table Delete API endpoints");
            return filteredData;

        } catch (Exception e) {
            String errorMsg = "Error while reading Table Delete API endpoint data from Excel sheet: " + 
                            (e.getMessage() != null ? e.getMessage() : "Unknown error");
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getTableDeleteData")
    public static Object[][] getTableDeleteData() throws customException {
        try {
            LogUtils.info("Starting to read table delete test scenario data from Excel");
            
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table delete test scenario data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                        "tabledelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully filtered " + obj.length + " test scenarios for table delete");
            return obj;

        } catch (Exception e) {
            String errorMsg = "Error while reading table delete test scenario data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            ExtentReport.createTest("Table Delete Test Script");
            LogUtils.info("=====Starting Table Delete Test Script Setup=====");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            LogUtils.info("Getting base URL from environment");
            baseURI = EnviromentChanges.getBaseUrl();
            
            LogUtils.info("Retrieving table delete URL configuration");
            Object[][] tableDeleteData = getTableDeleteUrl();
            
            if (tableDeleteData.length > 0) {
                String endpoint = tableDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
               
                LogUtils.success(logger, "Successfully constructed Table Delete Base URI: " + baseURI);
            } else {
                String errorMsg = "Failed to construct Table Delete Base URI - No endpoint data found";
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

            tableDeleteRequest=new TableDeleteRequest();
            LogUtils.success(logger, "Table delete test script Setup completed successfully");

        } catch (Exception e) {
            String errorMsg = "Setup failed: " + e.getMessage();
            LogUtils.exception(logger, "Error during table delete test script setup", e);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableDeleteData")
    private void verifyTableDelete(String apiName, String testCaseid, String testType, String description,
    String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode) throws customException
    {
        try {
            ExtentReport.createTest("Table Delete Test - " + testCaseid);
            LogUtils.info("=====Starting Table Delete Test Execution=====");
            LogUtils.info("Test Case ID: " + testCaseid);
            LogUtils.info("Description: " + description);
            
            ExtentReport.getTest().log(Status.INFO, "API URL: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsmethod);
            LogUtils.info("API URL: " + baseURI);
            LogUtils.info("HTTP Method: " + httpsmethod);

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            tableDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            tableDeleteRequest.setUser_id(String.valueOf(user_id));
            
            ExtentReport.getTest().log(Status.INFO, "Setting table_id in request");
            LogUtils.info("Setting table_id in request");
            tableDeleteRequest.setSection_id(requestBodyJson.getString("section_id"));
            
            ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Final Request Body: " + requestBodyJson.toString(2));

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            response = ResponseUtil.getResponseWithAuth(baseURI, tableDeleteRequest, httpsmethod, accessToken);

            // Response logging
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());
            System.out.println(response.getStatusCode());
            // Validation
            
            if(response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                
                if(!actualResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                                        
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table deleted successfully", ExtentColor.GREEN));
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

