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
import com.menumitra.apiRequest.MenuRequest;
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
public class MakeMenuSpecialNonSpecial extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuRequest menuRequest;
    private URL url;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    private int userId;
    Logger logger = LogUtils.getLogger(MakeMenuSpecialNonSpecial.class);
   
    @BeforeClass
    private void makeMenuSpecialNonSpecialSetUp() throws customException
    {
        try
        {
            LogUtils.info("Make Menu Special Non-Special SetUp");
            ExtentReport.createTest("Make Menu Special Non-Special SetUp");
            ExtentReport.getTest().log(Status.INFO,"Make Menu Special Non-Special SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getMakeMenuSpecialNonSpecialUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No make menu special/non-special URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No make menu special/non-special URL found in test data");
                throw new customException("No make menu special/non-special URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId=TokenManagers.getUserId();
            if(accessToken.isEmpty())
            {
                ActionsMethods.login();
                ActionsMethods.verifyOTP();
                accessToken = TokenManagers.getJwtToken();
                LogUtils.failure(logger,"Access Token is Empty check access token");
                ExtentReport.getTest().log(Status.FAIL,MarkupHelper.createLabel("Access Token is Empty check access token",ExtentColor.RED));
                throw new customException("Access Token is Empty check access token");
            }
            
            menuRequest = new MenuRequest();
          
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        }
        catch(Exception e)
        {
            LogUtils.exception(logger, "Error in make menu special/non-special setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error in make menu special/non-special setup: " + e.getMessage());
            throw new customException("Error in make menu special/non-special setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name="getMakeMenuSpecialNonSpecialUrl")
    private Object[][] getMakeMenuSpecialNonSpecialUrl() throws customException
    {
        try
        {
            LogUtils.info("Reading make menu special/non-special URL from Excel sheet");
            ExtentReport.getTest().log(Status.INFO, "Reading make menu special/non-special URL from Excel sheet");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            if(readExcelData == null)
            {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "makemenuspecialnonspecial".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
                    
            if(filteredData.length == 0) {
                String errorMsg = "No make menu special/non-special URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            LogUtils.info("Successfully retrieved make menu special/non-special URL data");
            ExtentReport.getTest().log(Status.PASS, "Successfully retrieved make menu special/non-special URL data");
            return filteredData;
        }
        catch(Exception e)
        {
            String errorMsg = "Error in getMakeMenuSpecialNonSpecialUrl: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
}
