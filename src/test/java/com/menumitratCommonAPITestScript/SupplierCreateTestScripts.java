package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.*;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.*;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class SupplierCreateTestScripts extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private SupplierCreateRequest supplierCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(SupplierCreateTestScripts.class);

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("Starting supplier create setup");
            ExtentReport.createTest("Supplier Create Setup");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] supplierCreateData = getSupplierCreateUrl();
            if (supplierCreateData.length > 0) {
                String endpoint = supplierCreateData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
            } else {
                throw new customException("No supplier create URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                throw new customException("Required tokens not found");
            }

            supplierCreateRequest = new SupplierCreateRequest();
            LogUtils.success(logger, "Supplier Create Setup completed successfully");

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in supplier create setup: " + e.getMessage());
            throw new customException("Error in supplier create setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getSupplierCreateUrl")
    private Object[][] getSupplierCreateUrl() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> "suppliercreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            throw new customException("Error in getting supplier create URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getSupplierCreateData")
    public Object[][] getSupplierCreateData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            List<Object[]> filteredData = new ArrayList<>();

            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                        "suppliercreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            throw new customException("Error in getting supplier create test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getSupplierCreateData")
    public void supplierCreateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting supplier create test case: " + testCaseid);
            ExtentReport.createTest("Supplier Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("suppliercreate")) {
                requestBodyJson = new JSONObject(requestBody);
                LogUtils.info("Processing request for test case: " + testCaseid);
                LogUtils.info("Original request body from Excel: " + requestBody);
                LogUtils.info("Parsed JSON body: " + requestBodyJson.toString(2));

                // Initialize supplier request with values from request body
                supplierCreateRequest = new SupplierCreateRequest();
                supplierCreateRequest.setUserId(user_id);
                supplierCreateRequest.setAppSource(requestBodyJson.optString("app_source", "owner_app"));
                supplierCreateRequest.setCompanyName(requestBodyJson.getString("company_name"));
                supplierCreateRequest.setAddress(requestBodyJson.optString("address", ""));
                supplierCreateRequest.setName(requestBodyJson.getString("name"));
                supplierCreateRequest.setMobile(requestBodyJson.getString("mobile"));
                supplierCreateRequest.setAlternateMobile(requestBodyJson.optString("alternate_mobile", ""));
                supplierCreateRequest.setGstin(requestBodyJson.optString("gstin", ""));
                supplierCreateRequest.setSettlementFrequency(requestBodyJson.optString("settlement_frequency", "monthly"));
                supplierCreateRequest.setSettlementFrequencyValue(requestBodyJson.optInt("settlement_frequency_value", 1));
                supplierCreateRequest.setCreditLimit(requestBodyJson.optDouble("credit_limit", 50000.0));
                supplierCreateRequest.setIsActive(requestBodyJson.optInt("is_active", 1));

                // Initialize empty lists for bank and UPI details
                List<BankDetail> bankDetails = new ArrayList<>();
                List<UpiDetail> upiDetails = new ArrayList<>();

                // Create bank details from the request body
                if (requestBodyJson.has("bank_details") && !requestBodyJson.isNull("bank_details")) {
                    JSONArray bankArray = requestBodyJson.getJSONArray("bank_details");
                    LogUtils.info("Processing bank details array: " + bankArray.toString(2));
                    
                    // Create a new JSONArray for the modified bank details
                    JSONArray modifiedBankArray = new JSONArray();
                    
                    for (int i = 0; i < bankArray.length(); i++) {
                        JSONObject bankObj = bankArray.getJSONObject(i);
                        JSONObject modifiedBankObj = new JSONObject();
                        
                        try {
                            // Validate and copy required fields
                            String[][] requiredFields = {
                                {"bank_name", "bank_name"},
                                {"account_holder_name", "account_holder_name"},
                                {"account_number", "account_number"},
                                {"IFSC_code", "IFSC_code"},
                                {"account_type", "account_type"}
                            };

                            for (String[] field : requiredFields) {
                                String sourceField = field[0];
                                String targetField = field[1];
                                
                                if (!bankObj.has(sourceField) || bankObj.isNull(sourceField) || 
                                    bankObj.getString(sourceField).trim().isEmpty()) {
                                    LogUtils.info("Missing or empty required field: " + sourceField);
                                    LogUtils.info("Available fields in bank object: " + bankObj.keySet());
                                    throw new customException("Required field missing or empty: " + sourceField);
                                }
                                
                                modifiedBankObj.put(targetField, bankObj.getString(sourceField));
                            }

                            LogUtils.info("Validated bank detail object: " + modifiedBankObj.toString(2));
                            modifiedBankArray.put(modifiedBankObj);
                            
                            // Create BankDetail object
                            BankDetail bankDetail = new BankDetail();
                            bankDetail.setBankName(modifiedBankObj.getString("bank_name"));
                            bankDetail.setAccountHolderName(modifiedBankObj.getString("account_holder_name"));
                            bankDetail.setAccountNumber(modifiedBankObj.getString("account_number"));
                            bankDetail.setIfscCode(modifiedBankObj.getString("IFSC_code"));
                            bankDetail.setAccountType(modifiedBankObj.getString("account_type"));
                            bankDetails.add(bankDetail);
                            
                            LogUtils.info("Successfully processed bank detail #" + (i + 1));
                            
                        } catch (Exception e) {
                            LogUtils.info("Error processing bank detail #" + (i + 1) + ": " + e.getMessage());
                            LogUtils.info("Original bank detail data: " + bankObj.toString(2));
                            throw new customException("Error processing bank details: " + e.getMessage());
                        }
                    }
                    
                    // Update the request body with modified bank details
                    requestBodyJson.put("bank_details", modifiedBankArray);
                    LogUtils.info("Final bank_details array in request: " + modifiedBankArray.toString(2));
                    
                } else {
                    LogUtils.info("No bank_details found in request JSON. Available keys: " + requestBodyJson.keySet());
                    throw new customException("bank_details array is required in the request");
                }

                // Set bank details in request
                supplierCreateRequest.setBankDetails(bankDetails);

                // Log the complete request for debugging
                LogUtils.info("Complete request body before sending: " + requestBodyJson.toString(2));
                LogUtils.info("Number of bank details added: " + bankDetails.size());

                // Handle upi_details if present
                if (requestBodyJson.has("upi_details") && !requestBodyJson.isNull("upi_details")) {
                    JSONArray upiArray = requestBodyJson.getJSONArray("upi_details");
                    for (int i = 0; i < upiArray.length(); i++) {
                        JSONObject upiObj = upiArray.getJSONObject(i);
                        UpiDetail upiDetail = new UpiDetail();
                        upiDetail.setUpiId(upiObj.optString("upi_id", ""));
                        upiDetail.setUpiHolderName(upiObj.optString("upi_holder_name", requestBodyJson.getString("name")));
                        upiDetails.add(upiDetail);
                    }
                }
                supplierCreateRequest.setUpiDetails(upiDetails);

                // Log complete request for debugging
                LogUtils.info("Final request body: " + requestBodyJson.toString(2));
                ExtentReport.getTest().log(Status.INFO, "Final Request Body: " + requestBodyJson.toString(2));

                // Make API call
                LogUtils.info("Making API call to: " + baseURI);
                response = ResponseUtil.getResponseWithAuth(baseURI, supplierCreateRequest, httpsmethod, accessToken);

                // Log response details
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Handle response
                if (response.getStatusCode() == 500) {
                    String errorDetail = "";
                    try {
                        JSONObject errorJson = new JSONObject(response.asString());
                        errorDetail = errorJson.optString("detail", "Unknown server error");
                    } catch (Exception e) {
                        errorDetail = response.asString();
                    }
                    throw new customException("Server Error (500): " + errorDetail);
                }

                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    actualJsonBody = new JSONObject(response.asString());
                    LogUtils.success(logger, "Supplier create test completed successfully for test case: " + testCaseid);
                    ExtentReport.getTest().log(Status.PASS, "Test case " + testCaseid + " passed");
                    ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                } else {
                    String errorMsg = "Status code mismatch for test case " + testCaseid + 
                                   " - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, errorMsg);
                    throw new customException(errorMsg);
                }
            }
        } catch (Exception e) {
            String errorMsg = "Error in supplier create test case " + testCaseid + ": " + e.getMessage();
            LogUtils.failure(logger, errorMsg);
            ExtentReport.getTest().log(Status.FAIL, "Test case " + testCaseid + " failed");
            ExtentReport.getTest().log(Status.FAIL, "Error: " + e.getMessage());
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
}
