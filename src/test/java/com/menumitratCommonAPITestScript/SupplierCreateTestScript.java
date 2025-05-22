package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
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
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class SupplierCreateTestScript extends APIBase
{
    private com.menumitra.apiRequest.SupplierRequest supplierCreateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(SupplierCreateTestScript.class);

    /**
     * Data provider for supplier create API endpoint URLs
     */
    @DataProvider(name = "getSupplierCreateUrl")
    public static Object[][] getSupplierCreateUrl() throws Exception {
        try {
            LogUtils.info("Reading Supplier Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "supplierCreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Supplier Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Supplier Create API endpoint data from Excel sheet");
            throw new Exception("Error While Reading Supplier Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for supplier create test scenarios
     */
    @DataProvider(name = "getSupplierCreateData")
    public static Object[][] getSupplierCreateData() throws Exception {
        try {
            LogUtils.info("Reading supplier create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No supplier create test scenario data found in Excel sheet");
                throw new Exception("No supplier create test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "suppliercreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for supplier create");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading supplier create test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading supplier create test scenario data: " + e.getMessage());
            throw new Exception(
                    "Error while reading supplier create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws Exception {
        try {
            LogUtils.info("====Starting setup for supplier create test====");
            ExtentReport.createTest("Supplier Create Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set supplier create URL
            Object[][] supplierCreateData = getSupplierCreateUrl();
            if (supplierCreateData.length > 0) {
                String endpoint = supplierCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for supplier create: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No supplier create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No supplier create URL found in test data");
                throw new Exception("No supplier create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            supplierCreateRequest = new com.menumitra.apiRequest.SupplierRequest();
            LogUtils.success(logger, "Supplier Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Supplier Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during supplier create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during supplier create setup: " + e.getMessage());
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create supplier
     */
    @Test(dataProvider = "getSupplierCreateData")
    private void createSupplier(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws Exception {
        try {
            LogUtils.info("Starting supplier creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Supplier Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            JSONObject requestBodyJson = new JSONObject(requestBodyPayload);
            
            ExtentReport.getTest().log(Status.INFO, "Setting outlet_id in request");
            LogUtils.info("Setting outlet_id in request");
            supplierCreateRequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
            
            ExtentReport.getTest().log(Status.INFO, "Setting user_id in request: " + user_id);
            LogUtils.info("Setting user_id in request: " + user_id);
            supplierCreateRequest.setUser_id(String.valueOf(user_id));
            
            ExtentReport.getTest().log(Status.INFO, "Setting supplier name in request");
            LogUtils.info("Setting supplier name in request");
            supplierCreateRequest.setName(requestBodyJson.getString("name"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting credit limit in request");
            LogUtils.info("Setting credit limit in request");
            supplierCreateRequest.setCredit_limit(requestBodyJson.getString("credit_limit"));
            supplierCreateRequest.setCredit_rating(requestBodyJson.getString("credit_rating"));
            ExtentReport.getTest().log(Status.INFO, "Setting location in request");
            LogUtils.info("Setting location in request");
            supplierCreateRequest.setLocation(requestBodyJson.getString("location"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting owner name in request");
            LogUtils.info("Setting owner name in request");
            supplierCreateRequest.setOwner_name(requestBodyJson.getString("owner_name"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting website in request");
            LogUtils.info("Setting website in request");
            supplierCreateRequest.setWebsite(requestBodyJson.getString("website"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting mobile numbers in request");
            LogUtils.info("Setting mobile numbers in request");
            supplierCreateRequest.setMobile_number1(requestBodyJson.getString("mobile_number1"));
            supplierCreateRequest.setMobille_number2(requestBodyJson.getString("mobille_number2"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting address in request");
            LogUtils.info("Setting address in request");
            supplierCreateRequest.setAddress(requestBodyJson.getString("address"));
            
            ExtentReport.getTest().log(Status.INFO, "Setting supplier status in request");
            LogUtils.info("Setting supplier status in request");
            supplierCreateRequest.setSupplier_status(requestBodyJson.getString("supplier_status"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseURI, supplierCreateRequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            // Report actual vs expected status code
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
            
            if (response.getStatusCode() == expectedStatusCode) {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                                        LogUtils.success(logger, "Successfully created supplier");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Successfully created supplier", ExtentColor.GREEN));
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                    throw new Exception("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Invalid status code: " + response.getStatusCode());
                throw new Exception("Expected status code " + expectedStatusCode + " but got " + response.getStatusCode());
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during supplier creation: " + e.getMessage(), e);
            ExtentReport.getTest().log(Status.FAIL, "Error during supplier creation: " + e.getMessage());
            throw new Exception("Error during supplier creation: " + e.getMessage());
        }
    }

    @AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down successfully===");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
            ActionsMethods.logout();
            TokenManagers.clearTokens();
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}

