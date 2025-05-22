package com.menumitratCommonAPITestScript;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.utilityclass.*;
import com.menumitra.superclass.APIBase;
import com.menumitra.apiRequest.MenuRequest;
import io.restassured.response.Response;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.log4j.Logger;

@Listeners(Listener.class)
public class MenuViewTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuRequest menuViewRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(MenuViewTestScript.class);

    @BeforeClass
    private void menuViewSetUp() throws customException {
        try {
            LogUtils.info("Menu View SetUp");
            ExtentReport.createTest("Menu View SetUp");
            ExtentReport.getTest().log(Status.INFO, "Menu View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getMenuViewUrl();
            if (getUrl.length > 0) {
                String endpoint = Objects.toString(getUrl[0][2], "");
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu view URL found in test data");
                throw new customException("No menu view URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            menuViewRequest = new MenuRequest();
            LogUtils.success(logger, "Menu View Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Menu View Setup completed successfully");
        } catch (Exception e) {
            LogUtils.exception(logger, "Error in menu view setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in menu view setup: " + e.getMessage());
            throw new customException("Error in menu view setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getMenuViewUrl")
    private Object[][] getMenuViewUrl() throws customException {
        try {
            LogUtils.info("Reading menu view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading menu view URL from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(APIBase.excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "menuview".equalsIgnoreCase(Objects.toString(row[0], "")))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No menu view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved menu view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved menu view URL data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getMenuViewData")
    public Object[][] getMenuViewData() throws customException {
        try {
            LogUtils.info("Reading menu view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(APIBase.excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No menu view test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "menuview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " menu view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " menu view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuViewData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getMenuViewData")
    private void menuViewTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting menu view test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Menu View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);

            menuViewRequest.setOutlet_id(Objects.toString(requestBodyJson.get("outlet_id"), ""));
            menuViewRequest.setMenu_id(Objects.toString(requestBodyJson.get("menu_id"), ""));

            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

            // API call
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);

            response = ResponseUtil.getResponseWithAuth(baseURI, menuViewRequest, httpsmethod, accessToken);

            int expectedStatusCode = Integer.parseInt(statusCode);
            int actualStatusCode = response.getStatusCode();

            LogUtils.info("Response received with status code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Response received with status code: " + actualStatusCode);

            if (actualStatusCode != expectedStatusCode) {
                String errorMsg = String.format("Status code mismatch - Expected: %d, Actual: %d. Response body: %s",
                        expectedStatusCode, actualStatusCode, response.getBody().asString());
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException("Error in menu view test: " + errorMsg);
            }

            // Log response details
            String responseBody = response.getBody().asString();
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                actualJsonBody = new JSONObject(responseBody);
                LogUtils.info("Response Body: " + actualJsonBody.toString());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualJsonBody.toString());

                // Validate response body only if status code is 200
                if (actualStatusCode == 200) {
                    expectedResponseJson = new JSONObject(expectedResponseBody);
                }

                LogUtils.success(logger, "Menu view test completed successfully");
                ExtentReport.getTest().log(Status.PASS,
                        MarkupHelper.createLabel("Menu view test completed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Full Response: " + response.asPrettyString());
            } else {
                String errorMsg = "Empty response body received";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException("Error in menu view test: " + errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error in menu view test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
} 