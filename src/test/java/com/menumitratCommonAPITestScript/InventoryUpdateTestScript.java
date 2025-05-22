package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Listeners(Listener.class)
public class InventoryUpdateTestScript extends APIBase
{
    private InventoryRequest inventoryUpdateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseURI;
    private JSONObject requestBodyJson;
    private URL url;
    private int user_id;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(InventoryUpdateTestScript.class);

    /**
     * Data provider for inventory update API endpoint URLs
     */
    @DataProvider(name = "getInventoryUpdateUrl")
    public static Object[][] getInventoryUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Inventory Update API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "inventoryUpdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Inventory Update API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Inventory Update API endpoint data from Excel sheet");
            throw new customException("Error While Reading Inventory Update API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for inventory update test scenarios
     */
    @DataProvider(name = "getInventoryUpdateData")
    public static Object[][] getInventoryUpdateData() throws customException {
        try {
            LogUtils.info("Reading positive inventory update test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive inventory update test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No inventory update test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "inventoryupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive inventory update test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive inventory update test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive inventory update test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive inventory update test scenario data: " + e.getMessage();
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
            LogUtils.info("====Starting setup for inventory update test====");
            ExtentReport.createTest("Inventory Update Setup"); 
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            // Get base URL
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
           
            // Get and set inventory update URL
            Object[][] inventoryUpdateData = getInventoryUpdateUrl();
            if (inventoryUpdateData.length > 0) {
                String endpoint = inventoryUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for inventory update: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No inventory update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No inventory update URL found in test data");
                throw new customException("No inventory update URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            
            inventoryUpdateRequest = new InventoryRequest();
            LogUtils.success(logger, "Inventory Update Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory Update Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during inventory update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during inventory update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to update inventory
     */
    @Test(dataProvider = "getInventoryUpdateData")
    private void updateInventory(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting inventory update test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Inventory Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Request preparation
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            requestBodyJson = new JSONObject(requestBodyPayload);
            
            // Set request parameters
            inventoryUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            inventoryUpdateRequest.setUser_id(String.valueOf(user_id));
            inventoryUpdateRequest.setName(requestBodyJson.getString("name"));
            inventoryUpdateRequest.setSupplier_id(requestBodyJson.getString("supplier_id"));
            inventoryUpdateRequest.setCategory_id(requestBodyJson.getString("category_id"));
            inventoryUpdateRequest.setDescription(requestBodyJson.getString("description"));
            inventoryUpdateRequest.setUnit_price(requestBodyJson.getString("unit_price"));
            inventoryUpdateRequest.setQuantity(requestBodyJson.getString("quantity"));
            inventoryUpdateRequest.setUnit_of_measure(requestBodyJson.getString("unit_of_measure"));
            inventoryUpdateRequest.setReorder_level(requestBodyJson.getString("reorder_level"));
            inventoryUpdateRequest.setBrand_name(requestBodyJson.getString("brand_name"));
            inventoryUpdateRequest.setTax_rate(requestBodyJson.getString("tax_rate"));
            
            // Make API call
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryUpdateRequest, httpsmethod, accessToken);
            
            // Log response
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Mark test as passed
            LogUtils.success(logger, "Inventory update test completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Inventory update test completed successfully");
            
        } catch (Exception e) {
            String errorMsg = "Error in inventory update test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    
   
    
    /**
     * Count the number of sentences in a given text
     * @param text Text to analyze
     * @return Number of sentences
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // Pattern to match sentence endings (period, exclamation mark, question mark followed by space or end of string)
        Pattern sentencePattern = Pattern.compile("[.!?](?:\\s|$)");
        String[] sentences = sentencePattern.split(text);
        
        // Return count of non-empty sentences
        return (int) Arrays.stream(sentences)
                .filter(s -> s != null && !s.trim().isEmpty())
                .count();
    }
    
    //@Test(dataProvider = "getInventoryUpdateNegativeData")
    public void inventoryUpdateNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting inventory update negative test case: " + testCaseid);
            ExtentReport.createTest("Inventory Update Negative Test - " + testCaseid + ": " + description);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Verify API name and test type
            if (!apiName.equalsIgnoreCase("inventoryupdate")) {
                String errorMsg = "Invalid API name: " + apiName + ". Expected 'inventoryupdate'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            if (!testType.equalsIgnoreCase("negative")) {
                String errorMsg = "Invalid test type: " + testType + ". Expected 'negative'";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            requestBodyJson = new JSONObject(requestBody);
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Set payload for inventory update request - adjust these fields as needed for your API
            inventoryUpdateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            inventoryUpdateRequest.setUser_id(String.valueOf(user_id));
            inventoryUpdateRequest.setName(requestBodyJson.getString("name"));
            inventoryUpdateRequest.setSupplier_id(requestBodyJson.getString("supplier_id"));
            inventoryUpdateRequest.setCategory_id(requestBodyJson.getString("category_id"));
            inventoryUpdateRequest.setDescription(requestBodyJson.getString("description"));
            inventoryUpdateRequest.setUnit_price(requestBodyJson.getString("unit_price"));
            inventoryUpdateRequest.setQuantity(requestBodyJson.getString("quantity"));
            inventoryUpdateRequest.setUnit_of_measure(requestBodyJson.getString("unit_of_measure"));
            inventoryUpdateRequest.setReorder_level(requestBodyJson.getString("reorder_level"));
            inventoryUpdateRequest.setBrand_name(requestBodyJson.getString("brand_name"));
            inventoryUpdateRequest.setTax_rate(requestBodyJson.getString("tax_rate"));
            inventoryUpdateRequest.setIn_or_out(requestBodyJson.getString("in_or_out"));
            inventoryUpdateRequest.setIn_date(requestBodyJson.getString("in_date"));
            inventoryUpdateRequest.setExpiration_date(requestBodyJson.getString("expiration_date"));
            inventoryUpdateRequest.setInventory_id(requestBodyJson.getString("inventory_id"));
            
            // Add other fields as needed for inventory update
            
            response = ResponseUtil.getResponseWithAuth(baseURI, inventoryUpdateRequest, httpsmethod, accessToken);
            
            int actualStatusCode = response.getStatusCode();
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            LogUtils.info("Response Status Code - Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code - Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Check for server errors
            if (actualStatusCode == 500 || actualStatusCode == 502) {
                LogUtils.failure(logger, "Server error detected with status code: " + actualStatusCode);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Server error detected: " + actualStatusCode, ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
            }
            // Validate status code
            else if (actualStatusCode != expectedStatusCode) {
                LogUtils.failure(logger, "Status code mismatch - Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Status code mismatch", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
            }
            else {
                LogUtils.success(logger, "Status code validation passed: " + actualStatusCode);
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + actualStatusCode);
                
                // Validate response body
                actualResponseBody = new JSONObject(response.asString());
                
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                	expectedResponse = new JSONObject(expectedResponseBody);
                    
                    // Validate response message
                    if (expectedResponse.has("detail") && actualResponseBody.has("detail")) {
                        String expectedDetail = expectedResponse.getString("detail");
                        String actualDetail = expectedResponse.getString("detail");
                        
                        // Validate sentence count in response message
                        int sentenceCount = countSentences(actualDetail);
                        if (sentenceCount > 6) {
                            String errorMsg = "Error message contains more than 6 sentences. Actual count: " + sentenceCount;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        } else {
                            LogUtils.info("Error message sentence count validation passed: " + sentenceCount + " sentences");
                            ExtentReport.getTest().log(Status.PASS, "Error message sentence count validation passed: " + sentenceCount + " sentences");
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
                                    }
                
                LogUtils.success(logger, "Inventory update negative test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Inventory update negative test completed successfully", ExtentColor.GREEN));
            }
            
            // Always log the full response
            ExtentReport.getTest().log(Status.INFO, "Full Response:");
            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
        } catch (Exception e) {
            String errorMsg = "Error in inventory update negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
    
    
    
    
    //@AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down started===");
            ExtentReport.createTest("Inventory Update Test Teardown");
            
            LogUtils.info("Logging out user");
            ActionsMethods.logout();
            
            LogUtils.info("Clearing tokens");
            TokenManagers.clearTokens();
            
            LogUtils.info("===Test environment tear down completed successfully===");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}
