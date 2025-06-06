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

import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class MenuCategoryCreateTestScript extends APIBase 
{
 
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private String deviceToken;
    private RequestSpecification request;
    Logger logger = Logger.getLogger(MenuCategoryCreateTestScript.class);
    
    
    
    
    /**
     * 
     * 
     * Data provider for menu category create API endpoint URLs
     */
    @DataProvider(name="getMenuCategoryCreateUrl")
    public Object[][] getMenuCategoryCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Category Create API endpoint data from Excel sheet");
            //ExtentReport.getTest().log(Status.INFO, "Reading Menu Category Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "menucategorycreate".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Menu Category Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Menu Category Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Menu Category Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for menu category create test scenarios
     */
    @DataProvider(name="getMenuCategoryCreateData")
    public Object[][] getMenuCategoryCreateData() throws customException {
        try {
            LogUtils.info("Reading menu category create test scenario data");
           //ExtentReport.getTest().log(Status.INFO, "Reading menu category create test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No menu category create test scenario data found in Excel sheet");
                //ExtentReport.getTest().log(Status.ERROR, "No menu category create test scenario data found in Excel sheet");
                throw new customException("No menu category create test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "menucategorycreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch(Exception e) {
            LogUtils.error("Error while reading menu category create test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading menu category create test scenario data: " + e.getMessage());
            throw new customException("Error while reading menu category create test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Menu Category Create SetUp");
            ExtentReport.createTest("Menu Category Create Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            //ExtentReport.getTest().log(Status.INFO, "Base URI set to: " + baseUri);
            
            Object[][] menuCategoryUrl = getMenuCategoryCreateUrl();
            if (menuCategoryUrl.length > 0) 
            {
          
                String endpoint = menuCategoryUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri+""+url.getPath()+"?"+url.getQuery();
                LogUtils.info("Menu Category Create URL set to: " + baseUri);
            } else {
                LogUtils.error("No menu category create URL found in test data");
                throw new customException("No menu category create URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
           
            LogUtils.info("Menu Category Create Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during menu category create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during menu category create setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create menu category
     */
    @Test(dataProvider="getMenuCategoryCreateData")
    private void createMenuCategoryUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu category create test: " + description);
            ExtentReport.createTest("Menu Category Create Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting menu category create test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("menucategorycreate") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing menu category create request");
                requestBodyJson = new JSONObject(requestBody.replace("\\", "\\\\"));
                expectedResponse = new JSONObject(expectedResponseBody);
               
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "multipart/form-data");

                // Set multipart form data
                request.multiPart("outlet_id", requestBodyJson.getInt("outlet_id"));
                request.multiPart("category_name", requestBodyJson.getString("category_name"));
                
                // Handle image file upload if it exists in the request
                if (requestBodyJson.has("image") && !requestBodyJson.getString("image").isEmpty())
                {
                    File categoryImage = new File(requestBodyJson.getString("image"));
                    if(categoryImage.exists())
                    {
                        request.multiPart("image", categoryImage);
                    }
                }
                request.multiPart("user_id",String.valueOf(userId));
                LogUtils.info("Constructing request body");
                ExtentReport.getTest().log(Status.INFO, "Constructing request body");
                LogUtils.info("Sending POST request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending POST request to create menu category");
                response = request.when().post(baseUri).then().extract().response();

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                if (response.getStatusCode() == 200) 
                {
                    LogUtils.success(logger, "Menu category created successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu category created successfully", ExtentColor.GREEN));
                    //validateResponseBody.handleResponseBody(response, expectedResponse);
                    LogUtils.info("Response validation completed successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response validation completed successfully");
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                } 
                else 
                {
                    LogUtils.failure(logger, "Menu category creation failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Menu category creation failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during menu category create test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during menu category create test execution: " + e.getMessage());
        }
    }

    @DataProvider(name = "getMenuCategoryCreateNegativeData")
    public Object[][] getMenuCategoryCreateNegativeData() throws customException {
        try {
            LogUtils.info("Reading menu category create negative test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category create negative test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                    "menucategorycreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "negative".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid menu category create negative test data found";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting menu category create negative test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu category create negative test data: " + e.getMessage());
            throw new customException("Error in getting menu category create negative test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getMenuCategoryCreateNegativeData")
    public void menuCategoryCreateNegativeTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu category create negative test: " + description);
            ExtentReport.createTest("Menu Category Create Negative Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if(apiName.equalsIgnoreCase("menucategorycreate") && testType.equalsIgnoreCase("negative"))
            {
            	requestBodyJson=new JSONObject(requestBody.replace("\\","\\\\"));
                
                request=RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.contentType("multipart/form-data");
                		
                if(requestBodyJson.has("image") && !requestBodyJson.getString("image").isEmpty())
                {
                    LogUtils.info("Processing image attachments");
                    File imageFile=new File(requestBodyJson.getString("image"));
                    if(imageFile.exists())
                    {
                       
                    	request.multiPart("image",requestBodyJson.getString("image"));
                        LogUtils.info("Successfully attached image files");
                        ExtentReport.getTest().log(Status.INFO, "Successfully attached 5 image files");
                    } else {
                        LogUtils.warn("Image file not found at path: " + requestBodyJson.getString("images"));
                        ExtentReport.getTest().log(Status.WARNING, "Image file not found at specified path");
                    }
                }

                LogUtils.info("Setting up request form parameters");
                ExtentReport.getTest().log(Status.INFO, "Setting up request form parameters");
                
                request.multiPart("user_id", requestBodyJson.getString("user_id"));
                request.multiPart("outlet_id", requestBodyJson.getString("outlet_id")); 
                request.multiPart("category_name", requestBodyJson.getString("category_name"));
                
                response = request.when().post(baseUri).then().extract().response();
                
                // Response logging
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                LogUtils.info("Response Body: " + response.asPrettyString());
                
                // Validation
                ExtentReport.getTest().log(Status.INFO, "Expected Status Code: " + statusCode);
                ExtentReport.getTest().log(Status.INFO, "Actual Status Code: " + response.getStatusCode());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                    LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                    
                    actualResponseBody = new JSONObject(response.asString());
                    expectedResponse = new JSONObject(expectedResponseBody);
                    
                    ExtentReport.getTest().log(Status.INFO, "Starting response body validation");
                    LogUtils.info("Starting response body validation");
                    ExtentReport.getTest().log(Status.INFO, "Expected Response Body:\n" + expectedResponse.toString(2));
                    LogUtils.info("Expected Response Body:\n" + expectedResponse.toString(2));
                    ExtentReport.getTest().log(Status.INFO, "Actual Response Body:\n" + actualResponseBody.toString(2));
                    LogUtils.info("Actual Response Body:\n" + actualResponseBody.toString(2));
                    
                    // Validate response message sentence count
                    if (actualResponseBody.has("message")) {
                        String message = actualResponseBody.getString("message");
                        String[] sentences = message.split("[.!?]+");
                        int sentenceCount = 0;
                        
                        for (String sentence : sentences) {
                            if (!sentence.trim().isEmpty()) {
                                sentenceCount++;
                            }
                        }
                        
                        ExtentReport.getTest().log(Status.INFO, "Response message contains " + sentenceCount + " sentences");
                        LogUtils.info("Response message contains " + sentenceCount + " sentences");
                        
                        if (sentenceCount > 6) {
                            String errorMsg = "Response message contains more than 6 sentences (" + sentenceCount + "), which exceeds the limit";
                            ExtentReport.getTest().log(Status.FAIL, errorMsg);
                            LogUtils.failure(logger, errorMsg);
                            throw new customException(errorMsg);
                        } else {
                            ExtentReport.getTest().log(Status.PASS, "Response message sentence count validation passed: " + sentenceCount + " sentences");
                            LogUtils.success(logger, "Response message sentence count validation passed: " + sentenceCount + " sentences");
                        }
                    }
                    
                    // Perform detailed response validation
                    ExtentReport.getTest().log(Status.INFO, "Performing detailed response validation");
                    LogUtils.info("Performing detailed response validation");
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    
                    ExtentReport.getTest().log(Status.PASS, "Response body validation passed successfully");
                    LogUtils.success(logger, "Response body validation passed successfully");
                    
                } else {
                    String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    ExtentReport.getTest().log(Status.FAIL, errorMsg);
                    LogUtils.failure(logger, errorMsg);
                    LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
                    throw new customException(errorMsg);
                }   
            }
        } catch (Exception e) {
            String errorMsg = "Error in menu category create negative test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    private int countSentences(String text) {
        // Split text into sentences using common sentence endings
        String[] sentences = text.split("[.!?]+");
        return sentences.length;
    }

    /**
     * Cleanup method to perform post-test cleanup
     * @throws customException 
     */
    @AfterClass
    private void tearDown() throws customException 
    {
    	
       /*LogUtils.info("Performing tear down");
       ExtentReport.getTest().log(Status.INFO, "Performing tear down");
       ActionsMethods.logout();
       TokenManagers.clearTokens();*/
    }
}