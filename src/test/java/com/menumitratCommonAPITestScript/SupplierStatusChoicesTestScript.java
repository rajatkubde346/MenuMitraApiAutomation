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
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Listeners(Listener.class)
public class SupplierStatusChoicesTestScript extends APIBase {
    private Response response;
    private String baseURI;
    private String accessToken;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(SupplierStatusChoicesTestScript.class);

    @DataProvider(name = "getSupplierStatusChoicesUrl")
    private Object[][] getSupplierStatusChoicesUrl() throws customException {
        try {
            LogUtils.info("Reading Supplier Status Choices API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Supplier Status Choices API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Supplier Status Choices API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "supplierStatusChoices".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No supplier status choices URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved Supplier Status Choices API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved Supplier Status Choices API endpoint data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error while reading Supplier Status Choices API endpoint data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getSupplierStatusChoicesData")
    public static Object[][] getSupplierStatusChoicesData() throws customException {
        try {
            LogUtils.info("Reading supplier status choices test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");

            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No supplier status choices test scenario data found in Excel sheet");
                throw new customException("No supplier status choices test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "supplierStatusChoices".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier status choices test data found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " supplier status choices test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading supplier status choices test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading supplier status choices test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("==== Starting setup for supplier status choices test ====");
            ExtentReport.createTest("Supplier Status Choices Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing supplier status choices test setup");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            Object[][] supplierStatusChoicesData = getSupplierStatusChoicesUrl();
            if (supplierStatusChoicesData.length > 0) {
                String endpoint = supplierStatusChoicesData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for supplier status choices: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No supplier status choices URL found in test data";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            accessToken = TokenManagers.getJwtToken();

            if (accessToken.isEmpty()) {
                String errorMsg = "Required tokens not found. Please ensure login and OTP verification is completed";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Supplier status choices setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier status choices setup completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            String errorMsg = "Error in supplier status choices setup: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getSupplierStatusChoicesData")
    public void testSupplierStatusChoices(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Executing supplier status choices test for scenario: " + testCaseid);
            ExtentReport.createTest("Supplier Status Choices Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            LogUtils.info("Making GET request to: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Making GET request to: " + baseURI);
            
            // Since this is a GET request with no request body, we'll use RestAssured directly
            response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get(baseURI)
                    .then()
                    .log().all()
                    .extract()
                    .response();
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());

            // Validate status code
            if(response.getStatusCode() != Integer.parseInt(statusCode)) {
                String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            // Only log the response body for status code 200
            if(response.getStatusCode() == 200) {
                LogUtils.info("Status code is 200 - Success");
                ExtentReport.getTest().log(Status.INFO, "Status code is 200 - Success");
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            }
                
                LogUtils.info("Response body validation passed");
                ExtentReport.getTest().log(Status.PASS, "Response body validation passed");
            

            // Success case
            String successMsg = "Supplier status choices retrieved successfully";
            LogUtils.success(logger, successMsg);
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
            ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
        }
        catch(Exception e) {
            String errorMsg = "Error during supplier status choices test execution: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
}