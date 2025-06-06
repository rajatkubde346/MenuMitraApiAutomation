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
public class SupplierCreditRatingChoicesTestScript extends APIBase {
    private Response response;
    private String baseURI;
    private String accessToken;
    private URL url;
    Logger logger = LogUtils.getLogger(SupplierCreditRatingChoicesTestScript.class);

    @DataProvider(name = "getSupplierCreditRatingChoicesUrl")
    private Object[][] getSupplierCreditRatingChoicesUrl() throws customException {
        try {
            LogUtils.info("Reading Supplier Credit Rating Choices API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Supplier Credit Rating Choices API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Supplier Credit Rating Choices API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "suppliercreditratinchoices".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No supplier credit rating choices URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved Supplier Credit Rating Choices API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved Supplier Credit Rating Choices API endpoint data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error while reading Supplier Credit Rating Choices API endpoint data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getSupplierCreditRatingChoicesData")
    public static Object[][] getSupplierCreditRatingChoicesData() throws customException {
        try {
            LogUtils.info("Reading supplier credit rating choices test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");

            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No supplier credit rating choices test scenario data found in Excel sheet");
                throw new customException("No supplier credit rating choices test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "suppliercreditratinchoices".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier credit rating choices test data found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " supplier credit rating choices test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading supplier credit rating choices test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading supplier credit rating choices test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("==== Starting setup for supplier credit rating choices test ====");
            ExtentReport.createTest("Supplier Credit Rating Choices Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing supplier credit rating choices test setup");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            Object[][] supplierCreditRatingChoicesData = getSupplierCreditRatingChoicesUrl();
            if (supplierCreditRatingChoicesData.length > 0) {
                String endpoint = supplierCreditRatingChoicesData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for supplier credit rating choices: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No supplier credit rating choices URL found in test data";
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

            LogUtils.info("Supplier credit rating choices setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier credit rating choices setup completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            String errorMsg = "Error in supplier credit rating choices setup: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getSupplierCreditRatingChoicesData")
    public void testSupplierCreditRatingChoices(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Executing supplier credit rating choices test for scenario: " + testCaseid);
            ExtentReport.createTest("Supplier Credit Rating Choices Test - " + testCaseid);
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
            
            // Validate status code and only print response if status code is 200
            if (response.getStatusCode() == 200) {
                String successMsg = "Supplier credit rating choices retrieved successfully";
                LogUtils.success(logger, successMsg);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
            } else if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                // Status code matches expected but is not 200
                String infoMsg = "Received expected status code: " + response.getStatusCode();
                LogUtils.info(infoMsg);
                ExtentReport.getTest().log(Status.PASS, infoMsg);
            } else {
                // Status code doesn't match expected
                String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
        }
        catch (Exception e) {
            String errorMsg = "Error during supplier credit rating choices test execution: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
            }
            throw new customException(errorMsg);
        }
    }
}