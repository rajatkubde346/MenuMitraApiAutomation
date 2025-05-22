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
import com.menumitra.apiRequest.ChefRequest;
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
public class ChefListViewTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private ChefRequest chefListViewRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(ChefListViewTestScript.class);
   
    @BeforeClass
    private void chefListViewSetUp() throws customException
    {
        try
        {
            LogUtils.info("Chef List View SetUp");
            ExtentReport.createTest("Chef List View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Chef List View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getChefListViewUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No chef list view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No chef list view URL found in test data");
                throw new customException("No chef list view URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            chefListViewRequest = new ChefRequest();
            LogUtils.success(logger, "Chef List View Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Chef List View Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in chef list view setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in chef list view setup: " + e.getMessage());
            throw new customException("Error in chef list view setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getChefListViewUrl")
    private Object[][] getChefListViewUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading chef list view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading chef list view URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "cheflistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No chef list view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved chef list view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved chef list view URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getChefListViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getChefListViewData") 
    public Object[][] getChefListViewData() throws customException {
        try {
            LogUtils.info("Reading chef list view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading chef list view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No chef list view test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "cheflistview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid chef list view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " chef list view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " chef list view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getChefListViewData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getChefListViewData")
    private void verifyChefListView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Chef list view test execution: " + description);
            ExtentReport.createTest("Chef List View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Chef list view test execution: " + description);

            if(apiName.equalsIgnoreCase("cheflistview"))
            {
                requestBodyJson = new JSONObject(requestBody);

                chefListViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                
                LogUtils.info("Constructed chef list view request"); 
                ExtentReport.getTest().log(Status.INFO, "Constructed chef list view request");

                response = ResponseUtil.getResponseWithAuth(baseURI, chefListViewRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                LogUtils.success(logger, "Chef list view API executed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Chef list view API executed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in chef list view test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in chef list view test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in chef list view test: " + e.getMessage());
        }
    }
}
