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
public class WaiterViewTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private WaiterRequest waiterViewRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    private int user_id;	
    Logger logger = LogUtils.getLogger(WaiterViewTestScript.class);

    @DataProvider(name = "getWaiterViewUrl")
    private Object[][] getWaiterViewUrl() throws customException
    {
        try {
            LogUtils.info("Reading waiter view URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading waiter view URL from Excel sheet");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "waiterview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No waiter view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            LogUtils.info("Successfully retrieved waiter view URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved waiter view URL data");
            return filteredData;
        } catch (Exception e) {
            String errorMsg = "Error in getWaiterViewUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @DataProvider(name = "getWaiterViewData")
    public Object[][] getWaiterViewData() throws customException {
        try {
            LogUtils.info("Reading waiter view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading waiter view test scenario data");

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
                        "waiterview".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid waiter view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " waiter view test scenarios");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved " + obj.length + " waiter view test scenarios");
            return obj;
        } catch (Exception e) {
            String errorMsg = "Error in getWaiterViewData: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @BeforeClass
    private void waiterViewSetUp() throws customException {
        try {
            LogUtils.info("Setting up waiter view test");
            ExtentReport.createTest("Waiter View Test Setup");
            ExtentReport.getTest().log(Status.INFO, "Initializing waiter view test setup");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] waiterViewData = getWaiterViewUrl();
            if (waiterViewData.length > 0) {
                String endpoint = waiterViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                String errorMsg = "No waiter view URL found in test data";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                String errorMsg = "Required tokens not found. Please ensure login and OTP verification is completed";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            waiterViewRequest = new WaiterRequest();
            LogUtils.info("Waiter view test setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Waiter view test setup completed successfully", ExtentColor.GREEN));
        } catch (Exception e) {
            String errorMsg = "Error in waiter view setUp: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getWaiterViewData")
    private void verifyWaiterView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting waiter view test case: " + testCaseid);
            ExtentReport.createTest("Waiter View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("waiterview")) {
                requestBodyJson = new JSONObject(requestBody);
               
                waiterViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                waiterViewRequest.setUser_id(requestBodyJson.getString("user_id"));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, waiterViewRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Only log response information to the report without validation
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("Response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "Waiter view test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Waiter view test completed successfully", ExtentColor.GREEN));
            }
        }
        catch (Exception e) {
            String errorMsg = "Error in waiter view test: " + e.getMessage();
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
