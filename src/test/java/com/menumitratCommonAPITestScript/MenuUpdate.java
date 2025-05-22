package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(Listener.class)
public class MenuUpdate extends APIBase {
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
    private static Logger logger = LogUtils.getLogger(MenuUpdate.class);

    /**
     * Helper method to safely convert Excel cell value to String
     */
    private static String safeConvertToString(Object value) {
        if (value == null) {
            return "";
        }
        try {
            if (value instanceof Number) {
                // Handle numeric values without decimal places
                if (value instanceof Double && ((Double) value) == ((Double) value).intValue()) {
                    return String.valueOf(((Double) value).intValue());
                }
                // Handle numeric values with decimal places
                return String.valueOf(value);
            } else if (value instanceof Boolean) {
                return String.valueOf(value);
            } else if (value instanceof String) {
                String strValue = ((String) value).trim();
                // Try to parse numeric strings and convert them to proper format
                try {
                    // Check if it's a decimal number
                    if (strValue.contains(".")) {
                        double doubleValue = Double.parseDouble(strValue);
                        if (doubleValue == (int) doubleValue) {
                            return String.valueOf((int) doubleValue);
                        }
                        return String.valueOf(doubleValue);
                    }
                    // Check if it's an integer
                    long longValue = Long.parseLong(strValue);
                    return String.valueOf(longValue);
                } catch (NumberFormatException e) {
                    // If parsing fails, return the original string
                    return strValue;
                }
            } else {
                return String.valueOf(value).trim();
            }
        } catch (Exception e) {
            LogUtils.error("Error converting value to string: " + e.getMessage());
            return "";
        }
    }

