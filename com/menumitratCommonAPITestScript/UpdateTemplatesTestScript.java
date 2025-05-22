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
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class UpdateTemplatesTestScript extends APIBase 
{
    private Response response;
    private JSONObject requestBodyJson;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private RequestSpecification request;
    Logger logger = Logger.getLogger(UpdateTemplatesTestScript.class);
    
    /**
     * Data provider for templates API endpoint URLs
     */
    @DataProvider(name="getUpdateTemplatesUrl")
    public Object[][] getUpdateTemplatesUrl() throws customException {
        try {
            LogUtils.info("Reading Update Templates API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "updatetemplates".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Update Templates API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Update Templates API endpoint data from Excel sheet");
            throw new customException("Error While Reading Update Templates API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for update templates test scenarios
     */
    @DataProvider(name="getUpdateTemplatesData")
    public Object[][] getUpdateTemplatesData() throws customException {
        try {
            LogUtils.info("Reading update templates test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No update templates test scenario data found in Excel sheet");
                throw new customException("No update templates test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "updatetemplates".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.error("Error while reading update templates test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading update templates test scenario data: " + e.getMessage());
            throw new customException("Error while reading update templates test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Update Templates SetUp");
            ExtentReport.createTest("Update Templates Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            Object[][] updateTemplatesUrl = getUpdateTemplatesUrl();
            if (updateTemplatesUrl.length > 0) 
            {
                String endpoint = updateTemplatesUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri + "" + url.getPath() + "?" + url.getQuery();
                LogUtils.info("Update Templates URL set to: " + baseUri);
            } else {
                LogUtils.error("No update templates URL found in test data");
                throw new customException("No update templates URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
            LogUtils.info("Update Templates Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during update templates setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during update templates setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to update templates
     */
    @Test(dataProvider="getUpdateTemplatesData")
    private void updateTemplatesUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting update templates test: " + description);
            ExtentReport.createTest("Update Templates Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting update templates test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("updatetemplates") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing update templates request");
                requestBodyJson = new JSONObject(requestBody.replace("\\", "\\\\"));
                
                System.out.println("Access Token: " + accessToken);
                
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "multipart/form-data");

                // Set multipart form data
                if (requestBodyJson.has("template_id")) {
                    request.multiPart("template_id", requestBodyJson.getInt("template_id"));
                }
                
                if (requestBodyJson.has("name")) {
                    request.multiPart("name", requestBodyJson.getString("name"));
                }
                
                if (requestBodyJson.has("qr_overlay_position")) {
                    request.multiPart("qr_overlay_position", requestBodyJson.getString("qr_overlay_position"));
                }
                
                if (requestBodyJson.has("image") && !requestBodyJson.getString("image").isEmpty())
                {
                    File templateImage = new File(requestBodyJson.getString("image"));
                    if(templateImage.exists())
                    {
                        request.multiPart("image", templateImage);
                    }
                }
                
                LogUtils.info("Constructing request body");
                ExtentReport.getTest().log(Status.INFO, "Constructing request body");
                LogUtils.info("Sending PUT request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending PUT request to update template");
                
                response = request.when().put(baseUri).then().extract().response();

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                
                if (response.getStatusCode() == 200) 
                {
                    LogUtils.success(logger, "Template updated successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Template updated successfully", ExtentColor.GREEN));
                    LogUtils.info("Response received successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response received successfully");
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                } 
                else 
                {
                    LogUtils.failure(logger, "Template update failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Template update failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during update templates test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during update templates test execution: " + e.getMessage());
        }
    }

    /**
     * Cleanup method to perform post-test cleanup
     */
    @AfterClass
    private void tearDown() throws customException 
    {
        // No clean up needed as per requirements
    }
} 