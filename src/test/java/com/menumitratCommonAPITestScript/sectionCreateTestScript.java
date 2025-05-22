package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
public class sectionCreateTestScript extends APIBase
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
    
    


    @DataProvider(name="getSectionCreateURL")
    public Object[][] getSectionCreateURL() throws Exception
    {
        try{
            Object[][] readData=DataDriven.readExcelData(excelSheetPathForGetApis,"commonAPI");
            if(readData==null)
            {
                LogUtils.failure(logger, "Error: Getting an error while read Section URL Excel File");
                throw new Exception("Error: Getting an error while read Section URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectioncreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section URL Excel File", e);
            throw new Exception("Error: Getting an error while read Section URL Excel File");
        }
    }

    @DataProvider(name="getSectionCreatePositiveInputData") 
    private Object[][] getSectionCreatePositiveInputData() throws Exception {
        try {
            LogUtils.info("Reading positive test scenario data for section create API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis,property.getProperty("CommonAPITestScenario"));
            
            if (testData == null || testData.length == 0)
             {
            	LogUtils.failure(logger, "No Section Create API positive test scenario data found in Excel sheet");
                throw new Exception("No Section Create API Positive test scenario data found in Excel sheet");
            }         
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                    "sectioncreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            // Optional: print to verify
            /*for (Object[] row : obj) {
                System.out.println(Arrays.toString(row));
            }*/
            return obj;
        }
        catch (Exception e) {
            LogUtils.exception(logger, "Failed to read Section Create API positive test scenario data: " + e.getMessage(), e);
            throw new Exception("Error reading Section Create API positive test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @DataProvider(name="getverifyOTPInvalidData")
    private Object[][] getverifyOTPInvalidData() throws Exception {
        try {
            LogUtils.info("Reading negative test scenario data for verify OTP API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No verify OTP API negative test scenario data found in Excel sheet");
                throw new Exception("No verify OTP API negative test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            // Filter for verify OTP API negative test cases
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "sectioncreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            
            return obj;
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read verify OTP API negative test scenario data: " + e.getMessage(), e);
            throw new Exception("Error reading verify OTP API negative test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void sectionCreateSetup() throws Exception 
    {
        try {
            LogUtils.info("Setting up test environment");
            ExtentReport.createTest("Start Section Create");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            // Get and set section create URL
            Object[][] sectionCreateData = getSectionCreateURL();
            if (sectionCreateData.length > 0) {
                String endpoint = sectionCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section Create URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section create URL found in test data");
                throw new Exception("No section create URL found in test data");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new Exception("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            sectionrequest=new sectionRequest();
            LogUtils.info("Section create setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section create setup: " + e.getMessage(), e);
            //ExtentReport.getTest().log(Status.FAIL, "Error during section create setup: " + e.getMessage());
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getSectionCreatePositiveInputData",priority = 1)
    private void verifySectionUsingValidInputData(String apiName, String testCaseId, 
    	    String testType, String description, String httpsMethod, 
    	    String requestBody, String expectedResponseBody, String statusCode ) throws Exception
    {
        try
        {
            LogUtils.info("Starting section creation test case: " + testCaseId);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Section Creation Test - " + testCaseId);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if(apiName.contains("sectioncreate") && testType.contains("positive"))
            {
                LogUtils.info("Processing positive test case for section creation");
                requestBodyJson=new JSONObject(requestBody);
                
                LogUtils.info("Setting up request parameters");
                sectionrequest.setOutlet_id(String.valueOf(requestBodyJson.getInt("outlet_id")));
                sectionrequest.setUser_id(String.valueOf(userId));
                sectionrequest.setSection_name(requestBodyJson.getString("section_name"));

                LogUtils.info("Sending POST request to create section");
                response=ResponseUtil.getResponseWithAuth(baseUri,sectionrequest,httpsMethod,accessToken);
                LogUtils.info("Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response received from section create API");
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    String responseBody = response.getBody().asString();
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        expectedJsonBody=new JSONObject(expectedResponseBody);
                     
                                                LogUtils.success(logger,"Section created successfully");
                        ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Section created successfully", ExtentColor.GREEN));
                        ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                    } else {
                        LogUtils.failure(logger, "Section creation failed - Empty response body received");
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Empty response body received", ExtentColor.RED));
                        throw new Exception("Response body is empty");
                    }
                } else {
                    LogUtils.failure(logger, "Section creation failed with status code: " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Section creation failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                    throw new Exception("Expected status code " + statusCode + " but got " + response.getStatusCode());
                }                
                
            }

        }
        catch(Exception e)
        {
            LogUtils.error("Error during section creation test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new Exception("Error during section creation test execution: " + e.getMessage());
        }
    }

    /**
     * Method to count the number of sentences in a string
     * Treats end of sentence markers (., !, ?) followed by space or end of string as sentence boundaries
     * @param text The text to analyze
     * @return The number of sentences
     */
    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        // Pattern to match end of sentences: period, exclamation, or question mark followed by space or end of string
        Pattern pattern = Pattern.compile("[.!?][ $]");
        String[] sentences = pattern.split(text);
        return sentences.length;
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

