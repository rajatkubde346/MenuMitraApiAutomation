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
import com.menumitra.apiRequest.TableRequest;
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
public class TableListTestScript extends APIBase
{
    private TableRequest tableListRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody; 
    private JSONObject expectedResponse;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private Logger logger = LogUtils.getLogger(TableListTestScript.class);


    @DataProvider(name = "getTableListUrl")
    private Object[][] getTableListUrl() throws customException {
        try {
            LogUtils.info("Starting to read Table List API endpoint data from Excel");
            ExtentReport.createTest("Reading Table List API Endpoints");
            
            if(excelSheetPathForGetApis == null || excelSheetPathForGetApis.isEmpty()) {
                String errorMsg = "Excel sheet path is null or empty";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.ERROR, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Table List API endpoint data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.ERROR, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length > 0 && "gettablelistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if(filteredData.length == 0) {
                String errorMsg = "No matching Table List API endpoints found after filtering";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.ERROR, errorMsg);
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved " + filteredData.length + " Table List API endpoints");
            ExtentReport.getTest().log(Status.INFO, "Successfully read " + filteredData.length + " endpoints from Excel sheet");
            return filteredData;
            
        } catch (Exception e) {
            String errorMsg = "Error while reading Table List API endpoint data from Excel sheet: " + 
                            (e.getMessage() != null ? e.getMessage() : "Unknown error");
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, "Excel Path: " + excelSheetPathForGetApis);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }


    @DataProvider(name = "getTableListData")
    public static Object[][] getTableListData() throws customException {
        try {
            LogUtils.info("Starting to read table list test scenario data from Excel");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                String errorMsg = "No table list test scenario data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.ERROR, errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                        "tablelistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully filtered " + obj.length + " test scenarios for table list view");
            ExtentReport.getTest().log(Status.INFO, "Found " + obj.length + " test scenarios for table list view");
            return obj;

        } catch (Exception e) {
            String errorMsg = "Error while reading table list test scenario data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.ERROR, "Excel Path: " + excelSheetPathForGetApis);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void TableListViewSetUp() throws customException {
        try {
            LogUtils.info("Starting setup for table list view test");
            ExtentReport.createTest("Table List View Setup");
            
            LogUtils.info("Attempting login");
            ActionsMethods.login();
            LogUtils.info("Login successful, verifying OTP");
            ActionsMethods.verifyOTP();
            
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseURI);
            
            Object[][] tableListData = getTableListUrl();
            if (tableListData.length > 0) 
            {
                String endpoint = tableListData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Endpoint URL constructed: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "API Endpoint: " + baseURI);
            }
            
            accessToken = TokenManagers.getJwtToken();
            LogUtils.info("JWT Token retrieved successfully");
            tableListRequest = new TableRequest();
            
            LogUtils.info("Table List View setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error during Table List View setup: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, "Setup failed");
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider="getTableListData")
    private void verifyTableListView(String apiName,String testCaseid, String testType, String description,
    String httpsmethod,String requestBodyPayload,String expectedResponseBody,String statusCode) throws customException
    {
        try
        {
            LogUtils.info("Starting table list view test - TestCase ID: " + testCaseid);
            ExtentReport.createTest("Table List View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "HTTP Method: " + httpsmethod);

            if(apiName.equalsIgnoreCase("gettablelistview"))
            {
                requestBodyJson = new JSONObject(requestBodyPayload);
                tableListRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                tableListRequest.setSection_id(requestBodyJson.getString("section_id"));

                LogUtils.info("Request Body: " + requestBodyJson.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
                ExtentReport.getTest().log(Status.INFO, "API URL: " + baseURI);

                response = ResponseUtil.getResponseWithAuth(baseURI,tableListRequest,httpsmethod,accessToken);
                LogUtils.info("Response received - Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                
                if(response.getStatusCode()==200) 
                {   
                    LogUtils.info("Response Body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.PASS, "Response Body: " + response.asPrettyString());
                    LogUtils.success(logger, "Table list view retrieved successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table list view retrieved successfully", ExtentColor.GREEN));
                  
                } else {
                    String errorMsg = "Failed to retrieve table list view";
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Expected Status Code: 200");
                    ExtentReport.getTest().log(Status.FAIL, "Actual Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asString());
                    ExtentReport.getTest().log(Status.FAIL, "API URL: " + baseURI);
                    throw new customException(errorMsg);
                }
            }
            
        }
        catch(Exception e)
        {
            String errorMsg = "Error in table list view test execution";
            LogUtils.error(errorMsg + ": " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Test execution failed");
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "API URL: " + baseURI);
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asString());
            }
            throw new customException(errorMsg + ": " + e.getMessage());
        }
    }
}
