package com.menumitratUserAPITestScript;

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
import com.menumitra.userV2.apiRequest.UserAccountCreateApiRequest;
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
public class UserAccountCreateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private UserAccountCreateApiRequest userAccountCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(UserAccountCreateTestScript.class);

    @BeforeClass
    private void userAccountCreateSetUp() throws customException {
        try {
            LogUtils.info("User Account Create SetUp");
            ExtentReport.createTest("User Account Create SetUp");
            ExtentReport.getTest().log(Status.INFO, "User Account Create SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getUserAccountCreateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No user account create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No user account create URL found in test data");
                throw new customException("No user account create URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            userAccountCreateRequest = new UserAccountCreateApiRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in user account create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in user account create setup: " + e.getMessage());
            throw new customException("Error in user account create setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUserAccountCreateUrl")
    private Object[][] getUserAccountCreateUrl() throws customException {
        try {
            LogUtils.info("Reading User Account Create API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading User Account Create API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No User Account Create API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "useraccountcreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No user account create URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting user account create URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting user account create URL: " + e.getMessage());
            throw new customException("Error in getting user account create URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUserAccountCreateData")
    public Object[][] getUserAccountCreateData() throws customException {
        try {
            LogUtils.info("Reading user account create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading user account create test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "useraccountcreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid user account create test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }
            
            return result;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting user account create test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting user account create test data: " + e.getMessage());
            throw new customException("Error in getting user account create test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUserAccountCreateData")
    public void userAccountCreateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting user account create test case: " + testCaseid);
            ExtentReport.createTest("User Account Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("useraccountcreate")) {
                requestBodyJson = new JSONObject(requestBody);

                userAccountCreateRequest.setMobile(requestBodyJson.getString("mobile"));
                userAccountCreateRequest.setName(requestBodyJson.getString("name"));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, userAccountCreateRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate status code
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Only show response without validation
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("User account create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "User account create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "User account create test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("User account create test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in user account create test: " + e.getMessage();
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
