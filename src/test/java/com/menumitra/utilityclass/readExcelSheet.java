package com.menumitra.utilityclass;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

public class readExcelSheet {
    public static Workbook workbook;
    public static Sheet sheet;
    public static Row row;
    public static Cell cell;
    public static FileInputStream fis;
    public static FileOutputStream fos;

    /**
     * Reads the Excel file and returns the specified sheet.
     * 
     * @param excelPath Path to the Excel file.
     * @param sheetName Name of the sheet to read.
     * @return Sheet object containing the data.
     * @throws CustomExceptions If there is an error while reading the file.
     */
    private static Sheet readExcelSheet(String excelPath, String sheetName) throws customException {
        try {
            fis = new FileInputStream(excelPath); // Open the file
            workbook = new XSSFWorkbook(fis); // Load the workbook
            sheet = workbook.getSheet(sheetName); // Get the specified sheet
            LogUtils.info("Excel sheet read successfully.");
            return sheet;
        } catch (IOException e) {
            LogUtils.error("Error reading the Excel file: " + e.getMessage());
            throw new customException("Error reading the Excel file: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.error("Unexpected error occured while reading excel sheet " + e.getMessage());
            throw new customException("Unexpected error occured while reading excel sheet " + e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    LogUtils.info("Excel file closed successfully.");
                }
            } catch (IOException e) {
                LogUtils.error("Error closing excel file. ");
                throw new customException("Error closing excel file: " + e.getMessage());
            }
        }

    }

    /**
     * Reads data from an Excel sheet and returns it as a 2D array.
     * 
     * @param excelPath Path to the Excel file.
     * @param sheetName Name of the sheet to read.
     * @return 2D array containing the sheet data.
     * @throws CustomExceptions If there is an error while reading the data.
     */
    public static Object[][] readExcelData(String excelPath, String sheetName) throws customException {
        if (excelPath == null || excelPath.trim().isEmpty()) {
            throw new customException("Excel path cannot be null or empty");
        }
        if (sheetName == null || sheetName.trim().isEmpty()) {
            throw new customException("Sheet name cannot be null or empty");
        }

        try {
            sheet = readExcelSheet(excelPath, sheetName);
            if (sheet == null) {
                throw new customException("Sheet '" + sheetName + "' not found in the Excel file");
            }

            int lastRow = sheet.getLastRowNum();
            if (lastRow < 0) {
                throw new customException("No data found in sheet '" + sheetName + "'");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new customException("Header row is empty in sheet '" + sheetName + "'");
            }

            int lastCell = headerRow.getLastCellNum();
            if (lastCell <= 0) {
                throw new customException("No columns found in sheet '" + sheetName + "'");
            }

            Object[][] data = new Object[lastRow + 1][lastCell];

            // Iterate over each row
            for (int i = 0; i <= lastRow; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    // Fill the row with null values
                    for (int j = 0; j < lastCell; j++) {
                        data[i][j] = null;
                    }
                    continue;
                }

                // Iterate over each cell in the row
                for (int j = 0; j < lastCell; j++) {
                    cell = row.getCell(j);

                    // Handle cell data based on its type
                    if (cell != null) {
                        try {
                            switch (cell.getCellType()) {
                                case 1: // STRING
                                    String stringValue = cell.getStringCellValue();
                                    // Try to parse as number if it looks like a number
                                    if (stringValue.matches("-?\\d+(\\.\\d+)?")) {
                                        try {
                                            if (stringValue.contains(".")) {
                                                data[i][j] = Double.parseDouble(stringValue);
                                            } else {
                                                data[i][j] = Long.parseLong(stringValue);
                                            }
                                        } catch (NumberFormatException e) {
                                            data[i][j] = stringValue;
                                        }
                                    } else {
                                        data[i][j] = stringValue;
                                    }
                                    break;
                                case 0: // NUMERIC
                                    // Check if the numeric value is actually a date
                                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                                        data[i][j] = cell.getDateCellValue().toString();
                                    } else {
                                        // Convert numeric to string without decimal places if it's a whole number
                                        double numericValue = cell.getNumericCellValue();
                                        if (numericValue == Math.floor(numericValue)) {
                                            data[i][j] = String.valueOf((long) numericValue);
                                        } else {
                                            data[i][j] = String.valueOf(numericValue);
                                        }
                                    }
                                    break;
                                case 4: // BOOLEAN
                                    data[i][j] = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                case 2: // FORMULA
                                    try {
                                        String formulaValue = cell.getStringCellValue();
                                        // Try to parse as number if it looks like a number
                                        if (formulaValue.matches("-?\\d+(\\.\\d+)?")) {
                                            try {
                                                if (formulaValue.contains(".")) {
                                                    data[i][j] = Double.parseDouble(formulaValue);
                                                } else {
                                                    data[i][j] = Long.parseLong(formulaValue);
                                                }
                                            } catch (NumberFormatException e) {
                                                data[i][j] = formulaValue;
                                            }
                                        } else {
                                            data[i][j] = formulaValue;
                                        }
                                    } catch (IllegalStateException e) {
                                        try {
                                            double numericValue = cell.getNumericCellValue();
                                            if (numericValue == Math.floor(numericValue)) {
                                                data[i][j] = String.valueOf((long) numericValue);
                                            } else {
                                                data[i][j] = String.valueOf(numericValue);
                                            }
                                        } catch (IllegalStateException ex) {
                                            data[i][j] = cell.getCellFormula();
                                        }
                                    }
                                    break;
                                default:
                                    data[i][j] = null;
                                    break;
                            }
                        } catch (Exception e) {
                            // If there's any error reading the cell, try to get it as a string
                            try {
                                String cellValue = cell.toString();
                                // Try to parse as number if it looks like a number
                                if (cellValue.matches("-?\\d+(\\.\\d+)?")) {
                                    try {
                                        if (cellValue.contains(".")) {
                                            data[i][j] = Double.parseDouble(cellValue);
                                        } else {
                                            data[i][j] = Long.parseLong(cellValue);
                                        }
                                    } catch (NumberFormatException ex) {
                                        data[i][j] = cellValue;
                                    }
                                } else {
                                    data[i][j] = cellValue;
                                }
                            } catch (Exception ex) {
                                data[i][j] = null;
                                LogUtils.warn("Could not read cell value at row " + i + ", column " + j + ": " + ex.getMessage());
                            }
                        }
                    } else {
                        data[i][j] = null; // In case the cell is empty
                    }
                }
            }

