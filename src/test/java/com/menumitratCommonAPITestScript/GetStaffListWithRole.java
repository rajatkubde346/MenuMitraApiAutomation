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
import com.menumitra.apiRequest.WaiterRequest;
import com.menumitra.apiRequest.staffRequest;
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
public class GetStaffListWithRole extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private staffRequest staffListWithRoleRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(GetStaffListWithRole.class);
   
    @BeforeClass
    private void getStaffListWithRoleSetUp() throws customException
    {
        try
        {
            LogUtils.info("Get Staff List With Role SetUp");
            ExtentReport.createTest("Get Staff List With Role SetUp");
            ExtentReport.getTest().log(Status.INFO,"Get Staff List With Role SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getStaffListWithRoleUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No staff list with role URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No staff list with role URL found in test data");
                throw new customException("No staff list with role URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            staffListWithRoleRequest = new staffRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in staff list with role setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in staff list with role setup: " + e.getMessage());
            throw new customException("Error in staff list with role setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getStaffListWithRoleUrl")
    private Object[][] getStaffListWithRoleUrl() throws customException {
        try {
            LogUtils.info("Reading Staff List With Role API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Staff List With Role API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Staff List With Role API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "getstafflistwithrole".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No staff list with role URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting staff list with role URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting staff list with role URL: " + e.getMessage());
            throw new customException("Error in getting staff list with role URL: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getStaffListWithRoleData")
    public Object[][] getStaffListWithRoleData() throws customException {
        try {
            LogUtils.info("Reading staff list with role test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading staff list with role test scenario data");
            
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
                        "getstafflistwithrole".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid staff list with role test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting staff list with role test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting staff list with role test data: " + e.getMessage());
            throw new customException("Error in getting staff list with role test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getStaffListWithRoleData")
    public void getStaffListWithRoleTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting staff list with role test case: " + testCaseid);
            ExtentReport.createTest("Staff List With Role Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("getstafflistwithrole")) {
                requestBodyJson = new JSONObject(requestBody);
                staffListWithRoleRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                staffListWithRoleRequest.setStaff_role(requestBodyJson.getString("staff_role"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, staffListWithRoleRequest, httpsmethod, accessToken);
                
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
                
                // Validate response body
                actualJsonBody = new JSONObject(response.asString());
                if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                    expectedJsonBody = new JSONObject(expectedResponseBody);
                    
                    // Use validateResponseBody for detailed validation
                 
                   
                } else {
                    LogUtils.info("No expected response body provided for validation");
                    ExtentReport.getTest().log(Status.INFO, "No expected response body provided for validation");
                }
                
                LogUtils.info("Staff list with role response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Staff list with role response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Full Response: " + response.asPrettyString());
                
                LogUtils.success(logger, "Staff list with role test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Staff list with role test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in staff list with role test: " + e.getMessage();
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
