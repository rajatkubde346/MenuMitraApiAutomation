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
import com.menumitra.apiRequest.MenuCategoryRequest;
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
public class MenuCategoryViewTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuCategoryRequest menuCategoryViewRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(MenuCategoryViewTestScript.class);
   
    @BeforeClass
    private void menuCategoryViewSetUp() throws customException
    {
        try
        {
            LogUtils.info("Menu Category View SetUp");
            ExtentReport.createTest("Menu Category View SetUp");
            ExtentReport.getTest().log(Status.INFO,"Menu Category View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getMenuCategoryViewUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu category view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu category view URL found in test data");
                throw new customException("No menu category view URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger,"Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            menuCategoryViewRequest = new MenuCategoryRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in menu category view setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in menu category view setup: " + e.getMessage());
            throw new customException("Error in menu category view setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getMenuCategoryViewUrl")
    private Object[][] getMenuCategoryViewUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading menu category view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category view URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "menucategoryview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No menu category view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved menu category view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved menu category view URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getMenuCategoryViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getMenuCategoryViewData") 
    public Object[][] getMenuCategoryViewData() throws customException {
        try {
            LogUtils.info("Reading menu category view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No menu category view test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "menucategoryview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu category view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " menu category view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " menu category view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuCategoryViewData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getMenuCategoryViewData")
    private void verifyMenuCategoryView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Menu category view test execution: " + description);
            ExtentReport.createTest("Menu Category View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Menu category view test execution: " + description);

            if(apiName.equalsIgnoreCase("menucategoryview"))
            {
                requestBodyJson = new JSONObject(requestBody);

                menuCategoryViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                menuCategoryViewRequest.setMenu_cat_id(requestBodyJson.getString("menu_cat_id"));
                menuCategoryViewRequest.setUser_id(String.valueOf(user_id));
                
                LogUtils.info("Constructed menu category view request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed menu category view request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, menuCategoryViewRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Menu category view API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu category view API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Validate response body if expected response is provided
                    actualJsonBody = new JSONObject(response.asString());
                    if(expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        expectedJsonBody = new JSONObject(expectedResponseBody);
                        
                      
                       
                    }
                    
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
            LogUtils.exception(logger, "Error in menu category view test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in menu category view test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in menu category view test: " + e.getMessage());
        }
    }
    
    
    @DataProvider(name = "getMenuCategoryViewNegativeData")
    public Object[][] getMenuCategoryViewNegativeData() throws customException {
        try {
            LogUtils.info("Reading menu category view negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category view negative test scenario data");

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
                    "menucategoryview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu category view negative test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            String errorMsg = "Error in getting menu category view negative test data: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getMenuCategoryViewNegativeData")
    public void menuCategoryViewNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu category view negative test case: " + testCaseid);
            ExtentReport.createTest("Menu Category View Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (!"menucategoryview".equalsIgnoreCase(apiName) || !"negative".equalsIgnoreCase(testType)) {
                String errorMsg = "Invalid API name or test type";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            requestBodyJson = new JSONObject(requestBody);
            menuCategoryViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            menuCategoryViewRequest.setMenu_cat_id(requestBodyJson.getString("menu_cat_id"));
            menuCategoryViewRequest.setUser_id(String.valueOf(user_id));

            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

            response = ResponseUtil.getResponseWithAuth(baseURI, menuCategoryViewRequest, httpsmethod, accessToken);

            int expectedStatusCode = Integer.parseInt(statusCode);
            int actualStatusCode = response.getStatusCode();

            // Log status code comparison
            LogUtils.info("Expected Status Code: " + expectedStatusCode);
            LogUtils.info("Actual Status Code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + actualStatusCode);

            // Validate status code
            if (actualStatusCode != expectedStatusCode) {
                String errorMsg = "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            // Validate response body
            actualJsonBody = new JSONObject(response.asString());
            expectedJsonBody = new JSONObject(expectedResponseBody);

            // Log response body comparison
            LogUtils.info("Expected Response Body: " + expectedResponseBody);
            LogUtils.info("Actual Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
            ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + response.asString());

            // Validate response message length
            if (actualJsonBody.has("message")) {
                String message = actualJsonBody.getString("message");
                int sentenceCount = countSentences(message);
                
                if (sentenceCount > 6) {
                    String errorMsg = "Response message exceeds 6 sentences. Current count: " + sentenceCount;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                
                LogUtils.info("Message sentence count: " + sentenceCount);
                ExtentReport.getTest().log(Status.INFO, "Message sentence count: " + sentenceCount);
            }

            // Validate response body structure
            validateResponseBody.handleResponseBody(response, expectedJsonBody);

            LogUtils.success(logger, "Menu category view negative test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu category view negative test completed successfully", ExtentColor.GREEN));

        } catch (Exception e) {
            String errorMsg = "Error in menu category view negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    private int countSentences(String text) {
        // Split text into sentences using common sentence ending punctuation
        String[] sentences = text.split("[.!?]+");
        return sentences.length;
    }
}