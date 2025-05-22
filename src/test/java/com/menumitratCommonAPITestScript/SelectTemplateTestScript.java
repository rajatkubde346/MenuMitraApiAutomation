package com.menumitratCommonAPITestScript;

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
import com.menumitra.apiRequest.SelectTemplateRequest;
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

import io.restassured.response.Response;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class SelectTemplateTestScript extends APIBase 
{
    private SelectTemplateRequest selectTemplateRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedJsonBody;
    Logger logger = Logger.getLogger(SelectTemplateTestScript.class);
    
    /**
     * Data provider for select template API endpoint URLs
     */
    @DataProvider(name="getSelectTemplateUrl")
    public Object[][] getSelectTemplateUrl() throws customException {
        try {
            LogUtils.info("Reading Select Template API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "selecttemplate".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Select Template API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Select Template API endpoint data from Excel sheet");
            throw new customException("Error While Reading Select Template API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for select template test scenarios
     */
    @DataProvider(name="getSelectTemplateData")
    public Object[][] getSelectTemplateData() throws customException {
        try {
            LogUtils.info("Reading select template test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No select template test scenario data found in Excel sheet");
                throw new customException("No select template test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "selecttemplate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.error("Error while reading select template test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading select template test scenario data: " + e.getMessage());
            throw new customException("Error while reading select template test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Select Template SetUp");
            ExtentReport.createTest("Select Template Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            Object[][] templateUrl = getSelectTemplateUrl();
            if (templateUrl.length > 0) 
            {
                String endpoint = templateUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Select Template URL set to: " + baseUri);
            } else {
                LogUtils.error("No select template URL found in test data");
                throw new customException("No select template URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
            selectTemplateRequest = new SelectTemplateRequest();
            LogUtils.info("Select Template Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during select template setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during select template setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to select template
     */
    @Test(dataProvider="getSelectTemplateData")
    private void selectTemplateUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting select template test: " + description);
            ExtentReport.createTest("Select Template Using Valid Input Data - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Starting select template test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("selecttemplate") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing select template request");
                requestBodyJson = new JSONObject(requestBody);
                
                selectTemplateRequest.setSection_id(requestBodyJson.getInt("section_id"));
                selectTemplateRequest.setTemplate_id(requestBodyJson.getInt("template_id"));
                selectTemplateRequest.setUser_id(String.valueOf(userId));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                LogUtils.info("Sending request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending request to select template");
                
                response = ResponseUtil.getResponseWithAuth(baseUri, selectTemplateRequest, httpsmethod, accessToken);
                
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Template selected successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Template selected successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                } else {
                    LogUtils.failure(logger, "Template selection failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Template selection failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                    throw new customException("Template selection failed with status code: " + response.getStatusCode());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during select template test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during select template test execution: " + e.getMessage());
        }
    }

    /**
     * Cleanup method to perform post-test cleanup
     */
    @AfterClass
    private void tearDown() throws customException {
        // No cleanup needed as per requirements
    }
}
