package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
import com.menumitra.apiRequest.staffRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.*;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class StaffUpdateTestScript extends APIBase {

    private staffRequest staffUpdateRequest;
    private Response response;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    Logger logger = LogUtils.getLogger(StaffUpdateTestScript.class);
    RequestSpecification request;

    @DataProvider(name = "getStaffUpdateUrl")
    public static Object[][] getStaffUpdateUrl() throws customException {
        try {
            LogUtils.info("Reading Staff Update API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "staffUpdate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Staff Update API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Staff Update API endpoint data from Excel sheet");
            throw new customException("Error While Reading Staff Update API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getStaffUpdateData")
    public static Object[][] getStaffUpdateData() throws customException {
        try {
            LogUtils.info("Reading staff update test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 2 &&
                    "staffupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.error("Error while reading staff update test scenario data: " + e.getMessage());
            throw new customException("Error while reading staff update test scenario data: " + e.getMessage());
        }
    }

    @DataProvider(name = "getStaffUpdateNegativeData")
    public Object[][] getStaffUpdateNegativeData() throws customException {
        try {
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            List<Object[]> filterData = new ArrayList<>();
            
            for (Object[] row : readExcelData) {
                if (row != null && "staffupdate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filterData.add(row);
                }
            }
            
            return filterData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.error("Error while reading staff update negative test data: " + e.getMessage());
            throw new customException("Error while reading staff update negative test data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====start setup update staff====");
            ExtentReport.createTest("Update Staff Setup");
            
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            
            Object[][] staffUpdateData = getStaffUpdateUrl();
            if (staffUpdateData.length > 0) {
                String endpoint = staffUpdateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Constructed base URI: " + baseUri);
            } else {
                throw new customException("No staff update URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                throw new customException("Required tokens not found");
            }
            
            staffUpdateRequest = new staffRequest();
            LogUtils.success(logger, "Staff Update Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error during staff update setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getStaffUpdateData")
    private void updateStaff(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting staff update test case: " + testCaseid);
            ExtentReport.createTest("Staff Update Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("staffupdate") && testType.equalsIgnoreCase("positive")) {
                requestBody = requestBody.replace("\\", "\\\\");
                JSONObject requestjsonBody = new JSONObject(requestBody);
                
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                System.out.println(accessToken);
                request.contentType("multipart/form-data");

                if (requestjsonBody.has("image") && !requestjsonBody.getString("image").isEmpty()) {
                    File imageFile = new File(requestjsonBody.getString("image"));
                    if (imageFile.exists()) {
                        request.multiPart("image", imageFile);
                    }
                }

                request.multiPart("outlet_id", requestjsonBody.getInt("outlet_id"));
                request.multiPart("user_id", requestjsonBody.getInt("user_id"));
                request.multiPart("staff_id", requestjsonBody.getInt("staff_id"));
                request.multiPart("mobile", requestjsonBody.getString("mobile"));
                request.multiPart("name", requestjsonBody.getString("name"));
                request.multiPart("dob", requestjsonBody.getString("dob"));
                request.multiPart("aadhar_number", requestjsonBody.getString("aadhar_number"));
                request.multiPart("address", requestjsonBody.getString("address"));
                request.multiPart("role", requestjsonBody.getString("role"));
                
                response = request.when().patch(baseUri).then().extract().response();
                System.out.println(response.getStatusCode());
                if (response.getStatusCode() == 200) {
                    LogUtils.success(logger, "Staff updated successfully");
                    ExtentReport.getTest().log(Status.PASS, "Staff updated successfully");
                    //validateResponseBody.handleResponseBody(response, new JSONObject(expectedResponseBody));
                } else {
                    LogUtils.failure(logger, "Staff update failed with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Staff update failed");
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during staff update test execution: " + e.getMessage());
            throw new customException("Error during staff update test execution: " + e.getMessage());
        }
    }

   // @Test(dataProvider = "getStaffUpdateNegativeData")
    private void verifyStaffUpdateNegativeCases(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) 
            throws customException {
        try {
            LogUtils.info("Starting staff update negative test case: " + testCaseid);
            ExtentReport.createTest("Staff Update Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("staffupdate") && testType.equalsIgnoreCase("negative")) {
                JSONObject requestjsonBody = new JSONObject(requestBody.replace("\\", "\\\\"));
                
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.contentType("multipart/form-data");

                // Add form data fields
                for (String key : requestjsonBody.keySet()) {
                    if (key.equals("image") && requestjsonBody.has("image") && !requestjsonBody.getString("image").isEmpty()) {
                        File imageFile = new File(requestjsonBody.getString("image"));
                        if (imageFile.exists()) {
                            request.multiPart("image", imageFile);
                        }
                    } else {
                        request.multiPart(key, requestjsonBody.get(key).toString());
                    }
                }
                
                response =  response = request.when().patch(baseUri).then().extract().response();
                
                // Validate status code
                int expectedStatusCode = Integer.parseInt(statusCode);
                if (response.getStatusCode() != expectedStatusCode) {
                    LogUtils.failure(logger, "Status code validation failed - Expected: " + expectedStatusCode + 
                        ", Actual: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, "Status code validation failed");
                } else {
                    LogUtils.success(logger, "Status code validation passed");
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed");
                    
                    // Validate response body if expected response is provided
                    if (expectedResponseBody != null && !expectedResponseBody.isEmpty()) {
                        JSONObject expectedResponseJson = new JSONObject(expectedResponseBody);
                        validateResponseBody.handleResponseBody(response, expectedResponseJson);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during staff update negative test execution: " + e.getMessage());
            throw new customException("Error during staff update negative test execution: " + e.getMessage());
        }
    }

   // @AfterClass
    private void tearDown() {
        try {
            LogUtils.info("===Test environment tear down successfully===");
            ExtentReport.getTest().log(Status.PASS, "Test environment tear down successfully");
            ActionsMethods.logout();
            TokenManagers.clearTokens();
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }
}