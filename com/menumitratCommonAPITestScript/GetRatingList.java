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

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Listeners(Listener.class)
public class GetRatingList extends APIBase
{
    private Response response;
    private String baseURI;
    private String accessToken;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(GetRatingList.class);
   
    @BeforeClass
    private void getRatingListSetUp() throws customException
    {
        try
        {
            LogUtils.info("Get Rating List SetUp");
            ExtentReport.createTest("Get Rating List SetUp");
            ExtentReport.getTest().log(Status.INFO,"Get Rating List SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getRatingListUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No rating list URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No rating list URL found in test data");
                throw new customException("No rating list URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger,"Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in get rating list setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in get rating list setup: " + e.getMessage());
            throw new customException("Error in get rating list setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getRatingListUrl")
    private Object[][] getRatingListUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading rating list URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading rating list URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "getratinglist".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No rating list URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved rating list URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved rating list URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getRatingListUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
   
    @DataProvider(name = "getRatingListData") 
    public Object[][] getRatingListData() throws customException {
        try {
            LogUtils.info("Reading rating list test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading rating list test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No rating list test scenario data found in Excel sheet";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "getratinglist".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid rating list test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " rating list test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " rating list test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getRatingListData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
    
    @Test(dataProvider = "getRatingListData")
    private void verifyRatingList(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        
        try
        {
            LogUtils.info("Rating list test execution: " + description);
            ExtentReport.createTest("Rating List Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Rating list test execution: " + description);

            if(apiName.equalsIgnoreCase("getratinglist"))
            {
                // No payload needed for this API
                LogUtils.info("Requesting rating list"); 
                ExtentReport.getTest().log(Status.INFO, "Requesting rating list");
                response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .when()
                        .get(baseURI)
                        .then()
                        .log().all()
                        .extract()
                        .response();
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                if(response.getStatusCode() == Integer.parseInt(statusCode))
                {
                    LogUtils.success(logger, "Rating list API executed successfully");
                    LogUtils.info("Status Code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Rating list API executed successfully", ExtentColor.GREEN));
                    ExtentReport.getTest().log(Status.PASS, "Status Code: " + response.getStatusCode());
                    
                    // Only show response without validation
                    actualJsonBody = new JSONObject(response.asString());
                    LogUtils.info("Response received successfully");
                    LogUtils.info("Response Body: " + actualJsonBody.toString());
                    ExtentReport.getTest().log(Status.PASS, "Response received successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response Body: " + actualJsonBody.toString());
                    
                    // Make sure to use Status.PASS for the response to show in the report
                    ExtentReport.getTest().log(Status.PASS, "Full Response:");
                    ExtentReport.getTest().log(Status.PASS, response.asPrettyString());
                    
                    // Add a screenshot or additional details that might help visibility
                    ExtentReport.getTest().log(Status.INFO, MarkupHelper.createLabel("Test completed successfully", ExtentColor.GREEN));
                }
                else{
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    LogUtils.info("Response Body: " + response.asString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response: " + response.asPrettyString());
                    throw new customException(errorMsg);
                }
            }
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in rating list test", e);
            ExtentReport.getTest().log(Status.ERROR, "Error in rating list test: " + e.getMessage());
            if(response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException("Error in rating list test: " + e.getMessage());
        }
    }
} 