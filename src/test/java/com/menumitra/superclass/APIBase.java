package com.menumitra.superclass;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.Status;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.ReadFile;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.EnviromentChanges.Environment;


/**
 * Base class for all test classes
 * Contains common setup and configuration methods
 */
public class APIBase 
{
	// Properties object to store configuration properties like browser type, URLs,etc.
	public static Properties property;

	// Path to the properties configuration file
	private static String propertyFilePath = "src\\test\\resources\\configFile\\config.properties";

	// Excel file path and sheet name for test data
	public static String excelSheetPathForGetApis = "src\\test\\resources\\excelsheet\\apiEndpoint.xlsx";
	public static String appsheetName = "userapp";

	// FileInputStream object for reading the properties file
	private static FileInputStream fis;

	public static List<Map<String,String>> getAllApiInList;

	/**
	 * @BeforeSuite Annotation
	 *              This method runs before the entire test suite starts.
	 *              It loads the configuration properties file and initializes the
	 *              logging mechanism.
	 * 
	 * @throws CustomExceptions If an error occurs while loading the properties file
	 *                          or configuring the logger.
	 */
	@BeforeSuite
	public static void setUp() throws customException 
	{
		try 
		{
			// Initialize Log4j configuration first
		
			LogUtils.initializeLogger();
			
			LogUtils.info("Starting test suite setup");

			// Load the properties file to read configurations
			loadPropertiesFile();
			LogUtils.info("Properties file loaded successfully");
			
			//getAllApiInList=ApiEndpoint.getApiList("userapp");
			String enviroment=property.getProperty("Enviroment").toLowerCase();
			
			switch (enviroment)
			{
				case "qa":
					
						EnviromentChanges.setEnvironment(Environment.QA);
						LogUtils.info("Setting QA environment...");
					break;
				case "production":
						EnviromentChanges.setEnvironment(Environment.PRODUCTION);
						LogUtils.info("Setting production environment...");
					break;
				default:
						EnviromentChanges.setEnvironment(Environment.QA);
						LogUtils.info("Set Default QA Enviroment.");
					break;
			}
			
			// Log the setup completion
			LogUtils.info("Setup completed. Log4j configured and properties loaded.");
			
			
		}
		catch (Exception e)
		{
			LogUtils.error("Error during configuration setup: " + e.getMessage());
			throw new customException("Error during configuration setup: " + e.getMessage());
		}
	}

	/**
	 * Initializes the properties file by reading and loading the properties.
	 * Throws CustomExceptions in case of errors during file reading or loading.
	 * 
	 * @throws CustomExceptions If there is any error while reading or loading the
	 *                          properties file.
	 */
	public static void loadPropertiesFile() throws customException 
	{
		try {
			LogUtils.info("Loading properties file from: " + propertyFilePath);

			// Use ReadFile class to read the properties file
			fis = ReadFile.readFile("src\\test\\resources\\configFile\\config.properties");
			LogUtils.info("Properties file opened successfully");

			// Initialize the Properties object
			property = new Properties();
			LogUtils.info("Properties object initialized");

			// Load the properties from the input stream
			property.load(fis);
			LogUtils.info("Properties loaded successfully");

		} catch (FileNotFoundException e) {
			LogUtils.error("Properties file not found: " + e.getMessage());
			// Custom exception for file not found errors
			throw new customException(
					"File not found or could not be loaded. Please check the file location: " + e.getMessage());
		} catch (IOException e) {
			LogUtils.error("IOException while loading the properties file: " + e.getMessage());
			// Custom exception for IO exceptions while loading the file
			throw new customException("Unable to load the file. Input/Output exception occurred: " + e.getMessage());
		} catch (Exception e) {
			LogUtils.error("Unexpected error occurred while loading the properties file: " + e.getMessage());
			// Catch any other unexpected errors
			throw new customException("Unexpected error occurred while loading the properties file: " + e.getMessage());
		}
	}

	@AfterSuite
	void tearDown() 
	{
		
	}
}
