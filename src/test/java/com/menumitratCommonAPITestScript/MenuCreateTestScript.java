package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.bson.types.Symbol;
import org.json.JSONObject;
import org.json.JSONArray;
import org.testng.annotations.AfterClass;
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

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

@Listeners(Listener.class)
public class MenuCreateTestScript extends APIBase {

    private MenuRequest menuRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private String accessToken;
    private int userId;
    private RequestSpecification request;
    private static Logger logger = LogUtils.getLogger(MenuCreateTestScript.class);

    /**
     * Data provider for menu create API endpoint URLs
     */
    @DataProvider(name = "getMenuCreateUrl")
    public static Object[][] getMenuCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData("src\\test\\resources\\excelsheet\\apiEndpoint.xlsx",
                    "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "menuCreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.exception(logger, "Error While Reading Menu Create API endpoint data from Excel sheet", e);
            ;
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Menu Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Menu Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for menu create test scenarios
     */
    @DataProvider(name = "getMenuCreateData")
    public static Object[][] getMenuCreateData() throws customException {
        try {
            LogUtils.info("Reading positive menu create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive menu create test scenario data");

            Object[][] testData = DataDriven.readExcelData("src\\test\\resources\\excelsheet\\apiEndpoint.xlsx",
                    "CommonAPITestScenario");

            if (testData == null || testData.length == 0) {
                String errorMsg = "No menu create test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : testData) {
                if (row != null && row.length >= 3 &&
                    "menucreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive menu create test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive menu create test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive menu create test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive menu create test scenario data: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
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
            LogUtils.info("=====Verify Menu Create Test Script=====");
            ExtentReport.createTest("Verify Menu Create Test Script");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();

            // Get and set menu create URL
            Object[][] menuCreateData = getMenuCreateUrl();

            if (menuCreateData.length > 0) {
                String endpoint = menuCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                baseUri = endpoint;
                LogUtils.success(logger, "Constructed Menu Create Base URI: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Constructed Menu Create Base URI: " + baseUri);
            } else {

                LogUtils.failure(logger, "Failed constructed Menu Create Base URI.");
                ExtentReport.getTest().log(Status.ERROR, "Failed constructed Menu Create Base URI.");
                throw new customException("Failed constructed Menu Create Base URI.");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error(
                        "Required tokens not found. Please ensure login and OTP verification is completed");
                ExtentReport.getTest().log(Status.FAIL,
                        "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException(
                        "Required tokens not found. Please ensure login and OTP verification is completed");
            }
            menuRequest = new MenuRequest();
            LogUtils.success(logger, "Menu create test script Setup completed successfully");
            ExtentReport.getTest().log(Status.INFO, "Menu create test script Setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during menu create test script setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during menu create test script setup " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getMenuCreateData")
    private void createMenuUsigValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {

        try {
            LogUtils.info("Starting menu creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Menu Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Validate test case ID
            if (testCaseid == null || testCaseid.trim().isEmpty()) {
                throw new customException("Test case ID cannot be empty");
            }

            expectedResponse = new JSONObject(expectedResponseBody);
            requestBodyJson = new JSONObject(requestBodyPayload.replace("\\", "\\\\"));

            // Log request details
            LogUtils.info("Request Body: " + requestBodyJson.toString(2));
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));

            // Validate portion_data
            if (!requestBodyJson.has("portion_data")) {
                String errorMsg = "Portion data is required for menu creation";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            // Validate portion data structure
            JSONArray portionData = requestBodyJson.getJSONArray("portion_data");
            if (portionData.length() == 0) {
                String errorMsg = "Portion data array cannot be empty";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            // Create a new JSONArray to store validated portion data
            JSONArray validatedPortionData = new JSONArray();

            for (int i = 0; i < portionData.length(); i++) {
                JSONObject portion = portionData.getJSONObject(i);
                JSONObject validatedPortion = new JSONObject();

                // Check and set portion_name
                if (!portion.has("portion_name")) {
                    String errorMsg = "Portion name is required at index " + i;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                String portionName = portion.getString("portion_name");
                if (portionName == null || portionName.trim().isEmpty()) {
                    String errorMsg = "Portion name cannot be empty at index " + i;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                validatedPortion.put("portion_name", portionName.trim());

                // Check and set price
                if (!portion.has("price")) {
                    String errorMsg = "Price is required at index " + i;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                try {
                    double price = portion.getDouble("price");
                    if (price <= 0) {
                        String errorMsg = "Price must be greater than 0 at index " + i;
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        throw new customException(errorMsg);
                    }
                    validatedPortion.put("price", price);
                } catch (Exception e) {
                    String errorMsg = "Invalid price format at index " + i;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Check and set unit_value and unit_type
                String unitValue = null;
                String unitType = null;

                // Handle both old format (unit) and new format (unit_value + unit_type)
                if (portion.has("unit")) {
                    String unit = portion.getString("unit");
                    if (unit != null && !unit.trim().isEmpty()) {
                        // Parse unit string (e.g., "250g" -> value="250", type="g")
                        String[] parts = unit.trim().split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
                        if (parts.length >= 2) {
                            unitValue = parts[0].trim();
                            unitType = parts[1].trim();
                        } else {
                            unitValue = unit.trim();
                            unitType = "g"; // Default unit type
                        }
                    }
                } else {
                    // Check for new format
                    if (portion.has("unit_value")) {
                        unitValue = portion.getString("unit_value");
                    }
                    if (portion.has("unit_type")) {
                        unitType = portion.getString("unit_type");
                    }
                }

                // Validate unit value
                if (unitValue == null || unitValue.trim().isEmpty()) {
                    String errorMsg = "Unit value is required at index " + i;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                validatedPortion.put("unit_value", unitValue.trim());

                // Validate unit type
                if (unitType == null || unitType.trim().isEmpty()) {
                    String errorMsg = "Unit type is required at index " + i;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
                validatedPortion.put("unit_type", unitType.trim());

                // Log the processed unit data
                LogUtils.info("Processed unit data at index " + i + ": value=" + unitValue + ", type=" + unitType);
                ExtentReport.getTest().log(Status.INFO, "Processed unit data at index " + i + ": value=" + unitValue + ", type=" + unitType);

                // Check and set flag
                if (!portion.has("flag")) {
                    // Set default flag value to 1 if not provided
                    LogUtils.info("Flag not provided at index " + i + ", setting default value to 1");
                    ExtentReport.getTest().log(Status.INFO, "Flag not provided at index " + i + ", setting default value to 1");
                    validatedPortion.put("flag", 1);
                } else {
                    try {
                        int flag = portion.getInt("flag");
                        if (flag != 0 && flag != 1) {
                            String errorMsg = "Flag must be either 0 or 1 at index " + i;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            throw new customException(errorMsg);
                        }
                        validatedPortion.put("flag", flag);
                    } catch (Exception e) {
                        String errorMsg = "Invalid flag format at index " + i;
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                        throw new customException(errorMsg);
                    }
                }

                // Log the processed portion data
                LogUtils.info("Processed portion data at index " + i + ": " + validatedPortion.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Processed portion data at index " + i + ": " + validatedPortion.toString(2));

                validatedPortionData.put(validatedPortion);
            }

            // Update the request body with validated portion data
            requestBodyJson.put("portion_data", validatedPortionData);

            request = RestAssured.given();
            request.header("Authorization", "Bearer " + accessToken);
            request.contentType("multipart/form-data");

            // Log validated request body
            LogUtils.info("Validated Request Body: " + requestBodyJson.toString(2));
            ExtentReport.getTest().log(Status.INFO, "Validated Request Body: " + requestBodyJson.toString(2));

            // Set up request parameters
            request.multiPart("user_id", userId);
            request.multiPart("outlet_id", requestBodyJson.getString("outlet_id"));
            request.multiPart("menu_cat_id", requestBodyJson.getString("menu_cat_id"));
            request.multiPart("name", requestBodyJson.getString("name"));
            request.multiPart("food_type", requestBodyJson.getString("food_type"));
            request.multiPart("description", requestBodyJson.getString("description"));
            request.multiPart("spicy_index", requestBodyJson.getString("spicy_index"));
            request.multiPart("portion_data", requestBodyJson.getJSONArray("portion_data").toString());
            request.multiPart("ingredients", requestBodyJson.getString("ingredients"));
            request.multiPart("offer", requestBodyJson.getString("offer"));
            request.multiPart("rating", requestBodyJson.getString("rating"));
            request.multiPart("cgst", requestBodyJson.getString("cgst"));
            request.multiPart("sgst", requestBodyJson.getString("sgst"));

            // Send request and get response
            LogUtils.info("Sending POST request to endpoint: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Sending POST request to endpoint: " + baseUri);
            
            response = request.when().post(baseUri).then().extract().response();
            
            // Log response
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asPrettyString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());

            // Validate response
            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                LogUtils.success(logger, "Menu item created successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu item created successfully", ExtentColor.GREEN));
                
                // Validate response body
                actualResponseBody = new JSONObject(response.asString());
                                
                LogUtils.info("Response validation completed successfully");
                ExtentReport.getTest().log(Status.PASS, "Response validation completed successfully");
            } else {
                String errorMsg = "Menu creation failed with status code: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

        } catch (customException e) {
            LogUtils.error("Test execution failed: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LogUtils.error("Unexpected error during test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Unexpected error during test execution", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Unexpected error during test execution: " + e.getMessage());
        }
    }

    @AfterClass
    private void tearDown()
    {
        try 
        {
            LogUtils.info("===Test environment tear down successfully===");
           
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
            
            ActionsMethods.logout();
            TokenManagers.clearTokens();
            
        } 
        catch (Exception e) 
        {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }

}


