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
import com.menumitra.apiRequest.verifyOTPRequest;
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
public class verifyOTPTestScript extends APIBase {
    private verifyOTPRequest verifyOTPRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject expectedJsonBody;
    private String baseURI;
    private URL url;
    Logger logger = LogUtils.getLogger(verifyOTPTestScript.class);

    @DataProvider(name = "getVerifyOTPUrl")
    public Object[][] getVerifyOTPUrl() throws Exception {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null) {
                LogUtils.failure(logger, "Error: Getting an error while read Verify OTP URL Excel File");
                throw new Exception("Error: Getting an error while read Verify OTP URL Excel File");
            }

            return Arrays.stream(readExcelData)
                    .filter(row -> "verifyotp".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Verify OTP URL Excel File", e);
            throw new Exception("Error: Getting an error while read Verify OTP URL Excel File");
        }
    }

    @DataProvider(name = "getVerifyOTPPositiveData")
    private Object[][] getVerifyOTPPositiveData() throws Exception {
        try {
            LogUtils.info("Reading positive test scenario data for verify OTP API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No verify OTP API positive test scenario data found in Excel sheet");
                throw new Exception("No verify OTP API positive test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "verifyotp".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            
            return obj;
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read verify OTP API positive test scenario data: " + e.getMessage(), e);
            throw new Exception("Error reading verify OTP API positive test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void verifyOTPSetup() throws Exception {
        try {
            LogUtils.info("Setting up test environment for verify OTP");
            ExtentReport.createTest("Start Verify OTP");
            
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseURI);
            
            Object[][] verifyOTPData = getVerifyOTPUrl();
            if (verifyOTPData.length > 0) {
                String endpoint = verifyOTPData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Verify OTP URL set to: " + baseURI);
            } else {
                LogUtils.failure(logger, "No verify OTP URL found in test data");
                throw new Exception("No verify OTP URL found in test data");
            }
            
            verifyOTPRequest = new verifyOTPRequest();
            LogUtils.info("Verify OTP setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during verify OTP setup: " + e.getMessage(), e);
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getVerifyOTPPositiveData")
    private void verifyOTPUsingValidInputData(String apiName, String testCaseId, String testType,
            String description, String httpsMethod, String requestBody, String expectedResponseBody,
            String statusCode) throws Exception {
        try {
            LogUtils.info("Starting verify OTP test case: " + testCaseId);
            ExtentReport.createTest("Verify OTP Test - " + testCaseId);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            requestBodyJson = new JSONObject(requestBody);
            expectedJsonBody = new JSONObject(expectedResponseBody);

            verifyOTPRequest.setMobile(requestBodyJson.getString("mobile_number"));
            verifyOTPRequest.setOtp(requestBodyJson.getString("otp"));

            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

            response = ResponseUtil.getResponse(baseURI, verifyOTPRequest, httpsMethod);
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());

            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                                        LogUtils.success(logger, "Successfully validated verify OTP API");
                    ExtentReport.getTest().log(Status.PASS, "Successfully validated verify OTP API");
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    throw new Exception("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code: " + response.getStatusCode());
                throw new Exception("Expected status code " + statusCode + " but got " + response.getStatusCode());
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during verify OTP test: " + e.getMessage(), e);
            ExtentReport.getTest().log(Status.FAIL, "Error during verify OTP test: " + e.getMessage());
            throw new Exception("Error during verify OTP test: " + e.getMessage());
        }
    }
}

