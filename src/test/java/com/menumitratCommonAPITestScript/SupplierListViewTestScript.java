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
import com.menumitra.apiRequest.StaffListViewRequest;
import com.menumitra.apiRequest.SupplierListViewRequest;
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
public class SupplierListViewTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private SupplierListViewRequest supplierListViewRequest;
    private URL url;
    private JSONObject expectedResponseJson;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(SupplierListViewTestScript.class);
   
    @BeforeClass
    private void supplierListViewSetUp() throws customException
    {
        try
        {
            LogUtils.info("Supplier List View SetUp");
            ExtentReport.createTest("Supplier List View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Supplier List View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getSupplierListViewUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No supplier list view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No supplier list view URL found in test data");
                throw new customException("No supplier list view URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger,"Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            supplierListViewRequest = new SupplierListViewRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in supplier list view setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in supplier list view setup: " + e.getMessage());
            throw new customException("Error in supplier list view setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getSupplierListViewUrl")
    private Object[][] getSupplierListViewUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading supplier list view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading supplier list view URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "supplierlistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No supplier list view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved supplier list view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved supplier list view URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getSupplierListViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getSupplierListViewPositiveData") 
    public Object[][] getSupplierListViewPositiveData() throws customException {
        try {
            LogUtils.info("Reading supplier list view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading supplier list view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No supplier list view test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "supplierlistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier list view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " supplier list view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " supplier list view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getSupplierListViewPositiveData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getSupplierListViewPositiveData")
    private void verifySupplierListViewUsingPositiveData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Supplier list view test execution: " + description);
            ExtentReport.createTest("Supplier list view test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Supplier list view test execution: " + description);

            if(apiName.equalsIgnoreCase("supplierlistview"))
            {
                requestBodyJson = new JSONObject(requestBody);

                supplierListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                LogUtils.info("Constructed supplier list view request"); 
                ExtentReport.getTest().log(Status.INFO, "Constructed supplier list view request");

                response = ResponseUtil.getResponseWithAuth(baseURI, supplierListViewRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                LogUtils.success(logger, "Supplier list view API executed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier list view API executed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in supplier list view test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in supplier list view test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in supplier list view test: " + e.getMessage());
        }
    }
}

