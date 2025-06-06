package com.menumitratCommonAPITestScript;

import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.apache.log4j.Logger;
import com.aventstack.extentreports.Status;
import com.menumitra.apiRequest.UdhariLedgerCreateRequest;
import com.menumitra.apiRequest.OrderRequest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

@Listeners(Listener.class)
public class UdhariCreateTestScript extends APIBase {
    private static final String API_NAME = "udharicreate";
    
    private UdhariLedgerCreateRequest udhariLedgerCreateRequest;
    private OrderRequest orderRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private String baseURI;
    private String accessToken;
    private String userId;
    private URL url;
    Logger logger = LogUtils.getLogger(UdhariCreateTestScript.class);

    @DataProvider(name = "getUdhariCreateUrl")
    private Object[][] getUdhariCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Udhari Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> API_NAME.equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting udhari create URL: " + e.getMessage());
            throw new customException("Error in getting udhari create URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUdhariCreateData")
    public Object[][] getUdhariCreateData() throws customException {
        try {
            LogUtils.info("Reading udhari create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading udhari create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                        API_NAME.equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid udhari create test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + result.length + " test scenarios for udhari create");
            return result;

        } catch (Exception e) {
            String errorMsg = "Error while reading udhari create test scenario data: " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void udhariCreateSetUp() throws customException {
        try {
            LogUtils.info("====Starting setup for Udhari create test====");
            ExtentReport.createTest("Udhari Create Setup");
            ExtentReport.getTest().log(Status.INFO, "Udhari Create Setup");

            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            // Get URL from excel
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.failure(logger, "No udhari create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No udhari create URL found in test data");
                throw new customException("No udhari create URL found in test data");
            }

            // Find the udharicreate endpoint
            String endpoint = null;
            for (int i = 0; i < readExcelData.length; i++) {
                if (readExcelData[i] != null && readExcelData[i].length > 2 
                    && API_NAME.equalsIgnoreCase(String.valueOf(readExcelData[i][0]))) {
                    endpoint = String.valueOf(readExcelData[i][2]);
                    break;
                }
            }

            if (endpoint == null || endpoint.trim().isEmpty()) {
                LogUtils.failure(logger, "No udhari create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No udhari create URL found in test data");
                throw new customException("No udhari create URL found in test data");
            }

            url = new URL(endpoint);
            baseURI = RequestValidator.buildUri(endpoint, baseURI);
            LogUtils.info("Constructed base URI: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);

            accessToken = TokenManagers.getJwtToken();
            userId = String.valueOf(TokenManagers.getUserId());
            
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            udhariLedgerCreateRequest = new UdhariLedgerCreateRequest();
            LogUtils.success(logger, "Udhari Create Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Udhari Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in Udhari Create Setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in Udhari Create Setup: " + e.getMessage());
            throw new customException("Error in Udhari Create Setup: " + e.getMessage());
        }
    }

    private Long createTestOrder() throws customException {
        try {
            LogUtils.info("Creating test order for Udhari test");
            
            // Get base URL without the Udhari endpoint
            String baseUrl = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL for order creation: " + baseUrl);
            
            // Create order request body as a map
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("user_id", userId);
            orderRequest.put("outlet_id", "1");
            orderRequest.put("order_type", "parcel");
            orderRequest.put("payment_method", "cash");
            
            // Create order items
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("menu_id", 1);
            orderItem.put("quantity", 1);
            orderItem.put("portion_name", "full");
            orderItem.put("comment", "");
            
            List<Map<String, Object>> orderItems = new ArrayList<>();
            orderItems.add(orderItem);
            
            orderRequest.put("order_items", orderItems);
            orderRequest.put("action", "create");
            orderRequest.put("customer_name", "Test Customer");
            orderRequest.put("customer_mobile", "9876543210");
            
            // Make API call to create order
            String orderCreateUrl = baseUrl + "/v2/common/create_order";
            LogUtils.info("Order creation URL: " + orderCreateUrl);
            
            // Log request body for debugging
            LogUtils.info("Order creation request body: " + new JSONObject(orderRequest).toString(2));
            
            Response orderResponse = ResponseUtil.getResponseWithAuth(orderCreateUrl, orderRequest, "POST", accessToken);
            
            // Log response for debugging
            LogUtils.info("Order creation response code: " + orderResponse.getStatusCode());
            LogUtils.info("Order creation response body: " + orderResponse.asString());
            
            if (orderResponse.getStatusCode() != 201 && orderResponse.getStatusCode() != 200) {
                throw new customException("Failed to create test order. Status: " + orderResponse.getStatusCode() + ", Response: " + orderResponse.asString());
            }
            
            // Extract order ID from response
            JSONObject orderResponseJson = new JSONObject(orderResponse.asString());
            Long orderId = orderResponseJson.getLong("order_id");
            LogUtils.info("Successfully created test order with ID: " + orderId);
            return orderId;
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error creating test order: " + e.getMessage());
            throw new customException("Error creating test order: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUdhariCreateData")
    public void testCreateUdhariLedger(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting Create Udhari Ledger Test - " + testCaseid);
            ExtentReport.createTest("Create Udhari Ledger Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            // First create a test order
            Long orderId = createTestOrder();
            
            // Parse request body
            requestBodyJson = new JSONObject(requestBody);
            
            // Update the order ID in the request with our newly created order
            requestBodyJson.put("order_id", orderId);

            // Initialize request object
            udhariLedgerCreateRequest.setUserId(Long.parseLong(userId));
            udhariLedgerCreateRequest.setCustomerName(requestBodyJson.getString("customer_name"));
            udhariLedgerCreateRequest.setCustomerMobile(requestBodyJson.getString("customer_mobile"));
            udhariLedgerCreateRequest.setCustomerAddress(requestBodyJson.getString("customer_address"));
            udhariLedgerCreateRequest.setOrderId(orderId);
            udhariLedgerCreateRequest.setBillAmount(requestBodyJson.getDouble("bill_amount"));
            udhariLedgerCreateRequest.setEstimatedSettlementPeriod(requestBodyJson.getString("estimated_settlement_period"));

            // Make API call
            LogUtils.info("Making API call to: " + baseURI);
            response = ResponseUtil.getResponseWithAuth(baseURI, udhariLedgerCreateRequest, httpsmethod, accessToken);

            // Log response details
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

            // Validate response
            int actualStatusCode = response.getStatusCode();
            int expectedStatusCode = Integer.parseInt(statusCode);
            
            if (actualStatusCode != expectedStatusCode && !(expectedStatusCode == 200 && actualStatusCode == 201)) {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + actualStatusCode;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Log success
            LogUtils.success(logger, "Create Udhari Ledger Test completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Create Udhari Ledger Test completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in Create Udhari Ledger Test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in Create Udhari Ledger Test: " + e.getMessage());
            throw new customException("Error in Create Udhari Ledger Test: " + e.getMessage());
        }
    }
}
