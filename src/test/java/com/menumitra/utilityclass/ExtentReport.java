package com.menumitra.utilityclass;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReport
{

	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal();
	private static final String OUTPUT_FOLDER = "src\\test\\resources\\extentReport\\report.html";
	

	public static ExtentReports getInstance() {
		if (extent == null) {
			createInstance();
		}
		return extent;
	}

	private static void createInstance() {
		// Create the report directory if it doesn't exist
		File reportDir = new File(OUTPUT_FOLDER);
		if (!reportDir.exists()) {
			reportDir.mkdirs();
			System.out.println("Created directory: " + reportDir.getAbsolutePath());
		}
		
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		String reportPath = OUTPUT_FOLDER + "MenuMitraReport_" + timestamp + ".html";
		
		//System.out.println("Creating Extent Report at: " + reportPath);
		
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
		
		try {
			sparkReporter.config().setTheme(Theme.STANDARD);
			sparkReporter.config().setDocumentTitle("MenuMitra API Test Report");
			sparkReporter.config().setReportName("API Automation Test Results");
			sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
		} catch (Exception e) {
			System.out.println("Exception in ExtentReport configuration: " + e.getMessage());
			e.printStackTrace();
		}

		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		extent.setSystemInfo("Operating System", System.getProperty("os.name"));
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Environment", "Test");
		extent.setSystemInfo("Project", "MenuMitra API Automation");
		extent.setSystemInfo("Test Type", "API Testing");
		
		//System.out.println("Extent Report initialized successfully");
		
	}

	// Add this method
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }

    // Add overloaded method for single parameter
    public static ExtentTest createTest(String testName) {
        return createTest(testName, "");
    }
	public static ExtentTest getTest() {
		return test.get();
	}

	public static void setTest(ExtentTest extentTest) {
		test.set(extentTest);
	}

	public static void removeTest() {
		test.remove();
	}
	
	public static void flushReport() {
		if (extent != null) {
			System.out.println("Flushing Extent Report");
			
			extent.flush();
		}
	}
}
