package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.JoinTablesRequest;
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
public class JoinTablesTestScript extends APIBase
{
    private JoinTablesRequest joinTablesRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(JoinTablesTestScript.class);

    @DataProvider(name = "getJoinTablesUrl")
    public static Object[][] getJoinTablesUrl() throws customException {
        try {
            LogUtils.info("Reading Join Tables API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "jointables".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Join Tables API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Join Tables API endpoint data from Excel sheet");
            throw new customException("Error While Reading Join Tables API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getJoinTablesData")
    public static Object[][] getJoinTablesData() throws customException {
        try {
            LogUtils.info("Reading join tables test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No join tables test scenario data found in Excel sheet");
                throw new customException("No join tables test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "jointables".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for join tables");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading join tables test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading join tables test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading join tables test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for join tables test====");
            ExtentReport.createTest("Join Tables Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set join tables URL
            Object[][] joinTablesData = getJoinTablesUrl();
            if (joinTablesData.length > 0) {
                String endpoint = joinTablesData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for join tables: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No join tables URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No join tables URL found in test data");
                throw new customException("No join tables URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            joinTablesRequest = new JoinTablesRequest();
            LogUtils.success(logger, "Join Tables Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Join Tables Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during join tables setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during join tables setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getJoinTablesData")
    private void joinTables(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting join tables test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Join Tables Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            joinTablesRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            joinTablesRequest.setSection_id(requestBodyJson.getInt("section_id"));
            joinTablesRequest.setPrimary_table_id(requestBodyJson.getInt("primary_table_id"));
            joinTablesRequest.setUser_id(requestBodyJson.getInt("user_id"));
            
            // Handle tables_to_join array
            JSONArray tablesArray = requestBodyJson.getJSONArray("tables_to_join");
            List<Integer> tablesToJoin = new ArrayList<>();
            for (int i = 0; i < tablesArray.length(); i++) {
                tablesToJoin.add(tablesArray.getInt(i));
            }
            joinTablesRequest.setTables_to_join(tablesToJoin);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            
            response = ResponseUtil.getResponseWithAuth(baseURI, joinTablesRequest, httpsmethod, accessToken);
            
            // Log response details
            LogUtils.info("Response received");
            ExtentReport.getTest().log(Status.INFO, "Response received");
            
            if (response.asString() != null && !response.asString().isEmpty()) {
                actualResponseBody = new JSONObject(response.asString());
                LogUtils.info("Response Body: " + actualResponseBody.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
            }
            
            LogUtils.success(logger, "Join tables test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Join tables test completed successfully", ExtentColor.GREEN));
            ExtentReport.getTest().log(Status.PASS, "Full Response: " + response.asPrettyString());
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error during join tables test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during join tables test: " + e.getMessage());
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error during join tables test: " + e.getMessage());
        }
    }
}
