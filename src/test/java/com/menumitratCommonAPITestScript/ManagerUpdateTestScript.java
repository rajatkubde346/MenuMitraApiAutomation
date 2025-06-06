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
import com.menumitra.apiRequest.MangerCreateRequest;
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
public class ManagerUpdateTestScript extends APIBase {
    private MangerCreateRequest managerUpdateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(ManagerUpdateTestScript.class);

    @DataProvider(name = "getManagerUpdateUrl")
    public static Object[][] getManagerUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Manager Update API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> "managerupdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Manager Update API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Manager Update API endpoint data from Excel sheet");
            throw new customException("Error While Reading Manager Update API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getManagerUpdateData")
    public static Object[][] getManagerUpdateData() throws customException {
        try {
            LogUtils.info("Reading manager update test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No manager update test scenario data found in Excel sheet");
                throw new customException("No manager update test scenario data found in Excel sheet");
            }
            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 2 &&
                        "managerupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.error("Error while reading manager update test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading manager update test scenario data: " + e.getMessage());
            throw new customException("Error while reading manager update test scenario data: " + e.getMessage());
        }
    }

    @DataProvider(name = "getManagerUpdateNegativeData")
    public static Object[][] getManagerUpdateNegativeData() throws customException {
        try {
            LogUtils.info("Reading manager update negative test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                        "managerupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.error("Error while reading manager update negative test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading manager update negative test scenario data: " + e.getMessage());
            throw new customException("Error while reading manager update negative test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for manager update test====");
            ExtentReport.createTest("Manager Update Setup");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            Object[][] managerUpdateData = getManagerUpdateUrl();
            if (managerUpdateData.length > 0) {
                String endpoint = managerUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for manager update: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No manager update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No manager update URL found in test data");
                throw new customException("No manager update URL found in test data");
            }
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            managerUpdateRequest = new MangerCreateRequest();
            LogUtils.success(logger, "Manager Update Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Manager Update Setup completed successfully");
        } catch (Exception e) {
            LogUtils.failure(logger, "Error during manager update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during manager update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getManagerUpdateData")
    private void updateManagerPositive(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting manager update positive test case: " + testCaseid);
            ExtentReport.createTest("Manager Update Positive Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            requestBodyJson = new JSONObject(requestBodyPayload);
            managerUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            managerUpdateRequest.setUser_id(requestBodyJson.getString("user_id"));
            managerUpdateRequest.setName(requestBodyJson.getString("name"));
            managerUpdateRequest.setMobile(requestBodyJson.getString("mobile"));
            managerUpdateRequest.setAddress(requestBodyJson.getString("address"));
            managerUpdateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
            managerUpdateRequest.setUpdate_user_id(requestBodyJson.getString("update_user_id"));

            // Log payload and request
            ExtentReport.getTest().log(Status.INFO, "Payload: " + requestBodyPayload);
            LogUtils.info("Payload: " + requestBodyPayload);

            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Request Body: " + requestBodyJson.toString(2));

            response = ResponseUtil.getResponseWithAuth(baseURI, managerUpdateRequest, httpsmethod, accessToken);

            // Log response
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Only status code validation for positive
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getManagerUpdateNegativeData")
    private void updateManagerNegative(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting manager update negative test case: " + testCaseid);
            ExtentReport.createTest("Manager Update Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            requestBodyJson = new JSONObject(requestBodyPayload);
            // Set request object
            managerUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            managerUpdateRequest.setUser_id(requestBodyJson.getString("user_id"));
            managerUpdateRequest.setName(requestBodyJson.getString("name"));
            managerUpdateRequest.setMobile(requestBodyJson.getString("mobile"));
            managerUpdateRequest.setAddress(requestBodyJson.getString("address"));
            managerUpdateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
            managerUpdateRequest.setUpdate_user_id(requestBodyJson.getString("update_user_id"));
            // Log payload and request
            ExtentReport.getTest().log(Status.INFO, "Payload: " + requestBodyPayload);
            LogUtils.info("Payload: " + requestBodyPayload);

            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
            LogUtils.info("Request Body: " + requestBodyJson.toString(2));

            response = ResponseUtil.getResponseWithAuth(baseURI, managerUpdateRequest, httpsmethod, accessToken);

            // Log response
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            // Validate status code
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                actualResponseBody = new JSONObject(response.asString());
                expectedResponse = new JSONObject(expectedResponseBody);

                // Log actual and expected response
                ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));

                // Validate response body
                validateResponseBody.handleResponseBody(response, expectedResponse);

                ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                LogUtils.success(logger, "Response body validation passed successfully");
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
}