    /**
     * Data provider for menu update API endpoint URLs
     */
    @DataProvider(name = "getMenuUpdateUrl")
    public static Object[][] getMenuUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Update API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading Menu Update API endpoint data from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                throw new customException("No data found in Excel sheet or sheet is empty");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3) {
                    String apiName = safeConvertToString(row[0]);
                    if ("menuUpdate".equalsIgnoreCase(apiName)) {
                        Object[] convertedRow = new Object[row.length];
                        for (int i = 0; i < row.length; i++) {
                            convertedRow[i] = safeConvertToString(row[i]);
                        }
                        filteredData.add(convertedRow);
                    }
                }
            }

            if (filteredData.isEmpty()) {
                throw new customException("No valid menu update API endpoint data found in Excel sheet");
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            String errorMsg = "Error While Reading Menu Update API endpoint data from Excel sheet: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }

    /**
     * Data provider for menu update test scenarios
     */
    @DataProvider(name = "getMenuUpdateData")
    public static Object[][] getMenuUpdateData() throws customException {
        try {
            LogUtils.info("Reading menu update test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu update test scenario data");

            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");

            if (testData == null || testData.length == 0) {
                throw new customException("No data found in Excel sheet or sheet is empty");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (Object[] row : testData) {
                if (row != null && row.length >= 3) {
                    String apiName = safeConvertToString(row[0]);
                    String testType = safeConvertToString(row[2]);
                    String testCaseId = safeConvertToString(row[1]);

                    if ("menuUpdate".equalsIgnoreCase(apiName) && 
                        "positive".equalsIgnoreCase(testType) &&
                        "menuupdate_001".equalsIgnoreCase(testCaseId)) {
                        
                        Object[] convertedRow = new Object[row.length];
                        for (int i = 0; i < row.length; i++) {
                            convertedRow[i] = safeConvertToString(row[i]);
                        }
                        
                        // Validate required fields for menuupdate_001
                        boolean isValidRow = true;
                        String[] requiredFields = {"menu_id", "outlet_id", "menu_cat_id", "name", "food_type", 
                                                 "description", "spicy_index", "portion_data"};
                        
                        try {
                            JSONObject requestBody = new JSONObject(convertedRow[5].toString());
                            for (String field : requiredFields) {
                                if (!requestBody.has(field) || requestBody.isNull(field) || 
                                    requestBody.getString(field).trim().isEmpty()) {
                                    isValidRow = false;
                                    LogUtils.warn("Required field '" + field + "' is missing or empty in menuupdate_001");
                                    break;
                                }
                            }
                            
                            // Validate portion_data array
                            if (isValidRow && requestBody.has("portion_data")) {
                                JSONArray portionData = requestBody.getJSONArray("portion_data");
                                if (portionData.length() == 0) {
                                    isValidRow = false;
                                    LogUtils.warn("portion_data array is empty in menuupdate_001");
                                }
                            }
                        } catch (Exception e) {
                            isValidRow = false;
                            LogUtils.error("Error validating request body for menuupdate_001: " + e.getMessage());
                        }
                        
                        if (isValidRow) {
                            filteredData.add(convertedRow);
                        }
                    }
                }
            }

            if (filteredData.isEmpty()) {
                throw new customException("No valid menu update test scenario data found for menuupdate_001");
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            String errorMsg = "Error while reading menu update test scenario data: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.ERROR, errorMsg);
            throw new customException(errorMsg);
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for menu update test====");
            ExtentReport.createTest("Menu Update Setup");

            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseUri);

            // Get and set menu update URL
            Object[][] menuUpdateData = getMenuUpdateUrl();
            if (menuUpdateData != null && menuUpdateData.length > 0) {
                String endpoint = String.valueOf(menuUpdateData[0][2]);
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Constructed base URI for menu update: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseUri);
            } else {
                LogUtils.failure(logger, "No menu update URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu update URL found in test data");
                throw new customException("No menu update URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            menuRequest = new MenuRequest();
            LogUtils.success(logger, "Menu Update Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Menu Update Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error during menu update setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during menu update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to update menu
     */
    @Test(dataProvider = "getMenuUpdateData", priority=0)
    private void updateMenu(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        
        RequestSpecification request = null;
        String menuId = null;
        
        try {
            LogUtils.info("Starting menu update test case: " + testCaseid);
            ExtentReport.createTest("Menu Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // Parse request payload
            LogUtils.info("Parsing request payload from Excel");
            try {
                requestBodyJson = new JSONObject(requestBodyPayload);
                LogUtils.info("Request payload parsed successfully");
                
                // Validate required fields
                String[] requiredFields = {"menu_id", "outlet_id", "menu_cat_id", "name", "food_type", 
                                         "description", "spicy_index"};
                for (String field : requiredFields) {
                    if (!requestBodyJson.has(field) || requestBodyJson.isNull(field) || 
                        requestBodyJson.getString(field).trim().isEmpty()) {
                        throw new customException("Required field '" + field + "' is missing or empty");
                    }
                }

                // Validate numeric fields
                String[] numericFields = {"spicy_index", "rating", "cgst", "sgst"};
                for (String field : numericFields) {
                    if (requestBodyJson.has(field) && !requestBodyJson.isNull(field)) {
                        try {
                            double value = Double.parseDouble(requestBodyJson.getString(field));
                            if (value < 0) {
                                throw new customException(field + " cannot be negative");
                            }
                        } catch (NumberFormatException e) {
                            throw new customException("Invalid " + field + " format. Must be a number.");
                        }
                    }
                }
                
                // Validate menu_id
                menuId = requestBodyJson.getString("menu_id");
                if (menuId == null || menuId.trim().isEmpty()) {
                    throw new customException("Menu ID is required and cannot be empty");
                }
                
                // For menuupdate_001, validate menu ID exists before proceeding
                if ("menuupdate_001".equalsIgnoreCase(testCaseid)) {
                    if (!validateMenuId(menuId)) {
                        String errorMsg = "Menu ID " + menuId + " does not exist in the system";
                        LogUtils.error(errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, errorMsg);
                        throw new customException(errorMsg);
                    }
                }
                
                // Validate portion data
                if (requestBodyJson.has("portion_data")) {
                    JSONArray portionData = requestBodyJson.getJSONArray("portion_data");
                    if (portionData.length() == 0) {
                        throw new customException("portion_data array cannot be empty");
                    }
                    
                    for (int i = 0; i < portionData.length(); i++) {
                        JSONObject portion = portionData.getJSONObject(i);
                        String[] portionRequiredFields = {"portion_name", "price", "unit_value", "unit_type", "flag"};
                        
                        for (String field : portionRequiredFields) {
                            if (!portion.has(field) || portion.isNull(field) || 
                                portion.getString(field).trim().isEmpty()) {
                                throw new customException("Required field '" + field + "' is missing in portion_data at index " + i);
                            }
                        }
                        
                        // Validate price is numeric and positive
                        try {
                            double price = Double.parseDouble(portion.getString("price"));
                            if (price <= 0) {
                                throw new customException("Price must be greater than 0 in portion_data at index " + i);
                            }
                        } catch (NumberFormatException e) {
                            throw new customException("Invalid price format in portion_data at index " + i);
                        }
                    }
                }
                
            } catch (Exception e) {
                LogUtils.error("Failed to parse request payload: " + e.getMessage());
                ExtentReport.getTest().log(Status.ERROR, "Failed to parse request payload: " + e.getMessage());
                throw new customException("JSON parsing error: " + e.getMessage());
            }
            
            // Initialize request
            LogUtils.info("Initializing multipart request");
            request = RestAssured.given()
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType("multipart/form-data");
            
            // Add form fields from Excel data
            try {
                LogUtils.info("Adding form fields from Excel data");
                
                // Add user_id (use from token if not in payload)
                if (requestBodyJson.has("user_id")) {
                    request.multiPart("user_id", requestBodyJson.getString("user_id"));
                } else {
                    request.multiPart("user_id", String.valueOf(userId));
                }
                
                // Add required fields
                request.multiPart("menu_id", menuId);
                request.multiPart("outlet_id", requestBodyJson.getString("outlet_id"));
                request.multiPart("menu_cat_id", requestBodyJson.getString("menu_cat_id"));
                request.multiPart("name", requestBodyJson.getString("name"));
                request.multiPart("food_type", requestBodyJson.getString("food_type"));
                request.multiPart("description", requestBodyJson.getString("description"));
                request.multiPart("spicy_index", requestBodyJson.getString("spicy_index"));
                
                // Handle portion data
                if (requestBodyJson.has("portion_data")) {
                    JSONArray portionData = requestBodyJson.getJSONArray("portion_data");
                    request.multiPart("portion_data", portionData.toString());
                    
                    // Add individual portion fields for backward compatibility
                    JSONObject firstPortion = portionData.getJSONObject(0);
                    request.multiPart("portion_name", firstPortion.getString("portion_name"));
                    request.multiPart("price", firstPortion.getString("price"));
                    request.multiPart("unit_value", firstPortion.getString("unit_value"));
                    request.multiPart("unit_type", firstPortion.getString("unit_type"));
                    request.multiPart("flag", firstPortion.getString("flag"));
                }
                
                // Add optional fields if present
                if (requestBodyJson.has("ingredients")) {
                    request.multiPart("ingredients", requestBodyJson.getString("ingredients"));
                }
                if (requestBodyJson.has("offer")) {
                    request.multiPart("offer", requestBodyJson.getString("offer"));
                }
                if (requestBodyJson.has("rating")) {
                    request.multiPart("rating", requestBodyJson.getString("rating"));
                }
                if (requestBodyJson.has("cgst")) {
                    request.multiPart("cgst", requestBodyJson.getString("cgst"));
                }
                if (requestBodyJson.has("sgst")) {
                    request.multiPart("sgst", requestBodyJson.getString("sgst"));
                }
                
                // Handle existing image IDs
                if (requestBodyJson.has("existing_image_ids")) {
                    JSONArray array = requestBodyJson.getJSONArray("existing_image_ids");
                    for (int i = 0; i < array.length(); i++) {
                        request.multiPart("existing_image_ids[]", array.get(i).toString());
                    }
                }
                
                // Process images if present
                if (requestBodyJson.has("images") && !requestBodyJson.isNull("images")) {
                    JSONArray imagesArray = requestBodyJson.getJSONArray("images");
                    processImageArray(request, imagesArray);
                }
                
                LogUtils.info("All form fields added successfully");
            } catch (Exception e) {
                LogUtils.error("Error adding form fields: " + e.getMessage());
                ExtentReport.getTest().log(Status.ERROR, "Error adding form fields: " + e.getMessage());
                throw new customException("Error adding form fields: " + e.getMessage());
            }
            
            // Log the prepared request
            LogUtils.info("Request prepared successfully");
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString(2));
            
            // Send the request
            LogUtils.info("Sending PUT request to " + baseUri);
            response = request.put(baseUri);
            
            // Log response details
            int actualStatusCode = response.getStatusCode();
            String responseBody = response.asPrettyString();
            LogUtils.info("Response Status Code: " + actualStatusCode);
            LogUtils.info("Response Body: " + responseBody);
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + actualStatusCode);
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + responseBody);
            
            // Validate response
            if (actualStatusCode == Integer.parseInt(statusCode)) {
                JSONObject responseJson = new JSONObject(responseBody);
                
                // For positive test cases
                if ("menuupdate_001".equalsIgnoreCase(testCaseid)) {
                    if (responseJson.has("message")) {
                        String message = responseJson.getString("message");
                        if (message.contains("success") || message.contains("updated")) {
                            LogUtils.success(logger, "Menu update test passed - Status Code and message match");
                            ExtentReport.getTest().log(Status.PASS, 
                                MarkupHelper.createLabel("Menu updated successfully", ExtentColor.GREEN));
                        } else {
                            String errorMsg = "Unexpected response message: " + message;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, errorMsg);
                            throw new customException(errorMsg);
                        }
                    } else {
                        String errorMsg = "Response missing expected 'message' field";
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, errorMsg);
                        throw new customException(errorMsg);
                    }
                }
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + actualStatusCode;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = "Menu update failed: " + e.getMessage();
            LogUtils.error(errorMsg);
            e.printStackTrace();
            
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
    
    /**
     * Helper method to process a JSON array of image paths
     */
    private void processImageArray(RequestSpecification request, JSONArray imagesArray) {
        for (int i = 0; i < imagesArray.length(); i++) {
            try {
                String imagePath = imagesArray.getString(i);
                addImageToRequest(request, imagePath);
            } catch (Exception e) {
                LogUtils.error("Error processing image at index " + i + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Helper method to add a single image to the request
     */
    private void addImageToRequest(RequestSpecification request, String imagePath) {
        try {
            LogUtils.info("Processing image path: " + imagePath);
            
            // Normalize path by replacing backslashes
            String normalizedPath = imagePath.replace("\\", "/").trim();
            
            File imgFile = new File(normalizedPath);
            if (imgFile.exists() && imgFile.isFile()) {
                LogUtils.info("Image file exists, adding to request: " + imgFile.getAbsolutePath());
                request.multiPart("images", imgFile);
            } else {
                LogUtils.warn("Image file does not exist or is not a file: " + normalizedPath);
                ExtentReport.getTest().log(Status.WARNING, "Image file not found: " + normalizedPath);
            }
        } catch (Exception e) {
            LogUtils.error("Error adding image to request: " + e.getMessage());
        }
    }

    /**
     * Helper method to validate menu ID before update
     */
    private boolean validateMenuId(String menuId) {
        try {
            // Create a GET request to check if menu exists
            Response checkResponse = RestAssured.given()
                    .header("Authorization", "Bearer " + accessToken)
                    .get(baseUri + "/" + menuId);

            if (checkResponse.getStatusCode() == 200) {
                LogUtils.info("Menu ID " + menuId + " exists and is valid");
                return true;
            } else {
                LogUtils.error("Menu ID " + menuId + " does not exist or is invalid");
                return false;
            }
        } catch (Exception e) {
            LogUtils.error("Error validating menu ID: " + e.getMessage());
            return false;
        }
    }
}
