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
import com.menumitra.apiRequest.MenuRequest;
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
public class MenuCategorysTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuRequest menuRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(MenuCategorysTestScript.class);
   
    @BeforeClass
    private void menuCategorysSetUp() throws customException
    {
        try
        {
            LogUtils.info("Menu Categorys SetUp");
            ExtentReport.createTest("Menu Categorys SetUp");
            ExtentReport.getTest().log(Status.INFO,"Menu Categorys SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getMenuCategorysUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu categorys URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu categorys URL found in test data");
                throw new customException("No menu categorys URL found in test data");
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
            
            menuRequest = new MenuRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in menu categorys setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in menu categorys setup: " + e.getMessage());
            throw new customException("Error in menu categorys setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getMenuCategorysUrl")
    private Object[][] getMenuCategorysUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading menu categorys URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading menu categorys URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "menucategorys".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No menu categorys URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved menu categorys URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved menu categorys URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getMenuCategorysUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getMenuCategorysData") 
    public Object[][] getMenuCategorysData() throws customException {
        try {
            LogUtils.info("Reading menu categorys test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu categorys test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No menu categorys test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "menucategorys".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu categorys test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " menu categorys test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " menu categorys test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuCategorysData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getMenuCategorysData")
    private void verifyMenuCategorys(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Menu categorys test execution: " + description);
            ExtentReport.createTest("Menu Categorys Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Menu categorys test execution: " + description);

            if(apiName.equalsIgnoreCase("menucategorys"))
            {
                requestBodyJson = new JSONObject(requestBody);

                // Set only outlet_id in the request
                menuRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                
                LogUtils.info("Constructed menu categorys request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed menu categorys request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, menuRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Menu categorys API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu categorys API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Only show response body without validation
                    actualJsonBody = new JSONObject(response.asString());
                    LogUtils.info("Response received successfully");
                    LogUtils.info("Response Body: " + actualJsonBody.toString());
                    ExtentReport.getTest().log(Status.PASS, "Response received successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response Body: " + actualJsonBody.toString());
                    
                    // Make sure to use Status.PASS for the response to show in the report
                    ExtentReport.getTest().log(Status.PASS, "Full Response:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    
                    // Add a screenshot or additional details that might help visibility
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                }
                else{
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    LogUtils.info("Response Body: " + response.asString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asPrettyString());
                    throw new customException(errorMsg);
                }
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in menu categorys test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in menu categorys test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in menu categorys test: " + e.getMessage());
        }
    }
    
    
    @DataProvider(name = "getMenuCategoriesNegativeData")
    public Object[][] getMenuCategoriesNegativeData() throws customException {
        try {
            LogUtils.info("Reading menu categories negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu categories negative test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "menucategories".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu categories negative test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting menu categories negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu categories negative test data: " + e.getMessage());
            throw new customException("Error in getting menu categories negative test data: " + e.getMessage());
        }
    }

    private boolean validateResponseMessageSentences(String message) {
        if (message == null || message.trim().isEmpty()) {
            return true; // Empty message is considered valid
        }
        
        // Split message into sentences using regex
        String[] sentences = message.split("[.!?]+");
        return sentences.length <= 6;
    }

    @Test(dataProvider = "getMenuCategoriesNegativeData")
    public void menuCategoriesNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu categories negative test case: " + testCaseid);
            ExtentReport.createTest("Menu Categories Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);


            requestBodyJson = new JSONObject(requestBody);
            expectedJsonBody = new JSONObject(expectedResponseBody);

            // Validate API name
            if (!"menucategories".equalsIgnoreCase(apiName)) {
                String errorMsg = "Invalid API name. Expected: menucategories, Actual: " + apiName;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            // Set request parameters
            if (requestBodyJson.has("outlet_id")) {
                menuRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            }

            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

            response = ResponseUtil.getResponseWithAuth(baseURI, menuRequest, httpsmethod, accessToken);

            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

            int expectedStatusCode = Integer.parseInt(statusCode);

            // Validate status code
            if (response.getStatusCode() != expectedStatusCode) {
                String errorMsg = "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            // Validate response body
            actualJsonBody = new JSONObject(response.asString());

            // Validate response message sentences
            if (actualJsonBody.has("message")) {
                String message = actualJsonBody.getString("message");
                if (validateResponseMessageSentences(message)) {
                    LogUtils.info("Response message validation passed");
                } else {
                    String errorMsg = "Response message contains more than 6 sentences";
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
            }

            // Validate complete response body
            validateResponseBody.handleResponseBody(response, expectedJsonBody);

            LogUtils.success(logger, "Menu categories negative test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu categories negative test completed successfully", ExtentColor.GREEN));

        } catch (Exception e) {
            String errorMsg = "Error in menu categories negative test: " + e.getMessage();
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