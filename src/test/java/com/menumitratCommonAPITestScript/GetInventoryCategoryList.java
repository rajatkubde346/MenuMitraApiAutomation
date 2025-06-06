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
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Listeners(Listener.class)
public class GetInventoryCategoryList extends APIBase
{
    private Response response;
    private String baseURI;
    private String accessToken;
    private URL url;
    private Logger logger = LogUtils.getLogger(GetInventoryCategoryList.class);

    /**
     * Data provider for inventory category list API endpoint URLs
     */
    @DataProvider(name = "getInventoryCategoryListUrl")
    public static Object[][] getInventoryCategoryListUrl() throws customException {
        try {
            LogUtils.info("Reading Inventory Category List API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "getinventorycategorylist".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Inventory Category List API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Inventory Category List API endpoint data from Excel sheet");
            throw new customException("Error While Reading Inventory Category List API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for inventory category list test scenarios
     */
    @DataProvider(name = "getInventoryCategoryListData")
    public static Object[][] getInventoryCategoryListData() throws customException {
        try {
            LogUtils.info("Reading inventory category list test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No inventory category list test scenario data found in Excel sheet");
                throw new customException("No inventory category list test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "getinventorycategorylist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for inventory category list");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading inventory category list test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading inventory category list test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading inventory category list test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for inventory category list test====");
            ExtentReport.createTest("Inventory Category List Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set inventory category list URL
            Object[][] inventoryCategoryListData = getInventoryCategoryListUrl();
            if (inventoryCategoryListData.length > 0) {
                String endpoint = inventoryCategoryListData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for inventory category list: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No inventory category list URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No inventory category list URL found in test data");
                throw new customException("No inventory category list URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            LogUtils.success(logger, "Inventory Category List Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory Category List Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during inventory category list setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during inventory category list setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to get inventory category list
     */
    @Test(dataProvider = "getInventoryCategoryListData")
    private void getInventoryCategoryList(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory category list test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Category List Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Check if request body has outlet_id to append as query parameter
            String completeUrl = baseURI;
            if (requestBodyPayload != null && !requestBodyPayload.isEmpty()) {
                JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
                if (requestBodyJson.has("outlet_id")) {
                    String outletId = requestBodyJson.getString("outlet_id");
                    if (completeUrl.contains("?")) {
                        completeUrl += "&outlet_id=" + outletId;
                    } else {
                        completeUrl += "?outlet_id=" + outletId;
                    }
                }
            }
            
            LogUtils.info("Final URL: " + completeUrl);
            ExtentReport.getTest().log(Status.INFO, "Final URL: " + completeUrl);
            
            // API call (GET method, no payload needed)
            LogUtils.info("Making GET request to endpoint: " + completeUrl);
            ExtentReport.getTest().log(Status.INFO, "Making GET request to endpoint: " + completeUrl);
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            
            response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get(completeUrl)
                    .then()
                    .log().all()
                    .extract()
                    .response();
            
            // Response logging
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asPrettyString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());

            // Validation of status code only
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                
                // Print response body on report without validation
                if (response.asString() != null && !response.asString().isEmpty()) {
                    ExtentReport.getTest().log(Status.PASS, "Response Body:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    LogUtils.info("Response Body displayed on report successfully");
                } else {
                    ExtentReport.getTest().log(Status.INFO, "Response body is empty");
                    LogUtils.info("Response body is empty");
                }
                
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory category list retrieved successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Inventory category list retrieved successfully");
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
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
}