package com.menumitratCommonAPITestScript;

import java.io.File;
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
import com.menumitra.apiRequest.TableRequest;
import com.menumitra.apiRequest.TableUpdateRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class UpdateTableTestScript extends APIBase 
{
    private TableUpdateRequest updateTableRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedResponseJson;
    
    Logger logger = Logger.getLogger(UpdateTableTestScript.class);
    
    /**
     * Data provider for update table API endpoint URLs
     */
    @DataProvider(name="getUpdateTableUrl")
    public Object[][] getUpdateTableUrl() throws customException {
        try {
            LogUtils.info("Reading Update Table API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "updatetable".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Update Table API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Update Table API endpoint data from Excel sheet");
            throw new customException("Error While Reading Update Table API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for update table test scenarios
     */
    @DataProvider(name="getUpdateTableData")
    public Object[][] getUpdateTableData() throws customException {
        try {
            LogUtils.info("Reading update table test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No update table test scenario data found in Excel sheet");
                throw new customException("No update table test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "updatetable".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch(Exception e) {
            LogUtils.error("Error while reading update table test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading update table test scenario data: " + e.getMessage());
            throw new customException("Error while reading update table test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Update Table SetUp");
            ExtentReport.createTest("Update Table Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            Object[][] tableUrl = getUpdateTableUrl();
            if (tableUrl.length > 0) 
            {
                String endpoint = tableUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri + "" + url.getPath();
                if (url.getQuery() != null) {
                    baseUri += "?" + url.getQuery();
                }
                LogUtils.info("Update Table URL set to: " + baseUri);
            } else {
                LogUtils.error("No update table URL found in test data");
                throw new customException("No update table URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
            updateTableRequest = new TableUpdateRequest();
            LogUtils.info("Update Table Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during update table setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during update table setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to update table
     */
    @Test(dataProvider="getUpdateTableData")
    private void updateTableUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting update table test: " + description);
            ExtentReport.createTest("Update Table Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting update table test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("updatetable") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing update table request");
                requestBodyJson = new JSONObject(requestBody.replace("\\", "\\\\"));
                
                updateTableRequest.setTable_number(requestBodyJson.getString("table_number"));
                updateTableRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                updateTableRequest.setSection_id(requestBodyJson.getString("section_id"));
                updateTableRequest.setNew_table_number(requestBodyJson.getString("new_table_number"));
                updateTableRequest.setUser_id(String.valueOf(userId));
                updateTableRequest.setOrder_id(requestBodyJson.getString("order_id"));
               
                response=ResponseUtil.getResponseWithAuth(baseUri, updateTableRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Table updated successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Table updated successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                } 
                else {
                    LogUtils.failure(logger, "Table update failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Table update failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                    throw new customException("Table update failed with status code: " + response.getStatusCode());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during update table test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during update table test execution: " + e.getMessage());
        }
    }

    /**
     * Cleanup method to perform post-test cleanup
     */
    @AfterClass
    private void tearDown() throws customException {
        // No validation needed as per requirements
    }
}
