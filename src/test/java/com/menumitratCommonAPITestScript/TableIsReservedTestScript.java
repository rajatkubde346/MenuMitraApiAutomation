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
import com.menumitra.apiRequest.TableReservationRequest;
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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class TableIsReservedTestScript extends APIBase
{
    private TableReservationRequest tableIsReservedRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedJsonBody;
    Logger logger = LogUtils.getLogger(TableIsReservedTestScript.class);
    
    @DataProvider(name = "getTableIsReservedUrl")
    public Object[][] getTableIsReservedUrl() throws customException {
        try {
            LogUtils.info("Reading Table Is Reserved API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "tableisreserved".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Table Is Reserved API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Table Is Reserved API endpoint data from Excel sheet");
            throw new customException("Error While Reading Table Is Reserved API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getTableIsReservedData")
    public Object[][] getTableIsReservedData() throws customException {
        try {
            LogUtils.info("Reading table is reserved test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No table is reserved test scenario data found in Excel sheet");
                throw new customException("No table is reserved test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "tableisreserved".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for table is reserved");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading table is reserved test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading table is reserved test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading table is reserved test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("Table Is Reserved SetUp");
            ExtentReport.createTest("Table Is Reserved Setup");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseURI);

            Object[][] tableIsReservedUrl = getTableIsReservedUrl();
            if (tableIsReservedUrl.length > 0) {
                String endpoint = tableIsReservedUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Table Is Reserved URL set to: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Table Is Reserved URL set to: " + baseURI);
            } else {
                LogUtils.error("No table is reserved URL found in test data");
                throw new customException("No table is reserved URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }

            tableIsReservedRequest = new TableReservationRequest();
            LogUtils.info("Table Is Reserved Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Table Is Reserved Setup completed successfully");

        } catch (Exception e) {
            LogUtils.error("Error during table is reserved setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during table is reserved setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getTableIsReservedData")
    private void setTableIsReserved(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting table is reserved test: " + description);
            ExtentReport.createTest("Table Is Reserved Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Starting table is reserved test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseURI);

            if (apiName.equalsIgnoreCase("tableisreserved") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing table is reserved request");
                requestBodyJson = new JSONObject(requestBody);
                
                tableIsReservedRequest.setTable_id(requestBodyJson.getInt("table_id"));
                tableIsReservedRequest.setTable_number(requestBodyJson.getString("table_number"));
                tableIsReservedRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                tableIsReservedRequest.setUser_id(String.valueOf(userId));
                tableIsReservedRequest.setIs_reserved(requestBodyJson.getBoolean("is_reserved"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, tableIsReservedRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                if (response.getStatusCode() == 200) {
                    LogUtils.success(logger, "Table reservation status updated successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table reservation status updated successfully", ExtentColor.GREEN));
                    
                    // Just show response without validation
                    if (response.asString() != null && !response.asString().isEmpty()) {
                        actualResponseBody = new JSONObject(response.asString());
                        ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
                    }
                } else {
                    LogUtils.failure(logger, "Table reservation status update failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Table reservation status update failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during table is reserved test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during table is reserved test execution: " + e.getMessage());
        }
    }
}
