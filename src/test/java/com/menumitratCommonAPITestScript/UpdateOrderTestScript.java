package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
import com.google.gson.JsonArray;
import com.menumitra.apiRequest.OrderRequest;
import com.menumitra.apiRequest.OrderRequest.OrderItem;
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
public class UpdateOrderTestScript extends APIBase {
    private OrderRequest orderRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private JSONObject expectedJsonBody;
    Logger logger = LogUtils.getLogger(UpdateOrderTestScript.class);

    @BeforeClass
    private void updateOrderSetUp() throws customException {
        try {
            LogUtils.info("Update Order SetUp");
            ExtentReport.createTest("Update Order SetUp");
            ExtentReport.getTest().log(Status.INFO, "Update Order SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getUpdateOrderUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No update order URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No update order URL found in test data");
                throw new customException("No update order URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if(accessToken.isEmpty()) {
                LogUtils.failure(logger, "JWT token is empty");
                ExtentReport.getTest().log(Status.FAIL, "JWT token is empty");
                throw new customException("JWT token is empty");
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in update order setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in update order setup: " + e.getMessage());
            throw new customException("Error in update order setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUpdateOrderUrl")
    private Object[][] getUpdateOrderUrl() throws customException {
        try {
            LogUtils.info("Reading update order URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading update order URL from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "updateOrder".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No update order URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception while reading update order URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception while reading update order URL: " + e.getMessage());
            throw new customException("Exception while reading update order URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getUpdateDineInOrderData")
    private Object[][] getUpdateDineInOrderData() throws customException {
        try {
            LogUtils.info("Reading positive update dine-in order test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive update dine-in order test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "No update dine-in order test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "updateorderdineIn".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive update dine-in order test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive update dine-in order test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive update dine-in order test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive update dine-in order test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getUpdateDeliveryOrderData")
    private Object[][] getUpdateDeliveryOrderData() throws customException {
        try {
            LogUtils.info("Reading positive update delivery order test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive update delivery order test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "No update delivery order test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "updatedeliveryorder".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive update delivery order test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive update delivery order test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive update delivery order test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive update delivery order test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getUpdateParcelOrderData")
    private Object[][] getUpdateParcelOrderData() throws customException {
        try {
            LogUtils.info("Reading positive update parcel order test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive update parcel order test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "No update parcel order test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "updateorderparcel".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive update parcel order test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive update parcel order test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive update parcel order test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive update parcel order test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getUpdateDriveThroughOrderData")
    private Object[][] getUpdateDriveThroughOrderData() throws customException {
        try {
            LogUtils.info("Reading positive update drive-through order test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive update drive-through order test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "No update drive-through order test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "updateorderdrivethrough".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive update drive-through order test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive update drive-through order test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive update drive-through order test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive update drive-through order test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getUpdateCounterOrderData")
    private Object[][] getUpdateCounterOrderData() throws customException {
        try {
            LogUtils.info("Reading positive update counter order test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading positive update counter order test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "No update counter order test scenario data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            // Filter for positive test cases only
            List<Object[]> positiveTestCases = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "updateordercounter".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    positiveTestCases.add(row);
                }
            }

            if (positiveTestCases.isEmpty()) {
                String errorMsg = "No positive update counter order test cases found in test data";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] positiveTestData = positiveTestCases.toArray(new Object[0][]);
            LogUtils.info("Successfully retrieved " + positiveTestData.length + " positive update counter order test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + positiveTestData.length + " positive update counter order test scenarios");
            return positiveTestData;
        } catch (Exception e) {
            String errorMsg = "Error while reading positive update counter order test scenario data: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getUpdateDineInOrderData", priority = 1)
    public void updateDineInOrder(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Update Dine-In Order Test");
            ExtentReport.createTest("Update Dine-In Order Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Update Dine-In Order Test: " + description);

            // Initialize order request
            if(apiName.equalsIgnoreCase("updateorderdineIn")) {
                requestBodyJson = new JSONObject(requestBody);
                orderRequest = new OrderRequest();
                orderRequest.setUser_id(String.valueOf(user_id));
                orderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                orderRequest.setOrder_id(requestBodyJson.getString("order_id"));
                orderRequest.setSection_id(requestBodyJson.getInt("section_id"));
                orderRequest.setOrder_type(requestBodyJson.getString("order_type"));
                orderRequest.setPayment_method(requestBodyJson.getString("payment_method"));
                
                JSONArray tablesArray = requestBodyJson.getJSONArray("tables");
                List<Integer> tablesList = new ArrayList<>();
                for (int i = 0; i < tablesArray.length(); i++) {
                    tablesList.add(tablesArray.getInt(i));
                }
                orderRequest.setTables(tablesList);

                // Set order_items
                JSONArray orderItemsArray = requestBodyJson.getJSONArray("order_items");
                List<OrderRequest.OrderItem> orderItemList = new ArrayList<>();

                for (int i = 0; i < orderItemsArray.length(); i++) {
                    JSONObject item = orderItemsArray.getJSONObject(i);

                    OrderRequest.OrderItem orderItem = new OrderRequest.OrderItem();
                    orderItem.setMenu_id(item.getInt("menu_id"));
                    orderItem.setQuantity(item.getInt("quantity"));
                    orderItem.setPortion_name(item.getString("portion_name"));
                    orderItem.setComment(item.getString("comment"));

                    orderItemList.add(orderItem);
                }
                
                orderRequest.setOrder_items(new ArrayList<>(orderItemList));
                orderRequest.setAction(requestBodyJson.getString("action"));
                orderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                orderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                orderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                orderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                orderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                orderRequest.setSpecial_discount(requestBodyJson.getString("special_discount"));
                orderRequest.setCharges(requestBodyJson.getString("charges"));
                orderRequest.setTip(requestBodyJson.getString("tip"));
                
                LogUtils.info("Constructed Update Dine-In order request");
                LogUtils.info("Request Body: " + requestBodyJson.toString(4));
                ExtentReport.getTest().log(Status.INFO, "Constructed Update Dine-In order request");
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Request Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + requestBodyJson.toString(4) + "</pre>");
                
                response = ResponseUtil.getResponseWithAuth(baseURI, orderRequest, httpsmethod, accessToken);
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Response Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + response.asPrettyString() + "</pre>");
                
                // Validate status code
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Update Dine-In order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update Dine-In order API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Format the response for better readability
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Response Details:", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "<pre>" + response.asPrettyString() + "</pre>");
                    
                    // Extract and display important information from the response
                    try {
                        JSONObject responseJson = new JSONObject(response.asString());
                        if (responseJson.has("data") && responseJson.has("message")) {
                            String message = responseJson.getString("message");
                            ExtentReport.getTest().log(Status.PASS, "Message: " + message);
                            
                            if (responseJson.getJSONObject("data").has("order_id")) {
                                String orderId = responseJson.getJSONObject("data").getString("order_id");
                                ExtentReport.getTest().log(Status.PASS, "Order ID: " + orderId);
                            }
                        }
                    } catch (Exception e) {
                        ExtentReport.getTest().log(Status.INFO, "Could not parse response JSON: " + e.getMessage());
                    }
                    
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                    ExtentReport.flushReport();
                } else {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error Response:", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
                    
                    // Try to extract error message if available
                    try {
                        JSONObject errorJson = new JSONObject(response.asString());
                        if (errorJson.has("message")) {
                            String message = errorJson.getString("message");
                            ExtentReport.getTest().log(Status.FAIL, "Error Message: " + message);
                        }
                    } catch (Exception e) {
                        ExtentReport.getTest().log(Status.INFO, "Could not parse error response JSON: " + e.getMessage());
                    }
                    
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in update Dine-In order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in update Dine-In order test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed Response Body:", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
            }
            ExtentReport.flushReport();
            throw new customException("Exception in update Dine-In order test: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUpdateDeliveryOrderData", priority = 2)
    public void updateDeliveryOrder(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Update Delivery Order Test");
            ExtentReport.createTest("Update Delivery Order Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Update Delivery Order Test: " + description);
            
            // Initialize order request
            if(apiName.equalsIgnoreCase("updatedeliveryorder")) {
                requestBodyJson = new JSONObject(requestBody);
                orderRequest = new OrderRequest();
                orderRequest.setUser_id(String.valueOf(user_id));
                orderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                orderRequest.setOrder_id(requestBodyJson.getString("order_id"));
                orderRequest.setOrder_type(requestBodyJson.getString("order_type"));
                orderRequest.setPayment_method(requestBodyJson.getString("payment_method"));
                
                // Set order_items
                JSONArray orderItemsArray = requestBodyJson.getJSONArray("order_items");
                List<OrderRequest.OrderItem> orderItemList = new ArrayList<>();

                for (int i = 0; i < orderItemsArray.length(); i++) {
                    JSONObject item = orderItemsArray.getJSONObject(i);

                    OrderRequest.OrderItem orderItem = new OrderRequest.OrderItem();
                    orderItem.setMenu_id(item.getInt("menu_id"));
                    orderItem.setQuantity(item.getInt("quantity"));
                    orderItem.setPortion_name(item.getString("portion_name"));
                    orderItem.setComment(item.getString("comment"));

                    orderItemList.add(orderItem);
                }
                
                orderRequest.setOrder_items(new ArrayList<>(orderItemList));
                orderRequest.setAction(requestBodyJson.getString("action"));
                orderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                orderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                orderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                orderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                orderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                orderRequest.setSpecial_discount(requestBodyJson.getString("special_discount"));
                orderRequest.setCharges(requestBodyJson.getString("charges"));
                orderRequest.setTip(requestBodyJson.getString("tip"));
                
                LogUtils.info("Constructed Update Delivery order request");
                LogUtils.info("Request Body: " + requestBodyJson.toString(4));
                ExtentReport.getTest().log(Status.INFO, "Constructed Update Delivery order request");
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Request Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + requestBodyJson.toString(4) + "</pre>");
                
                response = ResponseUtil.getResponseWithAuth(baseURI, orderRequest, httpsmethod, accessToken);
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Response Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + response.asPrettyString() + "</pre>");
                
                // Validate status code
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Update Delivery order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update Delivery order API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Format the response for better readability
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Response Details:", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "<pre>" + response.asPrettyString() + "</pre>");
                    
                    // Extract and display important information from the response
                    try {
                        JSONObject responseJson = new JSONObject(response.asString());
                        if (responseJson.has("data") && responseJson.has("message")) {
                            String message = responseJson.getString("message");
                            ExtentReport.getTest().log(Status.PASS, "Message: " + message);
                            
                            if (responseJson.getJSONObject("data").has("order_id")) {
                                String orderId = responseJson.getJSONObject("data").getString("order_id");
                                ExtentReport.getTest().log(Status.PASS, "Order ID: " + orderId);
                            }
                        }
                    } catch (Exception e) {
                        ExtentReport.getTest().log(Status.INFO, "Could not parse response JSON: " + e.getMessage());
                    }
                    
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                    ExtentReport.flushReport();
                } else {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error Response:", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
                    
                    // Try to extract error message if available
                    try {
                        JSONObject errorJson = new JSONObject(response.asString());
                        if (errorJson.has("message")) {
                            String message = errorJson.getString("message");
                            ExtentReport.getTest().log(Status.FAIL, "Error Message: " + message);
                        }
                    } catch (Exception e) {
                        ExtentReport.getTest().log(Status.INFO, "Could not parse error response JSON: " + e.getMessage());
                    }
                    
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in update Delivery order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in update Delivery order test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed Response Body:", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
            }
            ExtentReport.flushReport();
            throw new customException("Exception in update Delivery order test: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUpdateParcelOrderData", priority = 3)
    public void updateParcelOrder(String apiName, String testCaseid, String testType, String description,
    String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Update Parcel Order Test");
            ExtentReport.createTest("Update Parcel Order Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Update Parcel Order Test: " + description);

            // Initialize order request
            if(apiName.equalsIgnoreCase("updateorderparcel")) {
                requestBodyJson = new JSONObject(requestBody);
                orderRequest = new OrderRequest();
                orderRequest.setUser_id(String.valueOf(user_id));
                orderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                orderRequest.setOrder_id(requestBodyJson.getString("order_id"));
                orderRequest.setOrder_type(requestBodyJson.getString("order_type"));
                orderRequest.setPayment_method(requestBodyJson.getString("payment_method"));
                
                // Set order_items
                JSONArray orderItemsArray = requestBodyJson.getJSONArray("order_items");
                List<OrderRequest.OrderItem> orderItemList = new ArrayList<>();

                for (int i = 0; i < orderItemsArray.length(); i++) {
                    JSONObject item = orderItemsArray.getJSONObject(i);

                    OrderRequest.OrderItem orderItem = new OrderRequest.OrderItem();
                    orderItem.setMenu_id(item.getInt("menu_id"));
                    orderItem.setQuantity(item.getInt("quantity"));
                    orderItem.setPortion_name(item.getString("portion_name"));
                    orderItem.setComment(item.getString("comment"));

                    orderItemList.add(orderItem);
                }
                
                orderRequest.setOrder_items(new ArrayList<>(orderItemList));
                orderRequest.setAction(requestBodyJson.getString("action"));
                orderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                orderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                orderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                orderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                orderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                orderRequest.setSpecial_discount(requestBodyJson.getString("special_discount"));
                orderRequest.setCharges(requestBodyJson.getString("charges"));
                orderRequest.setTip(requestBodyJson.getString("tip"));
                
                LogUtils.info("Constructed Update Parcel order request");
                LogUtils.info("Request Body: " + requestBodyJson.toString(4));
                ExtentReport.getTest().log(Status.INFO, "Constructed Update Parcel order request");
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Request Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + requestBodyJson.toString(4) + "</pre>");
                
                response = ResponseUtil.getResponseWithAuth(baseURI, orderRequest, httpsmethod, accessToken);
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Validate status code
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Update Parcel order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update Parcel order API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Only show response without validation
                    ExtentReport.getTest().log(Status.PASS, "Full Response:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    
                    // Add a screenshot or additional details that might help visibility
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                    ExtentReport.flushReport();
                } else {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asString());
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in update Parcel order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in update Parcel order test: " + e.getMessage());
            throw new customException("Exception in update Parcel order test: " + e.getMessage());
        }
    }
    
    
    @Test(dataProvider = "getUpdateDriveThroughOrderData", priority = 4)
    public void updateDriveThroughOrder(String apiName, String testCaseid, String testType, String description,
    	    String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Update Drive-Through Order Test");
            ExtentReport.createTest("Update Drive-Through Order Test");
            ExtentReport.getTest().log(Status.INFO, "Update Drive-Through Order Test");
            // Initialize order request
            if(apiName.equalsIgnoreCase("updateorderdrivethrough")) {
                requestBodyJson = new JSONObject(requestBody);
                orderRequest = new OrderRequest();
                orderRequest.setUser_id(String.valueOf(user_id));
                orderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                orderRequest.setOrder_id(requestBodyJson.getString("order_id"));
                orderRequest.setOrder_type(requestBodyJson.getString("order_type"));
                orderRequest.setPayment_method(requestBodyJson.getString("payment_method"));
                
                // Set order_items
                JSONArray orderItemsArray = requestBodyJson.getJSONArray("order_items");
                List<OrderRequest.OrderItem> orderItemList = new ArrayList<>();

                for (int i = 0; i < orderItemsArray.length(); i++) {
                    JSONObject item = orderItemsArray.getJSONObject(i);

                    OrderRequest.OrderItem orderItem = new OrderRequest.OrderItem();
                    orderItem.setMenu_id(item.getInt("menu_id"));
                    orderItem.setQuantity(item.getInt("quantity"));
                    orderItem.setPortion_name(item.getString("portion_name"));
                    orderItem.setComment(item.getString("comment"));

                    orderItemList.add(orderItem);
                }
                
                orderRequest.setOrder_items(new ArrayList<>(orderItemList));
                orderRequest.setAction(requestBodyJson.getString("action"));
                orderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                orderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                orderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                orderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                orderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                orderRequest.setSpecial_discount(requestBodyJson.getString("special_discount"));
                orderRequest.setCharges(requestBodyJson.getString("charges"));
                orderRequest.setTip(requestBodyJson.getString("tip"));
                
                LogUtils.info("Constructed Update Drive-Through order request");
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed Update Drive-Through order request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, orderRequest, httpsmethod, accessToken);
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Validate status code
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Update Drive-Through order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update Drive-Through order API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Only show response without validation
                    ExtentReport.getTest().log(Status.PASS, "Full Response:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    
                    // Add a screenshot or additional details that might help visibility
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                    ExtentReport.flushReport();
                } else {
                	// Continuing the updateDriveThroughOrder method where it was cut off
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asString());
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in update Drive-Through order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in update Drive-Through order test: " + e.getMessage());
            throw new customException("Exception in update Drive-Through order test: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUpdateCounterOrderData", priority = 5)
    public void updateCounterOrder(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Update Counter Order Test");
            ExtentReport.createTest("Update Counter Order Test");
            ExtentReport.getTest().log(Status.INFO, "Update Counter Order Test");
            // Initialize order request
            if(apiName.equalsIgnoreCase("updateordercounter")) {
                requestBodyJson = new JSONObject(requestBody);
                orderRequest = new OrderRequest();
                orderRequest.setUser_id(String.valueOf(user_id));
                orderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                orderRequest.setOrder_id(requestBodyJson.getString("order_id"));
                orderRequest.setOrder_type(requestBodyJson.getString("order_type"));
                orderRequest.setPayment_method(requestBodyJson.getString("payment_method"));
                
                // Set order_items
                JSONArray orderItemsArray = requestBodyJson.getJSONArray("order_items");
                List<OrderRequest.OrderItem> orderItemList = new ArrayList<>();

                for (int i = 0; i < orderItemsArray.length(); i++) {
                    JSONObject item = orderItemsArray.getJSONObject(i);

                    OrderRequest.OrderItem orderItem = new OrderRequest.OrderItem();
                    orderItem.setMenu_id(item.getInt("menu_id"));
                    orderItem.setQuantity(item.getInt("quantity"));
                    orderItem.setPortion_name(item.getString("portion_name"));
                    orderItem.setComment(item.getString("comment"));

                    orderItemList.add(orderItem);
                }
                
                orderRequest.setOrder_items(new ArrayList<>(orderItemList));
                orderRequest.setAction(requestBodyJson.getString("action"));
                orderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                orderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                orderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                orderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                orderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                orderRequest.setSpecial_discount(requestBodyJson.getString("special_discount"));
                orderRequest.setCharges(requestBodyJson.getString("charges"));
                orderRequest.setTip(requestBodyJson.getString("tip"));
                
                LogUtils.info("Constructed Update Counter order request");
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed Update Counter order request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, orderRequest, httpsmethod, accessToken);
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Validate status code
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Update Counter order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update Counter order API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Only show response without validation
                    ExtentReport.getTest().log(Status.PASS, "Full Response:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    
                    // Add a screenshot or additional details that might help visibility
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                    ExtentReport.flushReport();
                } else {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asString());
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in update Counter order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in update Counter order test: " + e.getMessage());
            throw new customException("Exception in update Counter order test: " + e.getMessage());
        }
    }
    
    /**
     * Validate if a message contains more than the maximum allowed number of sentences
     * @param message The message to validate
     * @param maxSentences Maximum allowed sentences
     * @return Error message if validation fails, null if validation passes
     */
    private String validateSentenceCount(String message, int maxSentences) {
        if (message == null || message.trim().isEmpty()) {
            return null; // Empty message, no sentences
        }
        
        String[] sentences = message.split("[.!?]+");
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                sentenceCount++;
            }
        }
        
        if (sentenceCount > maxSentences) {
            return "Response message exceeds maximum allowed sentences - Found: " + sentenceCount + 
                   ", Maximum allowed: " + maxSentences;
        }
        
        return null; // Validation passed
    }
    
    @Test(dataProvider = "getUpdateOrderData")
    private void updateOrder(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try {
            LogUtils.info("Update order test execution: " + description);
            ExtentReport.createTest("Update Order Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Update order test execution: " + description);

            if(apiName.equalsIgnoreCase("updateorder")) {
                requestBodyJson = new JSONObject(requestBody);

                orderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                orderRequest.setOrder_id(requestBodyJson.getString("order_id"));
                orderRequest.setOrder_status(requestBodyJson.getString("order_status"));
                orderRequest.setPayment_status(requestBodyJson.getString("payment_status"));
                orderRequest.setPayment_mode(requestBodyJson.getString("payment_mode"));
                
                LogUtils.info("Constructed update order request"); 
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Constructed update order request");
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, orderRequest, httpsmethod, accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Only show response without validation
                actualResponseBody = new JSONObject(response.asString());
                LogUtils.info("Update order response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Update order response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                
                LogUtils.success(logger, "Update order test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Update order test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in update order test: " + e.getMessage();
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
    

