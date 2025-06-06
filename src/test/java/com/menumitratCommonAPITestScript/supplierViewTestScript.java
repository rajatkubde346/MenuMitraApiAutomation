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
	import com.menumitra.apiRequest.SupplierViewRequest;
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
	public class supplierViewTestScript extends APIBase
	{
	    private JSONObject requestBodyJson;
	    private Response response;
	    private String baseURI;
	    private String accessToken;
	    private SupplierViewRequest supplierViewRequest;
	    private URL url;
	    Logger logger = LogUtils.getLogger(supplierViewTestScript.class);
	    private JSONObject expectedResponseJson;
	    private JSONObject actualJsonBody;

	    @DataProvider(name = "getSupplierViewUrl")
	    private Object[][] getSupplierViewUrl() throws customException
	    {
	        try {
	            LogUtils.info("Reading supplier view URL from Excel sheet");
	            ExtentReport.getTest().log(Status.INFO, "Reading supplier view URL from Excel sheet");

	            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
	            if (readExcelData == null) {
	                String errorMsg = "Error fetching data from Excel sheet - Data is null";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            Object[][] filteredData = Arrays.stream(readExcelData)
	                    .filter(row -> "supplierview".equalsIgnoreCase(row[0].toString()))
	                    .toArray(Object[][]::new);

	            if (filteredData.length == 0) {
	                String errorMsg = "No supplier view URL data found after filtering";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            LogUtils.info("Successfully retrieved supplier view URL data");
	            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved supplier view URL data");
	            return filteredData;
	        } catch (Exception e) {
	            String errorMsg = "Error in getSupplierViewUrl: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            throw new customException(errorMsg);
	        }
	    }

	    @DataProvider(name = "getSupplierViewValidData")
	    public Object[][] getSupplierViewValidData() throws customException {
	        try {
	            LogUtils.info("Reading supplier view valid data from Excel sheet");
	            ExtentReport.getTest().log(Status.INFO, "Reading supplier view valid data from Excel sheet");

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
	                        "supplierview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
	                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

	                    filteredData.add(row);
	                }
	            }

	            if (filteredData.isEmpty()) {
	                String errorMsg = "No valid supplier view test data found after filtering";
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }

	            Object[][] obj = new Object[filteredData.size()][];
	            for (int i = 0; i < filteredData.size(); i++) {
	                obj[i] = filteredData.get(i);
	            }

	            LogUtils.info("Successfully retrieved " + obj.length + " supplier view test scenarios");
	            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " supplier view test scenarios");
	            return obj;
	        } catch (Exception e) {
	            String errorMsg = "Error in getSupplierViewValidData: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            throw new customException(errorMsg);
	        }
	    }

	    @BeforeClass
	    private void supplierViewSetUp() throws customException {
	        try {
	            LogUtils.info("Setting up supplier view test");
	            ExtentReport.createTest("Supplier View Test Setup");
	            ExtentReport.getTest().log(Status.INFO, "Initializing supplier view test setup");

	            ActionsMethods.login();
	            ActionsMethods.verifyOTP();
	            baseURI = EnviromentChanges.getBaseUrl();

	            Object[][] supplierViewData = getSupplierViewUrl();
	            if (supplierViewData.length > 0) {
	                String endpoint = supplierViewData[0][2].toString();
	                url = new URL(endpoint);
	                baseURI = RequestValidator.buildUri(endpoint, baseURI);
	                LogUtils.info("Constructed base URI: " + baseURI);
	                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
	            } else {
	                String errorMsg = "No supplier view URL found in test data";
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

	            supplierViewRequest = new SupplierViewRequest();
	            LogUtils.info("Supplier view test setup completed successfully");
	            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier view test setup completed successfully", ExtentColor.GREEN));
	        } catch (Exception e) {
	            String errorMsg = "Error in supplier view setUp: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            throw new customException(errorMsg);
	        }
	    }

	    @Test(dataProvider = "getSupplierViewValidData")
	    private void verifySupplierView(String apiName, String testCaseid, String testType, String description,
	            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
	        try {
	            LogUtils.info("Starting supplier view test case: " + testCaseid);
	            ExtentReport.createTest("Supplier View Test - " + testCaseid);
	            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

	            if (apiName.equalsIgnoreCase("supplierview")) {
	                requestBodyJson = new JSONObject(requestBody);
	                supplierViewRequest.setSupplier_id(String.valueOf(requestBodyJson.getString("supplier_id")));
	                supplierViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));

	                LogUtils.info("Request Body: " + requestBodyJson.toString());
	                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

	                response = ResponseUtil.getResponseWithAuth(baseURI, supplierViewRequest, httpsmethod, accessToken);

	                LogUtils.info("Response Status Code: " + response.getStatusCode());
	                LogUtils.info("Response Body: " + response.asString());
	                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
	                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

	                // Validate status code
	                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
	                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
	                    LogUtils.failure(logger, errorMsg);
	                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                    throw new customException(errorMsg);
	                }

	                LogUtils.success(logger, "Supplier view test completed successfully\nResponse: " + response.asPrettyString());
	                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier view test completed successfully", ExtentColor.GREEN));
	                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
	            }
	        } catch (Exception e) {
	            String errorMsg = "Error in supplier view test: " + e.getMessage();
	            LogUtils.exception(logger, errorMsg, e);
	            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	            if (response != null) {
	                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
	                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
	            }
	            throw new customException(errorMsg);
	        }
	    }
	    
	    @DataProvider(name = "getSupplierViewNegativeData")
	    public Object[][] getSupplierViewNegativeData() throws customException {
	        try {
	            LogUtils.info("Reading supplier view negative test scenario data");
	            ExtentReport.getTest().log(Status.INFO, "Reading supplier view negative test scenario data");
	            
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
	                        "supplierview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
	                        "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
	                    
	                    filteredData.add(row);
	                }
	            }
	            
	            if (filteredData.isEmpty()) {
	                String errorMsg = "No valid supplier view negative test data found after filtering";
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
	            LogUtils.failure(logger, "Error in getting supplier view negative test data: " + e.getMessage());
	            ExtentReport.getTest().log(Status.FAIL, "Error in getting supplier view negative test data: " + e.getMessage());
	            throw new customException("Error in getting supplier view negative test data: " + e.getMessage());
	        }
	    }
	    
	    /**
	     * Checks if a message contains exactly 6 sentences
	     * @param message The message to check
	     * @return true if message contains exactly 6 sentences, false otherwise
	     */
	    private boolean validateSentenceCount(String message) {
	        if (message == null || message.trim().isEmpty()) {
	            return false;
	        }
	        
	        // Count sentences by splitting on ., !, and ? followed by space or end of string
	        String[] sentences = message.split("[.!?](?:\\s|$)");
	        
	        // Filter out empty sentences
	        int count = 0;
	        for (String sentence : sentences) {
	            if (!sentence.trim().isEmpty()) {
	                count++;
	            }
	        }
	        
	        return count == 6;
	    }
	    
	    @Test(dataProvider = "getSupplierViewNegativeData")
	    public void supplierViewNegativeTest(String apiName, String testCaseid, String testType, String description,
	            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
	        try {
	            LogUtils.info("Starting supplier view negative test case: " + testCaseid);
	            ExtentReport.createTest("Supplier View Negative Test - " + testCaseid + ": " + description);
	            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
	            
	            if (!apiName.equalsIgnoreCase("supplierview") || !testType.equalsIgnoreCase("negative")) {
	                String errorMsg = "Incorrect API name or test type. Expected: supplierview/negative, Actual: " + apiName + "/" + testType;
	                LogUtils.failure(logger, errorMsg);
	                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
	                throw new customException(errorMsg);
	            }
	            
	            requestBodyJson = new JSONObject(requestBody);
	            
	            LogUtils.info("Request Body: " + requestBodyJson.toString());
	            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
	            
	            // Set payload for supplier view request
	            if (requestBodyJson.has("outlet_id")) {
	                supplierViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
	            }
	            
	            if (requestBodyJson.has("supplier_id")) {
	                supplierViewRequest.setSupplier_id(requestBodyJson.getString("supplier_id"));
	            }
	            
	            response = ResponseUtil.getResponseWithAuth(baseURI, supplierViewRequest, httpsmethod, accessToken);
	            
	            LogUtils.info("Response Status Code: " + response.getStatusCode());
	            LogUtils.info("Response Body: " + response.asString());
	            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
	            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
	            
	            int expectedStatusCode = Integer.parseInt(statusCode);
	            
	            // Report actual vs expected status codes
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
	                if (response.asString() == null || response.asString().trim().isEmpty()) {
	                    LogUtils.failure(logger, "Empty response body");
	                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Empty response body", ExtentColor.RED));
	                } else {
	                    actualJsonBody = new JSONObject(response.asString());
	                    
	                    // Report expected vs actual response body
	                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body: " + expectedResponseBody);
	                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body: " + response.asString());
	                    
	                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
	                        expectedResponseJson = new JSONObject(expectedResponseBody);
	                        
	                        // Validate response message
	                        if (expectedResponseJson.has("detail") && actualJsonBody.has("detail")) {
	                            String expectedDetail = expectedResponseJson.getString("detail");
	                            String actualDetail = actualJsonBody.getString("detail");
	                            
	                            // Check sentence count
	                            boolean validSentenceCount = validateSentenceCount(actualDetail);
	                            if (!validSentenceCount) {
	                                int sentenceCount = actualDetail.split("[.!?](?:\\s|$)").length;
	                                if (sentenceCount > 6) {
	                                    String sentenceErrorMsg = "Response message contains more than 6 sentences: " + sentenceCount;
	                                    LogUtils.failure(logger, sentenceErrorMsg);
	                                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(sentenceErrorMsg, ExtentColor.RED));
	                                } 
	                            } else {
	                                LogUtils.info("Sentence count validation passed: Exactly 6 sentences");
	                                ExtentReport.getTest().log(Status.PASS, "Sentence count validation passed: Exactly 6 sentences");
	                            }
	                            
	                            if (expectedDetail.equals(actualDetail)) {
	                                LogUtils.info("Error message validation passed: " + actualDetail);
	                                ExtentReport.getTest().log(Status.PASS, "Error message validation passed: " + actualDetail);
	                            } else {
	                                LogUtils.failure(logger, "Error message mismatch - Expected: " + expectedDetail + ", Actual: " + actualDetail);
	                                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Error message mismatch", ExtentColor.RED));
	                            }
	                        }
	                        
	                        // Complete response validation
	                        validateResponseBody.handleResponseBody(response, expectedResponseJson);
	                    }
	                }
	                
	                LogUtils.success(logger, "Supplier view negative test completed successfully");
	                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier view negative test completed successfully", ExtentColor.GREEN));
	            }
	            
	            // Always log the full response
	            ExtentReport.getTest().log(Status.INFO, "Full Response:");
	            ExtentReport.getTest().log(Status.INFO, response.asPrettyString());
	        } catch (Exception e) {
	            String errorMsg = "Error in supplier view negative test: " + e.getMessage();
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