            // Validate that we have data
            boolean hasData = false;
            for (int i = 1; i <= lastRow; i++) { // Start from 1 to skip header
                for (int j = 0; j < lastCell; j++) {
                    if (data[i][j] != null) {
                        hasData = true;
                        break;
                    }
                }
                if (hasData) break;
            }

            if (!hasData) {
                throw new customException("No data found in sheet '" + sheetName + "' after header row");
            }

            LogUtils.info("Excel data read successfully from sheet '" + sheetName + "'");
            return data;

        } catch (customException e) {
            LogUtils.error("Error reading Excel data: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LogUtils.error("Unexpected error occurred while reading excel sheet: " + e.getMessage());
            throw new customException("Unexpected error occurred while reading excel sheet: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                    LogUtils.info("Excel workbook closed successfully.");
                }
            } catch (IOException e) {
                LogUtils.error("Error closing excel workbook: " + e.getMessage());
            }
        }
    }

    /**
     * Reads Excel data and returns it as a list of maps where each map represents a row.
     * The map uses column index as key and cell value as value.
     * 
     * @param excelPath Path to the Excel file
     * @param sheetName Name of the sheet to read
     * @return List of maps containing row data
     * @throws customException If there is an error reading the Excel file
     */
    public static List<Map<Integer, String>> readExcelDataAsMap(String excelPath, String sheetName) throws customException {
        // Get the Excel sheet
        Sheet sheet = readExcelSheet(excelPath, sheetName);
        DataDriven obj = new DataDriven();
        
        // Get sheet dimensions
        int startRow = obj.getstartRowdata(sheet);
        int lastRow = sheet.getLastRowNum();
        int lastCell = sheet.getRow(startRow).getLastCellNum();
        
        List<Map<Integer, String>> excelData = new ArrayList<>();
        
        // Iterate through rows
        for (int rowIndex = startRow; rowIndex <= lastRow; rowIndex++) {
            Row currentRow = sheet.getRow(rowIndex);
            if (currentRow == null) continue;
            
            Map<Integer, String> rowData = new HashMap<>();
            
            // Iterate through cells in the row
            for (int columnIndex = 0; columnIndex < lastCell; columnIndex++) {
                Cell cell = currentRow.getCell(columnIndex);
                if (cell != null) {
                    try {
                        rowData.put(columnIndex, cell.getStringCellValue());
                    } catch (IllegalStateException e) {
                        // Handle non-string cell values
                        LogUtils.warn("Non-string value found at row " + rowIndex + ", column " + columnIndex);
                        rowData.put(columnIndex, String.valueOf(cell.getNumericCellValue()));
                    }
                }
            }
            excelData.add(rowData);
        }
        return excelData;
    }

    public  int getstartRowdata(Sheet sheet)
    {
        int startRow=0;
        int lastRow=sheet.getRow(0).getLastCellNum();
       
        for(int i=0;i<lastRow;i++)
        {
           if(sheet.getRow(i).getCell(i)!=null)
           {
        	   startRow=i;
        	   break;
           }
        }
        return startRow;
    }

    /**
     * Reads menu update data from Excel sheet with specific handling for menu update scenarios
     * 
     * @param excelPath Path to the Excel file
     * @param sheetName Name of the sheet to read
     * @param apiType Type of API data to read (e.g., "menuupdate")
     * @return 2D array containing the filtered data
     * @throws customException If there is an error reading the data
     */
    public static Object[][] readMenuUpdateData(String excelPath, String sheetName, String apiType) throws customException {
        try {
            LogUtils.info("Reading " + apiType + " data from Excel sheet");
            
            // First read all data
            Object[][] allData = readExcelData(excelPath, sheetName);
            if (allData == null || allData.length == 0) {
                throw new customException("No data found in sheet '" + sheetName + "'");
            }

            // Validate header row
            if (allData[0] == null || allData[0].length < 3) {
                throw new customException("Invalid header row in sheet '" + sheetName + "'");
            }

            // Filter data for the specific API type
            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 1; i < allData.length; i++) { // Start from 1 to skip header
                if (allData[i] != null && allData[i].length > 0) {
                    String rowApiType = allData[i][0] != null ? allData[i][0].toString().trim() : "";
                    if (apiType.equalsIgnoreCase(rowApiType)) {
                        // Validate required fields
                        boolean isValidRow = true;
                        for (int j = 0; j < allData[i].length; j++) {
                            if (allData[i][j] == null) {
                                LogUtils.warn("Null value found in row " + i + ", column " + j + ". Setting to empty string.");
                                allData[i][j] = "";
                            }
                        }
                        if (isValidRow) {
                            filteredData.add(allData[i]);
                        }
                    }
                }
            }

            if (filteredData.isEmpty()) {
                throw new customException("No data found for API type '" + apiType + "' in sheet '" + sheetName + "'");
            }

            // Convert to 2D array
            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully read " + result.length + " rows of " + apiType + " data");
            return result;

        } catch (customException e) {
            LogUtils.error("Error reading " + apiType + " data: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LogUtils.error("Unexpected error reading " + apiType + " data: " + e.getMessage());
            throw new customException("Unexpected error reading " + apiType + " data: " + e.getMessage());
        }
    }

    /**
     * Validates a row of menu update data
     * 
     * @param row The row data to validate
     * @param rowIndex The index of the row (for error messages)
     * @return true if the row is valid, false otherwise
     */
    private static boolean validateMenuUpdateRow(Object[] row, int rowIndex) {
        if (row == null || row.length < 3) {
            LogUtils.warn("Invalid row structure at index " + rowIndex);
            return false;
        }

        // Validate required fields
        if (row[0] == null || row[0].toString().trim().isEmpty()) {
            LogUtils.warn("Missing API type in row " + rowIndex);
            return false;
        }

        if (row[1] == null || row[1].toString().trim().isEmpty()) {
            LogUtils.warn("Missing test case ID in row " + rowIndex);
            return false;
        }

        if (row[2] == null || row[2].toString().trim().isEmpty()) {
            LogUtils.warn("Missing test type in row " + rowIndex);
            return false;
        }

        return true;
    }
}
