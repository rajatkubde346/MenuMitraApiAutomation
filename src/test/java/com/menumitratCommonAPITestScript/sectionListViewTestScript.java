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
import com.menumitra.apiRequest.sectionListViewRequest;
import com.menumitra.apiRequest.sectionRequest;
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
public class sectionListViewTestScript extends APIBase
{
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualJsonBody;
    private JSONObject expectedJsonBody; 
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private sectionRequest sectionrequest;
    private sectionListViewRequest sectionListViewRequest;
    Logger logger=LogUtils.getLogger(sectionCreateTestScript.class); 	
    
    @DataProvider(name="getSectionListViewURL")
    public Object[][] getSectionListViewURL() throws Exception {
        try {
            Object[][] readData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readData == null) {
                LogUtils.failure(logger, "Error: Getting an error while read Section URL Excel File");
                throw new Exception("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectionlistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section URL Excel File", e);
            throw new Exception("Error: Getting an error while read Section URL Excel File");
        }
    }
	
    @DataProvider(name="getSectionListViewPositiveInputData")
    private Object[][] getSectionListViewPositiveInputData() throws Exception {
        try {
            LogUtils.info("Reading positive test scenario data for section list view API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, property.getProperty("CommonAPITestScenario"));
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No Section List View API positive test scenario data found in Excel sheet");
                throw new Exception("No Section List View API Positive test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "sectionlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Section List View API positive test scenario data: " + e.getMessage(), e);
            throw new Exception("Error reading Section List View API positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void sectionListViewSetup() throws Exception {
        try {
            LogUtils.info("Setting up test environment");
            ExtentReport.createTest("Start Section List View");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set section list view URL
            Object[][] sectionListViewData = getSectionListViewURL();
            if (sectionListViewData.length > 0) {
                String endpoint = sectionListViewData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section List View URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section list view URL found in test data");
                throw new Exception("No section list view URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            sectionrequest = new sectionRequest();
            
            LogUtils.info("Section list view setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section list view setup: " + e.getMessage(), e);
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getSectionListViewPositiveInputData", priority = 1)
    private void verifySectionListViewUsingValidInputData(String apiName, String testCaseId,
            String testType, String description, String httpsMethod,
            String requestBody, String expectedResponseBody, String statusCode) throws Exception {
        try {
            LogUtils.info("Start section list view API using valid input data");
            ExtentReport.createTest("Verify Section List View API: " + description);
            ExtentReport.getTest().log(Status.INFO, "====Start section list view using positive input data====");
            ExtentReport.getTest().log(Status.INFO, "Constructed Base URI: " + baseUri);

            if (apiName.contains("sectionlistview") && testType.contains("positive")) {
                requestBodyJson = new JSONObject(requestBody);
                expectedJsonBody = new JSONObject(expectedResponseBody);
                sectionListViewRequest = new sectionListViewRequest();
                sectionListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                sectionListViewRequest.setUser_id(String.valueOf(userId));
                LogUtils.info("Verify section list view payload prepared");
                ExtentReport.getTest().log(Status.INFO, "Verify section list view payload prepared with outlet_id: " + requestBodyJson.getString("outlet_id"));
                
                response = ResponseUtil.getResponseWithAuth(baseUri, sectionListViewRequest, httpsMethod, accessToken);
                LogUtils.info("Section list view API response");
                ExtentReport.getTest().log(Status.INFO, "Section list view API response: " + response.getBody().asString());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    String responseBody = response.getBody().asString();
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                                                LogUtils.success(logger, "Successfully validated section list view API using positive input data");
                        ExtentReport.getTest().log(Status.PASS, "Successfully validated section list view API using positive input data");
                    } else {
                        LogUtils.failure(logger, "Empty response body received");
                        ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                        throw new Exception("Response body is empty");
                    }
                } else {
                    LogUtils.failure(logger, "Invalid status code for section list view API using positive input data: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Invalid status code for section list view API using positive input data: " + response.getStatusCode());
                    throw new Exception("In section list view API using positive input test case expected status code " + statusCode + " but got " + response.getStatusCode());
                }
            } else {
                String errorMsg = "API name or test type mismatch - Expected: sectionlistview/positive, Actual: " + apiName + "/" + testType;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new Exception(errorMsg);
            }
        } catch (Exception e) {
            LogUtils.exception(logger, "An error occurred during section list view verification: " + e.getMessage(), e);
            ExtentReport.getTest().log(Status.FAIL, "An error occurred during section list view verification: " + e.getMessage());
            throw new Exception("An error occurred during section list view verification");
        }
    }
}

