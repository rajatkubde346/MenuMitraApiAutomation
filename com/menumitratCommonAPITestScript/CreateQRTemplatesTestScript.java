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
public class CreateQRTemplatesTestScript extends APIBase 
{
    private Response response;
    private JSONObject requestBodyJson;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private RequestSpecification request;
    Logger logger = Logger.getLogger(CreateQRTemplatesTestScript.class);
    
    /**
     * Data provider for QR templates API endpoint URLs
     */
    @DataProvider(name="getCreateQRTemplatesUrl")
    public Object[][] getCreateQRTemplatesUrl() throws customException {
        try {
            LogUtils.info("Reading Create QR Templates API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "createqrtemplates".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Create QR Templates API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Create QR Templates API endpoint data from Excel sheet");
            throw new customException("Error While Reading Create QR Templates API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for create QR templates test scenarios
     */
    @DataProvider(name="getCreateQRTemplatesData")
    public Object[][] getCreateQRTemplatesData() throws customException {
        try {
            LogUtils.info("Reading create QR templates test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No create QR templates test scenario data found in Excel sheet");
                throw new customException("No create QR templates test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "createqrtemplates".equalsIgnoreCase(Objects.toString(row[0], "")) &&
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
            LogUtils.error("Error while reading create QR templates test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading create QR templates test scenario data: " + e.getMessage());
            throw new customException("Error while reading create QR templates test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Create QR Templates SetUp");
            ExtentReport.createTest("Create QR Templates Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            Object[][] qrTemplatesUrl = getCreateQRTemplatesUrl();
            if (qrTemplatesUrl.length > 0) 
            {
                String endpoint = qrTemplatesUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri + "" + url.getPath() + "?" + url.getQuery();
                LogUtils.info("Create QR Templates URL set to: " + baseUri);
            } else {
                LogUtils.error("No create QR templates URL found in test data");
                throw new customException("No create QR templates URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
            LogUtils.info("Create QR Templates Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during create QR templates setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during create QR templates setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create QR templates
     */
    @Test(dataProvider="getCreateQRTemplatesData")
    private void createQRTemplatesUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting create QR templates test: " + description);
            ExtentReport.createTest("Create QR Templates Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting create QR templates test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("createqrtemplates") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing create QR templates request");
                requestBodyJson = new JSONObject(requestBody.replace("\\", "\\\\"));
                
                System.out.println("Access Token: " + accessToken);
                
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "multipart/form-data");

                // Set multipart form data
                request.multiPart("user_id", userId);
                request.multiPart("name", requestBodyJson.getString("name"));
                request.multiPart("qr_overlay_position", requestBodyJson.getString("qr_overlay_position"));
                
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
                LogUtils.info("Sending POST request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending POST request to create QR template");
                
                response = request.when().post(baseUri).then().extract().response();

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) 
                {
                    LogUtils.success(logger, "QR template created successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("QR template created successfully", ExtentColor.GREEN));
                    LogUtils.info("Response received successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response received successfully");
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                } 
                else 
                {
                    LogUtils.failure(logger, "QR template creation failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("QR template creation failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during create QR templates test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during create QR templates test execution: " + e.getMessage());
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