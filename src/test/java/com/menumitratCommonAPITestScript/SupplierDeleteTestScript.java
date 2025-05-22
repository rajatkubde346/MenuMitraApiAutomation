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
import com.menumitra.apiRequest.SupplierRequest;
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
public class SupplierDeleteTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private SupplierRequest supplierDeleteRequest;
    private URL url;
    private int user_id;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(SupplierDeleteTestScript.class);

    @DataProvider(name="getSupplierDeleteUrl")
    private Object[][] getSupplierDeleteUrl() throws customException {
        try {
            LogUtils.info("Reading Supplier Delete API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Supplier Delete API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if(readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Supplier Delete API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "supplierdelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No supplier delete URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved Supplier Delete API endpoint data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved Supplier Delete API endpoint data");
            return filteredData;
        }
        catch(Exception e) {
            String errorMsg = "Error while reading Supplier Delete API endpoint data from Excel sheet: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getSupplierDeleteData")
    public static Object[][] getSupplierDeleteData() throws customException {
        try {
            LogUtils.info("Reading supplier delete test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No supplier delete test scenario data found in Excel sheet");
                throw new customException("No supplier delete test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "supplierdelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid supplier delete test data found after filtering";
                LogUtils.error(errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " supplier delete test scenarios");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading supplier delete test scenario data from Excel sheet: " + e.getMessage());
            throw new customException("Error while reading supplier delete test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("==== Starting setup for supplier delete test ====");
            ExtentReport.createTest("Supplier Delete Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing supplier delete test setup");
            
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
            
            Object[][] supplierDeleteData = getSupplierDeleteUrl();
            if (supplierDeleteData.length > 0) {
                String endpoint = supplierDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for supplier delete: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No supplier delete URL found in test data";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                String errorMsg = "Required tokens not found. Please ensure login and OTP verification is completed";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            supplierDeleteRequest = new SupplierRequest();
            
            LogUtils.info("Supplier delete setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier delete setup completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            String errorMsg = "Error in supplier delete setup: " + e.getMessage();
            LogUtils.error(errorMsg);
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getSupplierDeleteData")
    public void testSupplierDelete(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Executing supplier delete test for scenario: " + testCaseid);
            ExtentReport.createTest("Supplier Delete Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            // Prepare request
            requestBodyJson = new JSONObject(requestBody);
            supplierDeleteRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            supplierDeleteRequest.setSupplier_id(requestBodyJson.getString("supplier_id"));
            supplierDeleteRequest.setUser_id(String.valueOf(user_id));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            
            // Execute API call
            response = ResponseUtil.getResponseWithAuth(baseURI, supplierDeleteRequest, httpsmethod, accessToken);
            
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

            // Only show response without validation
            actualJsonBody = new JSONObject(response.asString());
            LogUtils.info("Supplier delete response received successfully");
            ExtentReport.getTest().log(Status.PASS, "Supplier delete response received successfully");
            ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
            
            LogUtils.success(logger, "Supplier delete test completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Supplier delete test completed successfully", ExtentColor.GREEN));
            
        } catch (Exception e) {
            String errorMsg = "Error in supplier delete test: " + e.getMessage();
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
