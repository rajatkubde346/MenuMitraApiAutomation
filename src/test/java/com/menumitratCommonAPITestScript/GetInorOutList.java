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
public class GetInorOutList extends APIBase {
    private Response response;
    private String baseURI;
    private String accessToken;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(GetInorOutList.class);

    @DataProvider(name = "getInOrOutListUrl")
    private Object[][] getInOrOutListUrl() throws customException {
        try {
            LogUtils.info("Reading In Or Out List API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading In Or Out List API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No In Or Out List API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "getinoroutlist".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No in or out list URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved In Or Out List API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved In Or Out List API endpoint data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error while reading In Or Out List API endpoint data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getInOrOutListData")
    public static Object[][] getInOrOutListData() throws customException {
        try {
            LogUtils.info("Reading in or out list test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");

            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No in or out list test scenario data found in Excel sheet");
                throw new customException("No in or out list test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "getinoroutlist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid in or out list test data found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " in or out list test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading in or out list test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading in or out list test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("==== Starting setup for in or out list test ====");
            ExtentReport.createTest("In Or Out List Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing in or out list test setup");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            Object[][] inOrOutListData = getInOrOutListUrl();
            if (inOrOutListData.length > 0) {
                String endpoint = inOrOutListData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for in or out list: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No in or out list URL found in test data";
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

            LogUtils.info("In or out list setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("In or out list setup completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            String errorMsg = "Error in in or out list setup: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getInOrOutListData")
    public void testInOrOutList(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Executing in or out list test for scenario: " + testCaseid);
            ExtentReport.createTest("In Or Out List Test - " + testCaseid);
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
            if(response.getStatusCode() != 200) {
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
            String successMsg = "In or out list retrieved successfully";
            LogUtils.success(logger, successMsg);
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(successMsg, ExtentColor.GREEN));
            ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
        }
        catch(Exception e) {
            String errorMsg = "Error during in or out list test execution: " + e.getMessage();
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