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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class TableCreateTestScript extends APIBase
{

    private tableCreateRequest tableCreateRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject expectedJsonBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(TableCreateTestScript.class);

    @DataProvider(name = "getTableCreateUrl")
    public Object[][] getTableCreateUrl() throws Exception {
        try {
            LogUtils.info("Reading Table Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "tablecreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Table Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Table Create API endpoint data from Excel sheet");
            throw new Exception("Error While Reading Table Create API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getTableCreateData")
    public Object[][] getTableCreateData() throws Exception {
        try {
            LogUtils.info("Reading table create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No table create test scenario data found in Excel sheet");
                throw new Exception("No table create test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "tablecreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for table create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading table create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading table create test scenario data: " + e.getMessage());
            throw new Exception(
                    "Error while reading table create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws Exception {
        try {
            LogUtils.info("====Starting setup for table create test====");
            ExtentReport.createTest("Table Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set table create URL
            Object[][] tableCreateData = getTableCreateUrl();
            if (tableCreateData.length > 0) {
                String endpoint = tableCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for table create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No table create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No table create URL found in test data");
                throw new Exception("No table create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            tableCreateRequest = new tableCreateRequest();
            LogUtils.success(logger, "Table Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Table Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during table create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during table create setup: " + e.getMessage());
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getTableCreateData")
    private void createTable(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws Exception {
        try {
            LogUtils.info("Starting table creation test case: " + testCaseid);
            ExtentReport.createTest("Table Creation Test - " + testCaseid);
            
            // Request preparation
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            tableCreateRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
            tableCreateRequest.setUser_id(String.valueOf(userId));
            tableCreateRequest.setSection_id(requestBodyJson.getString("section_id"));
            tableCreateRequest.setTable_number(requestBodyJson.getString("table_number"));
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseURI, tableCreateRequest, httpsmethod, accessToken);
            
            // Log response
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Mark test as passed
            LogUtils.success(logger, "Table creation test completed");
            ExtentReport.getTest().log(Status.PASS, "Table creation test completed");
            
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during table creation: " + e.getMessage(), e);
            ExtentReport.getTest().log(Status.FAIL, "Error during table creation: " + e.getMessage());
            throw new Exception("Error during table creation: " + e.getMessage());
        }
    }

}

