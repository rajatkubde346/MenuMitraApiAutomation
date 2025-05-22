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
import com.menumitra.apiRequest.InventoryRequest;
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
public class InventoryDeleteTestScript extends APIBase
{
    private InventoryRequest inventoryDeleteRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(InventoryDeleteTestScript.class);

    /**
     * Data provider for inventory delete API endpoint URLs
     */
    @DataProvider(name = "getInventoryDeleteUrl")
    public static Object[][] getInventoryDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Inventory Delete API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "inventoryDelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Inventory Delete API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Inventory Delete API endpoint data from Excel sheet");
            throw new customException("Error While Reading Inventory Delete API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for inventory delete test scenarios
     */
    @DataProvider(name = "getInventoryDeleteData")
    public static Object[][] getInventoryDeleteData() throws customException {
        try {
            LogUtils.info("Reading positive inventory delete test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive inventory delete test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No inventory delete test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "inventoryDelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive inventory delete test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive inventory delete test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive inventory delete test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive inventory delete test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

  
    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for inventory delete test====");
            ExtentReport.createTest("Inventory Delete Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set inventory delete URL
            Object[][] inventoryDeleteData = getInventoryDeleteUrl();
            if (inventoryDeleteData.length > 0) {
                String endpoint = inventoryDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for inventory delete: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No inventory delete URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No inventory delete URL found in test data");
                throw new customException("No inventory delete URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            inventoryDeleteRequest = new InventoryRequest();
            LogUtils.success(logger, "Inventory Delete Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory Delete Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during inventory delete setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during inventory delete setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to delete inventory
     */
    @Test(dataProvider = "getInventoryDeleteData")
    private void inventoryDeleteTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting inventory delete test case: " + testCaseid);
            ExtentReport.createTest("Inventory Delete Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("inventorydelete")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Set request parameters
                inventoryDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                inventoryDeleteRequest.setInventory_id(requestBodyJson.getString("inventory_id"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                // Make API call
                response = ResponseUtil.getResponseWithAuth(baseURI, inventoryDeleteRequest, httpsmethod, accessToken);
                
                // Log response
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Mark test as passed
                LogUtils.success(logger, "Inventory delete test completed successfully");
                ExtentReport.getTest().log(Status.PASS, "Inventory delete test completed successfully");
            }
        } catch (Exception e) {
            String errorMsg = "Error in inventory delete test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    /**
     * Helper method to count sentences in a string
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Count sentences by looking for sentence terminators (., !, ?)
        // followed by whitespace or end of string
        int count = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            char c = text.charAt(i);
            char next = text.charAt(i + 1);
            
            if ((c == '.' || c == '!' || c == '?') && (Character.isWhitespace(next) || i == text.length() - 2)) {
                count++;
            }
        }
        
        // Check if the last character is a sentence terminator
        char lastChar = text.charAt(text.length() - 1);
        if (lastChar == '.' || lastChar == '!' || lastChar == '?') {
            count++;
        }
        
        return count;
    }
}
