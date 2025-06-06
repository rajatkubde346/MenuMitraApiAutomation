package com.menumitra.utilityclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.testng.annotations.DataProvider;

public class ApiEndpoint
{
    private static final String ExcelSheetPathForGetApis="src\\test\\resources\\excelsheet\\apiEndpoint.xlsx";
    List<Map<String,String>> getApiListData;
    Object[][] getDataFormExcel;


    public static List<Map<String,String>> getApiList(String sheetName) throws customException
    {
        return getApiDataFromExcel(sheetName);
    }

   
    private static List<Map<String,String>> getApiDataFromExcel(String sheetName) throws customException
    {
        ApiEndpoint api=new ApiEndpoint();
        try
        {
            api.getDataFormExcel = DataDriven.readExcelData(ExcelSheetPathForGetApis, sheetName);
            api.getApiListData = new ArrayList<>();

            // Get headers from first row
            String[] headers = new String[api.getDataFormExcel[0].length];
            for (int i = 0; i < api.getDataFormExcel[0].length; i++) {
                headers[i] = api.getDataFormExcel[0][i].toString();
            }

            // Start from row 1 to skip headers
            for (int i = 1; i < api.getDataFormExcel.length; i++)
            {
                Map<String, String> putAPIData = new HashMap<>();
                for (int j = 0; j < headers.length; j++)
                {
                    putAPIData.put(headers[j], api.getDataFormExcel[i][j].toString());
                }
                api.getApiListData.add(putAPIData);
            }
            return api.getApiListData;
        }
        catch(Exception e)
        {
            throw new customException("Error reading API data from Excel sheet: " + e.getMessage());
        }

    }

    
}
