package com.menumitratCommonAPITestScript;

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
import com.menumitra.apiRequest.sectionRequest;
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

import io.restassured.response.Response;


@Listeners(Listener.class)
public class SectionViewTestScript extends APIBase
{

	
	private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedJsonBody; 
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    sectionRequest sectionrequest;
    Logger logger=LogUtils.getLogger(sectionCreateTestScript.class);
    
    
    @DataProvider(name="getSectionViewURL")
    public Object[][] getSectionViewURL() throws Exception {
        try {
            Object[][] readData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readData == null) {
                LogUtils.failure(logger, "Error: Getting an error while read Section URL Excel File");
                throw new Exception("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectionview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section URL Excel File", e);
            throw new Exception("Error: Getting an error while read Section URL Excel File");
        }
    }

    @DataProvider(name="getSectionViewPositiveInputData")
    private Object[][] getSectionViewPositiveInputData() throws Exception {
        try {
            LogUtils.info("Reading positive test scenario data for section view API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, 
            		property.getProperty("CommonAPITestScenario"));
            
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No Section View API positive test scenario data found in Excel sheet");
                throw new Exception("No Section View API Positive test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "sectionview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Section View API positive test scenario data: " + e.getMessage(), e);
            throw new Exception("Error reading Section View API positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    

    @BeforeClass
    private void sectionViewSetup() throws Exception 
    {
        try {
            LogUtils.info("Setting up test environment");
            ExtentReport.createTest("Start Section View");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set section view URL
            Object[][] sectionViewData = getSectionViewURL();
            if (sectionViewData.length > 0) {
                String endpoint = sectionViewData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section View URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section view URL found in test data");
                throw new Exception("No section view URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            sectionrequest = new sectionRequest();
            LogUtils.info("Section view setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section view setup: " + e.getMessage(), e);
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }


@Test(dataProvider = "getSectionViewPositiveInputData", priority = 1)
private void verifySectionViewUsingValidInputData(String apiName, String testCaseId,
        String testType, String description, String httpsMethod,
        String requestBody, String expectedResponseBody, String statusCode) throws Exception 
{
    try {
        LogUtils.info("Start section view API using valid input data");
        ExtentReport.createTest("Verify Section View API: " + description);
        ExtentReport.getTest().log(Status.INFO, "====Start section view using positive input data====");
        ExtentReport.getTest().log(Status.INFO, "Constructed Base URI: " + baseUri);

        if (apiName.contains("sectionview") && testType.contains("positive")) {
            requestBodyJson = new JSONObject(requestBody);
            expectedJsonBody = new JSONObject(expectedResponseBody);

            sectionrequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            sectionrequest.setSection_id(requestBodyJson.getString("section_id"));
            sectionrequest.setUser_id(String.valueOf(userId));
            LogUtils.info("Section view payload prepared");
            
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            LogUtils.info("Section view API response");
            ExtentReport.getTest().log(Status.INFO, "Section view API response: " + response.getBody().asString());

            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                String responseBody = response.getBody().asString();
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                                        LogUtils.success(logger, "Successfully validated section view API using positive input data");
                    ExtentReport.getTest().log(Status.PASS, "Successfully validated section view API using positive input data");
                } else {
                    LogUtils.failure(logger, "Empty response body received");
                    ExtentReport.getTest().log(Status.FAIL, "Empty response body received");
                    throw new Exception("Response body is empty");
                }
            } else {
                LogUtils.failure(logger, "Invalid status code for section view API using positive input data: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Invalid status code for section view API using positive input data: " + response.getStatusCode());
                throw new Exception("In section view API using positive input test case expected status code " + statusCode + " but got " + response.getStatusCode());
            }
        }
    } catch (Exception e) {
        LogUtils.exception(logger, "An error occurred during section view verification: " + e.getMessage(), e);
        ExtentReport.getTest().log(Status.FAIL, "An error occurred during section view verification: " + e.getMessage());
        throw new Exception("An error occurred during section view verification");
    }
}



//@AfterClass
private void tearDown()
{
    try 
    {
        LogUtils.info("===Test environment tear down successfully===");
       
        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
        
        ActionsMethods.logout();
        TokenManagers.clearTokens();
        
    } 
    catch (Exception e) 
    {
        LogUtils.exception(logger, "Error during test environment tear down", e);
        ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
    }
}

}

