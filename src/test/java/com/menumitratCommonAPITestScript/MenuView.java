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
public class MenuView extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuRequest menuViewRequest;
    private URL url;
    Logger logger = LogUtils.getLogger(MenuView.class);
    private JSONObject expectedResponse;
    private JSONObject actualJsonBody;

    @DataProvider(name = "getMenuViewUrl")
    private Object[][] getMenuViewUrl() throws customException
    {
        try {
            LogUtils.info("Reading menu view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading menu view URL from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "menuview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No menu view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved menu view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved menu view URL data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getMenuViewData")
    public Object[][] getMenuViewData() throws customException {
        try {
            LogUtils.info("Reading menu view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu view test scenario data");

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
                        "menuview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " menu view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " menu view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getMenuViewData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void menuViewSetUp() throws customException {
        try {
            LogUtils.info("Setting up menu view test");
            ExtentReport.createTest("Menu View Test Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing menu view test setup");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] menuViewData = getMenuViewUrl();
            if (menuViewData.length > 0) {
                String endpoint = menuViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No menu view URL found in test data";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            accessToken = TokenManagers.getJwtToken();
            if (accessToken.isEmpty()) {
                String errorMsg = "Required tokens not found. Please ensure login and OTP verification is completed";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            menuViewRequest = new MenuRequest();
            LogUtils.info("Menu view test setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu view test setup completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            String errorMsg = "Error in menu view setUp: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getMenuViewData")
    private void verifyMenuView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu view test case: " + testCaseid);
            ExtentReport.createTest("Menu View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("menuview")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Only set outlet_id and menu_id as required
                menuViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                menuViewRequest.setMenu_id(requestBodyJson.getString("menu_id"));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, menuViewRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate status code only
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Only display response body without validation
                LogUtils.success(logger, "Menu view test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu view test completed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
            }
        }
        catch (Exception e) {
            String errorMsg = "Error in menu view test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getMenuViewNegativeData")
    public Object[][] getMenuViewNegativeData() throws customException {
        try {
            LogUtils.info("Reading menu view negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu view negative test scenario data");
            
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
                        "menuview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu view negative test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting menu view negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu view negative test data: " + e.getMessage());
            throw new customException("Error in getting menu view negative test data: " + e.getMessage());
        }
    }
    
    /**
     * Checks if a message has more than the allowed number of sentences
     * @param message The message to check
     * @param maxAllowed Maximum allowed number of sentences
     * @return true if within limits, false if exceeds
     */
    private boolean validateSentenceCount(String message, int maxAllowed) {
        if (message == null || message.isEmpty()) {
            return true;
        }
        
        // Use regex to count sentences ending with period, question mark, or exclamation mark
        String[] sentences = message.split("[.!?]+\\s*");
        return sentences.length <= maxAllowed;
    }
    
    @Test(dataProvider = "getMenuViewNegativeData")
    public void menuViewNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu view negative test case: " + testCaseid);
            ExtentReport.createTest("Menu View Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Validate API name and test type
            if (!apiName.equalsIgnoreCase("menuview")) {
                String errorMsg = "Invalid API name: " + apiName + ". Expected: menuview";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid test type: " + testType + ". Expected: negative";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Set payload for menu view request
            if (requestBodyJson.has("outlet_id")) {
            	 menuViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                
            }
            
            if (requestBodyJson.has("menu_id")) {
            	 menuViewRequest.setMenu_id(requestBodyJson.getString("menu_id"));
            }
            
            response = ResponseUtil.getResponseWithAuth(baseURI, menuViewRequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            
            // Log expected vs actual status code
            int expectedStatusCode = Integer.parseInt(statusCode);
            ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + expectedStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
            
            // Check for server errors
            if (response.getStatusCode() == 500 || response.getStatusCode() == 502) {
                LogUtils.failure(logger, "Server error detected with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error detected: " + response.getStatusCode(), ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
            }
            // Validate status code
            else if (response.getStatusCode() != expectedStatusCode) {
                LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
            }
            else {
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                
                // Validate response body
                actualJsonBody = new JSONObject(response.asString());
                
                // Log expected vs actual response body
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
                }
                ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + actualJsonBody.toString(2));
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    // Validate response message
                    if (expectedResponse.has("detail") && actualJsonBody.has("detail")) {
                        String expectedDetail = expectedResponse.getString("detail");
                        String actualDetail = expectedResponse.getString("detail");
                        
                        // Check sentence count limitation
                        boolean validSentenceCount = validateSentenceCount(actualDetail, 6);
                        if (!validSentenceCount) {
                            String errorMsg = "Response message contains more than 6 sentences. Message: " + actualDetail;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Response message validation failed", ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, errorMsg);
                        } else {
                            LogUtils.info("Response message sentence count validation passed");
                            ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed");
                        }
                        
                        if (expectedDetail.equals(actualDetail)) {
                            LogUtils.info("Error message validation passed: " + actualDetail);
                            ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
                        } else {
                            LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
                            ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedDetail + ", Actual: " + actualDetail);
                        }
                    }
                    
                    // Complete response validation
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                }
                
                LogUtils.success(logger, "Menu view negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu view negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        } catch (Exception e) {
            String errorMsg = "Error in menu view negative test: " + e.getMessage();
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