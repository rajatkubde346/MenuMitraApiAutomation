package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.menumitra.apiRequest.WaiterRequest;
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
public class DeleteTemplatesTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private WaiterRequest deleteTemplatesRequest;
    private URL url;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(DeleteTemplatesTestScript.class);
   
    @BeforeClass
    private void deleteTemplatesSetUp() throws customException
    {
        try
        {
            LogUtils.info("Delete Templates SetUp");
            ExtentReport.createTest("Delete Templates SetUp");
            ExtentReport.getTest().log(Status.INFO,"Delete Templates SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getDeleteTemplatesUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No delete templates URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No delete templates URL found in test data");
                throw new customException("No delete templates URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            deleteTemplatesRequest = new WaiterRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in delete templates setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in delete templates setup: " + e.getMessage());
            throw new customException("Error in delete templates setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getDeleteTemplatesUrl")
    private Object[][] getDeleteTemplatesUrl() throws customException {
        try {
            LogUtils.info("Reading Delete Templates API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Delete Templates API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Delete Templates API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "deletetemplates".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No delete templates URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting delete templates URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting delete templates URL: " + e.getMessage());
            throw new customException("Error in getting delete templates URL: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getDeleteTemplatesData")
    public Object[][] getDeleteTemplatesData() throws customException {
        try {
            LogUtils.info("Reading delete templates test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading delete templates test scenario data");
            
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
                        "deletetemplates".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid delete templates test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting delete templates test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting delete templates test data: " + e.getMessage());
            throw new customException("Error in getting delete templates test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getDeleteTemplatesData")
    public void deleteTemplatesTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting delete templates test case: " + testCaseid);
            ExtentReport.createTest("Delete Templates Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("deletetemplates")) {
                requestBodyJson = new JSONObject(requestBody);
                
                Map<String,String> data=new HashMap<String, String>();
                data.put("template_id",requestBodyJson.getString("template_id"));                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, data, httpsmethod, accessToken);
                
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
                
                // Only log the response body, no validation as requested
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("Template deleted successfully");
                ExtentReport.getTest().log(Status.PASS, "Template deleted successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                
                LogUtils.success(logger, "Delete templates test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Delete templates test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in delete templates test: " + e.getMessage();
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