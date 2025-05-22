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
public class SectionDeleteTestScript extends APIBase
{
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedJson;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private sectionRequest sectionrequest;
    Logger logger = LogUtils.getLogger(SectionDeleteTestScript.class);

    @DataProvider(name="getSectionDeleteURL")
    public Object[][] getSectionDeleteURL() throws Exception {
        try {
            Object[][] readData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readData == null) {
                LogUtils.failure(logger, "Error: Getting an error while read Section Delete URL Excel File");
                throw new Exception("Error: Getting an error while read Section Delete URL Excel File");
            }
            
            return Arrays.stream(readData)
                    .filter(row -> "sectiondelete".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.exception(logger, "Error: Getting an error while read Section Delete URL Excel File", e);
            throw new Exception("Error: Getting an error while read Section Delete URL Excel File");
        }
    }

    @DataProvider(name="getSectionDeletePositiveInputData")
    private Object[][] getSectionDeletePositiveInputData() throws Exception {
        try {
            LogUtils.info("Reading positive test scenario data for section delete API");
            Object[][] testData = DataDriven.readExcelData(excelSheetPathForGetApis, property.getProperty("CommonAPITestScenario"));
            
            if (testData == null || testData.length == 0) {
                LogUtils.failure(logger, "No Section Delete API positive test scenario data found in Excel sheet");
                throw new Exception("No Section Delete API Positive test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];
                if (row != null && row.length >= 3 &&
                    "sectiondelete".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.exception(logger, "Failed to read section delete API positive test scenario data", e);
            throw new Exception("Error reading section delete API positive test scenario data");
        }
    }

    @BeforeClass
    private void sectionDeleteSetup() throws Exception {
        try {
            LogUtils.info("Setting up test environment for section delete");
            ExtentReport.createTest("Start Section Delete");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);

            Object[][] sectionDeleteData = getSectionDeleteURL();
            if (sectionDeleteData.length > 0) {
                String endpoint = sectionDeleteData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                LogUtils.info("Section Delete URL set to: " + baseUri);
            } else {
                LogUtils.failure(logger, "No section delete URL found in test data");
                throw new Exception("No section delete URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Required tokens not found");
                throw new Exception("Required tokens not found");
            }

            sectionrequest = new sectionRequest();
            LogUtils.info("Section delete setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section delete setup: " + e.getMessage(), e);
            throw new Exception("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getSectionDeletePositiveInputData", priority = 1)
    private void verifySectionDeleteUsingValidInputData(String apiName, String testCaseId,
            String testType, String description, String httpsMethod,
            String requestBody, String expectedResponseBody, String statusCode) throws Exception {
        try {
            LogUtils.info("Start section delete API test");
            ExtentReport.createTest("Verify Section Delete API: " + description);
            
            requestBodyJson = new JSONObject(requestBody);
            
            sectionrequest.setSection_id(requestBodyJson.getString("section_id"));
            sectionrequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            sectionrequest.setUser_id(String.valueOf(userId));
            
            // API call
            response = ResponseUtil.getResponseWithAuth(baseUri, sectionrequest, httpsMethod, accessToken);
            
            // Log response
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Body: " + response.asString());
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
            
            // Mark test as passed
            LogUtils.success(logger, "Section delete test completed");
            ExtentReport.getTest().log(Status.PASS, "Section delete test completed");
            
        } catch (Exception e) {
            LogUtils.exception(logger, "Error during section delete verification: " + e.getMessage(), e);
            ExtentReport.getTest().log(Status.FAIL, "Error during section delete verification: " + e.getMessage());
            throw new Exception("Error during section delete verification");
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

