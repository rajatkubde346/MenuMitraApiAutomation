package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.LoadOrderRequest;
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
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class LoadOrderTestScripts extends APIBase {
    private LoadOrderRequest loadOrderRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private String accessToken;
    private int user_id;
    private JSONObject expectedJsonBody;
    private String section_id;
    Logger logger = LogUtils.getLogger(LoadOrderTestScripts.class);

    @BeforeClass
    private void loadOrderSetUp() throws customException {
        try {
            LogUtils.info("Load Order SetUp");
            ExtentReport.createTest("Load Order SetUp");
            ExtentReport.getTest().log(Status.INFO, "Load Order SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getLoadOrderUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No load order URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No load order URL found in test data");
                throw new customException("No load order URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if(accessToken.isEmpty()) {
                LogUtils.failure(logger, "JWT token is empty");
                ExtentReport.getTest().log(Status.FAIL, "JWT token is empty");
                throw new customException("JWT token is empty");
            }

        loadOrderRequest = new LoadOrderRequest();
            LogUtils.success(logger, "Load order setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Load order setup completed successfully");
            ExtentReport.flushReport();
        } catch (Exception e) {
            LogUtils.failure(logger, "Error during setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during setup: " + e.getMessage());
            ExtentReport.flushReport();
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getLoadOrderUrl")
    private Object[][] getLoadOrderUrl() throws customException {
        try {
            LogUtils.info("Reading Load Order API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Load Order API endpoint data found in Excel sheet at path: " + excelSheetPathForGetApis;
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "loadorder".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            LogUtils.success(logger, "Successfully retrieved Load Order API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully loaded Load Order API configuration");
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting load order URL data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting load order URL data: " + e.getMessage());
            throw new customException("Error in getting load order URL data: " + e.getMessage());
        }
    }

    @DataProvider(name = "getDineInLoadOrderData")
    private Object[][] getDineInLoadOrderData() throws customException {
        try {
            LogUtils.info("Reading Dine-In Load Order test data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading Dine-In Load Order test data from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return Arrays.stream(readExcelData)
                    .filter(row -> "loadorderdineIn".equalsIgnoreCase(row[0].toString()) && 
                                 "positive".equalsIgnoreCase(row[2].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Dine-In load order test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Dine-In load order test data: " + e.getMessage());
            throw new customException("Error in getting Dine-In load order test data: " + e.getMessage());
        }
    }

    private boolean isSuccessStatusCode(int statusCode) {
        // Consider both 200 and 201 as success status codes
        return statusCode == 200 || statusCode == 201;
    }

    @Test(dataProvider = "getDineInLoadOrderData", priority = 1)
    public void loadDineInOrder(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Load Dine-In Order Test");
            ExtentReport.createTest("Load Dine-In Order Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Load Dine-In Order Test: " + description);

            if(apiName.equalsIgnoreCase("loadorderdineIn")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Set the request parameters
                loadOrderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                loadOrderRequest.setUser_id(String.valueOf(user_id));
                loadOrderRequest.setTable_id(requestBodyJson.getInt("table_id"));
                loadOrderRequest.setSection_id(String.valueOf(requestBodyJson.getInt("section_id")));
                loadOrderRequest.setOrder_type("dinein"); // Updated order type without underscore
                loadOrderRequest.setAction(requestBodyJson.getString("action"));
                
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
                
                loadOrderRequest.setOrder_items(new ArrayList<>(orderItemList));
                
                // Set customer details
                loadOrderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                loadOrderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                loadOrderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                loadOrderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                loadOrderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                
                // Set payment details
                loadOrderRequest.setSpecial_discount(requestBodyJson.getDouble("special_discount"));
                loadOrderRequest.setCharges(requestBodyJson.getDouble("charges"));
                loadOrderRequest.setTip(requestBodyJson.getDouble("tip"));
                loadOrderRequest.setOrder_payment_settle_type(requestBodyJson.getString("order_payment_settle_type"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString(4));
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Request Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + requestBodyJson.toString(4) + "</pre>");
                
                // Make the API call
                response = ResponseUtil.getResponseWithAuth(baseURI, loadOrderRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Response Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + response.asPrettyString() + "</pre>");
                
                // Updated status code validation
                if (isSuccessStatusCode(response.getStatusCode())) {
                    LogUtils.success(logger, "Load Dine-In order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Load Dine-In order API executed successfully", ExtentColor.GREEN));
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
                    
                    ExtentReport.flushReport();
                } else {
                    String errorMsg = "Status code indicates failure - Received: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asString());
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in load Dine-In order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in load Dine-In order test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed Response Body:", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
            }
            ExtentReport.flushReport();
            throw new customException("Exception in load Dine-In order test: " + e.getMessage());
        }
    }

    @DataProvider(name = "getDeliveryLoadOrderData")
    private Object[][] getDeliveryLoadOrderData() throws customException {
        try {
            LogUtils.info("Reading Delivery Load Order test data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading Delivery Load Order test data from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return Arrays.stream(readExcelData)
                    .filter(row -> "loadorderdelivery".equalsIgnoreCase(row[0].toString()) && 
                                 "positive".equalsIgnoreCase(row[2].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Delivery load order test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Delivery load order test data: " + e.getMessage());
            throw new customException("Error in getting Delivery load order test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getDeliveryLoadOrderData", priority = 2)
    public void loadDeliveryOrder(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Load Delivery Order Test");
            ExtentReport.createTest("Load Delivery Order Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Load Delivery Order Test: " + description);

            if(apiName.equalsIgnoreCase("loadorderdelivery")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Set the request parameters with correct order type
                loadOrderRequest.setOrder_type("delivery");
                
                // Set the request parameters
                loadOrderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                loadOrderRequest.setUser_id(String.valueOf(user_id));
                loadOrderRequest.setSection_id(String.valueOf(requestBodyJson.getInt("section_id")));
                
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
                
                loadOrderRequest.setOrder_items(new ArrayList<>(orderItemList));
                
                // Set customer details
                loadOrderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                loadOrderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                loadOrderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                loadOrderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                loadOrderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                
                // Set payment details
                loadOrderRequest.setSpecial_discount(requestBodyJson.getDouble("special_discount"));
                loadOrderRequest.setCharges(requestBodyJson.getDouble("charges"));
                loadOrderRequest.setTip(requestBodyJson.getDouble("tip"));
                loadOrderRequest.setOrder_payment_settle_type(requestBodyJson.getString("order_payment_settle_type"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString(4));
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Request Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + requestBodyJson.toString(4) + "</pre>");
                
                // Make the API call
                response = ResponseUtil.getResponseWithAuth(baseURI, loadOrderRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Response Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + response.asPrettyString() + "</pre>");
                
                // Updated status code validation
                if (isSuccessStatusCode(response.getStatusCode())) {
                    LogUtils.success(logger, "Load Delivery order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Load Delivery order API executed successfully", ExtentColor.GREEN));
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
                    
                    ExtentReport.flushReport();
                } else {
                    String errorMsg = "Status code indicates failure - Received: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asString());
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in load Delivery order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in load Delivery order test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed Response Body:", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
            }
            ExtentReport.flushReport();
            throw new customException("Exception in load Delivery order test: " + e.getMessage());
        }
    }

    @DataProvider(name = "getTakeawayLoadOrderData")
    private Object[][] getTakeawayLoadOrderData() throws customException {
        try {
            LogUtils.info("Reading Takeaway Load Order test data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading Takeaway Load Order test data from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return Arrays.stream(readExcelData)
                    .filter(row -> "loadordertakeaway".equalsIgnoreCase(row[0].toString()) && 
                                 "positive".equalsIgnoreCase(row[2].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Takeaway load order test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Takeaway load order test data: " + e.getMessage());
            throw new customException("Error in getting Takeaway load order test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getTakeawayLoadOrderData", priority = 3)
    public void loadTakeawayOrder(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Load Takeaway Order Test");
            ExtentReport.createTest("Load Takeaway Order Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Load Takeaway Order Test: " + description);

            if(apiName.equalsIgnoreCase("loadordertakeaway")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Set the request parameters with correct order type
                loadOrderRequest.setOrder_type("parcel"); // Keep parcel as the order type
                
                // Set the request parameters
                loadOrderRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                loadOrderRequest.setUser_id(String.valueOf(user_id));
                loadOrderRequest.setSection_id(String.valueOf(requestBodyJson.getInt("section_id")));
                
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
                
                loadOrderRequest.setOrder_items(new ArrayList<>(orderItemList));
                
                // Set customer details
                loadOrderRequest.setCustomer_name(requestBodyJson.getString("customer_name"));
                loadOrderRequest.setCustomer_mobile(requestBodyJson.getString("customer_mobile"));
                loadOrderRequest.setCustomer_address(requestBodyJson.getString("customer_address"));
                loadOrderRequest.setCustomer_alternate_mobile(requestBodyJson.getString("customer_alternate_mobile"));
                loadOrderRequest.setCustomer_landmark(requestBodyJson.getString("customer_landmark"));
                
                // Set payment details
                loadOrderRequest.setSpecial_discount(requestBodyJson.getDouble("special_discount"));
                loadOrderRequest.setCharges(requestBodyJson.getDouble("charges"));
                loadOrderRequest.setTip(requestBodyJson.getDouble("tip"));
                loadOrderRequest.setOrder_payment_settle_type(requestBodyJson.getString("order_payment_settle_type"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString(4));
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Request Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + requestBodyJson.toString(4) + "</pre>");
                
                // Make the API call
                response = ResponseUtil.getResponseWithAuth(baseURI, loadOrderRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Response Body:", ExtentColor.BLUE));
                ExtentReport.getTest().log(Status.INFO, "<pre>" + response.asPrettyString() + "</pre>");
                
                // Updated status code validation
                if (isSuccessStatusCode(response.getStatusCode())) {
                    LogUtils.success(logger, "Load Takeaway order API executed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Load Takeaway order API executed successfully", ExtentColor.GREEN));
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
                    
                    ExtentReport.flushReport();
                } else {
                    String errorMsg = "Status code indicates failure - Received: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asString());
                    ExtentReport.flushReport();
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in load Takeaway order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in load Takeaway order test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed Response Body:", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
            }
            ExtentReport.flushReport();
            throw new customException("Exception in load Takeaway order test: " + e.getMessage());
        }
    }

    @DataProvider(name = "getParcelLoadOrderData")
    private Object[][] getParcelLoadOrderData() throws customException {
        try {
            LogUtils.info("Reading Parcel Load Order test data from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading Parcel Load Order test data from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return Arrays.stream(readExcelData)
                    .filter(row -> "loadorderparcel".equalsIgnoreCase(row[0].toString()) && 
                                 "positive".equalsIgnoreCase(row[2].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting Parcel load order test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting Parcel load order test data: " + e.getMessage());
            throw new customException("Error in getting Parcel load order test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getParcelLoadOrderData", priority = 4)
    public void loadParcelOrder() throws customException {
        try {
            LogUtils.info("Load Parcel Order Test");
            ExtentReport.createTest("Load Parcel Order Test");
            ExtentReport.getTest().log(Status.INFO, "Load Parcel Order Test");

            // Set the request parameters with correct order type
            loadOrderRequest.setOrder_type("parcel"); // Keep parcel as the order type
            
            // Set the request parameters
            loadOrderRequest.setOutlet_id("1");
            loadOrderRequest.setUser_id(String.valueOf(user_id));
            loadOrderRequest.setSection_id("366");
            
            // Set order_items
            List<OrderRequest.OrderItem> orderItemList = new ArrayList<>();
            OrderRequest.OrderItem orderItem = new OrderRequest.OrderItem();
            orderItem.setMenu_id(17);
            orderItem.setQuantity(1);
            orderItem.setPortion_name("Half");
            orderItem.setComment("Extra spicy please");
            orderItemList.add(orderItem);
            
            loadOrderRequest.setOrder_items(new ArrayList<>(orderItemList));
            
            // Set customer details
            loadOrderRequest.setCustomer_name("John Doe");
            loadOrderRequest.setCustomer_mobile("9876543210");
            loadOrderRequest.setCustomer_address("123 Main Street, City");
            loadOrderRequest.setCustomer_alternate_mobile("8765432109");
            loadOrderRequest.setCustomer_landmark("Near Central Park");
            
            // Set payment details
            loadOrderRequest.setSpecial_discount(10.0);
            loadOrderRequest.setCharges(5.0);
            loadOrderRequest.setTip(20.0);
            loadOrderRequest.setOrder_payment_settle_type("paid");
            
            // Make the API call
            response = ResponseUtil.getResponseWithAuth(baseURI, loadOrderRequest, "POST", accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Response Body:", ExtentColor.BLUE));
            ExtentReport.getTest().log(Status.INFO, "<pre>" + response.asPrettyString() + "</pre>");
            
            // Updated status code validation
            if (isSuccessStatusCode(response.getStatusCode())) {
                LogUtils.success(logger, "Load Parcel order API executed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Load Parcel order API executed successfully", ExtentColor.GREEN));
                ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                
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
                
                ExtentReport.flushReport();
            } else {
                String errorMsg = "Status code indicates failure - Received: " + response.getStatusCode();
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asString());
                ExtentReport.flushReport();
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in load Parcel order test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in load Parcel order test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Failed Response Body:", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "<pre>" + response.asPrettyString() + "</pre>");
            }
            ExtentReport.flushReport();
            throw new customException("Exception in load Parcel order test: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void loadTestMultipleOrders() throws customException {
        try {
            LogUtils.info("Starting Load Test for Multiple Orders");
            ExtentReport.createTest("Load Test - Multiple Orders");
            ExtentReport.getTest().log(Status.INFO, "Starting load test for 5 takeaway and 5 delivery orders");

            // Get test data for takeaway and delivery orders
            Object[][] takeawayData = getTakeawayLoadOrderData();
            Object[][] deliveryData = getDeliveryLoadOrderData();

            if (takeawayData.length == 0 || deliveryData.length == 0) {
                throw new customException("Test data not available for takeaway or delivery orders");
            }

            // Use first test case data as template for both order types
            String[] takeawayTestCase = Arrays.stream(takeawayData[0])
                    .map(Object::toString)
                    .toArray(String[]::new);
            String[] deliveryTestCase = Arrays.stream(deliveryData[0])
                    .map(Object::toString)
                    .toArray(String[]::new);

            // Perform load test - 5 iterations for each order type
            for (int i = 1; i <= 5; i++) {
                try {
                    // Test Takeaway Order
                    LogUtils.info("Iteration " + i + " - Testing Takeaway Order");
                    ExtentReport.getTest().log(Status.INFO, "Iteration " + i + " - Testing Takeaway Order");
                    loadTakeawayOrder(
                        takeawayTestCase[0],
                        "LoadTest_Takeaway_" + i,
                        takeawayTestCase[2],
                        "Load Test Iteration " + i + " for Takeaway Order",
                        takeawayTestCase[4],
                        takeawayTestCase[5],
                        takeawayTestCase[6],
                        takeawayTestCase[7]
                    );

                    // Test Delivery Order
                    LogUtils.info("Iteration " + i + " - Testing Delivery Order");
                    ExtentReport.getTest().log(Status.INFO, "Iteration " + i + " - Testing Delivery Order");
                    loadDeliveryOrder(
                        deliveryTestCase[0],
                        "LoadTest_Delivery_" + i,
                        deliveryTestCase[2],
                        "Load Test Iteration " + i + " for Delivery Order",
                        deliveryTestCase[4],
                        deliveryTestCase[5],
                        deliveryTestCase[6],
                        deliveryTestCase[7]
                    );

                    // Add a small delay between iterations
                    Thread.sleep(1000);
                    
                    LogUtils.info("Completed iteration " + i + " successfully");
                    ExtentReport.getTest().log(Status.PASS, "Completed iteration " + i + " successfully");
                } catch (Exception e) {
                    LogUtils.failure(logger, "Failed in iteration " + i + ": " + e.getMessage());
                    ExtentReport.getTest().log(Status.FAIL, "Failed in iteration " + i + ": " + e.getMessage());
                    // Continue with next iteration even if current one fails
                    continue;
                }
            }

            LogUtils.success(logger, "Load test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Load test completed successfully", ExtentColor.GREEN));
            ExtentReport.flushReport();

        } catch (Exception e) {
            LogUtils.failure(logger, "Exception in load test: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception in load test: " + e.getMessage());
            ExtentReport.flushReport();
            throw new customException("Exception in load test: " + e.getMessage());
        }
    }
}
