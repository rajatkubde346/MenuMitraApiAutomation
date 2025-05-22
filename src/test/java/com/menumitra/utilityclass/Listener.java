package com.menumitra.utilityclass;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class Listener implements ITestListener {

	@Override
	public void onStart(ITestContext context) {
		System.out.println("Test Suite started: " + context.getName());
		LogUtils.info("Test Suite started: " + context.getName());
		
		// Initialize ExtentReports
		ExtentReport.getInstance();
	}

	@Override
	public void onTestStart(ITestResult result) {
		System.out.println("Test case started: " + result.getMethod().getMethodName());
		
		// Create a test in ExtentReports
		/*ExtentReport.setTest(ExtentReport.getInstance().createTest(result.getMethod().getMethodName(), 
				"Test Description: " + result.getMethod().getDescription()));*/
		
		LogUtils.info(result.getMethod().getMethodName() + " test case started!");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		System.out.println("Test case passed: " + result.getMethod().getMethodName());
		LogUtils.info("Test case passed: " + result.getMethod().getMethodName());
		
		ExtentReport.getTest().log(Status.PASS,
				MarkupHelper.createLabel(result.getName() + " - Test Case PASSED", ExtentColor.GREEN));
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("Test case failed: " + result.getMethod().getMethodName());
		LogUtils.error("Test case failed: " + result.getMethod().getMethodName());
		
		ExtentReport.getTest().log(Status.FAIL,
				MarkupHelper.createLabel(result.getName() + " - Test Case FAILED", ExtentColor.RED));
		ExtentReport.getTest().fail(result.getThrowable());
	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("Test Suite finished: " + context.getName());
		LogUtils.info("Test Suite finished: " + context.getName());
		
		// Flush the ExtentReports to generate the report
		ExtentReport.flushReport();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		System.out.println("Test case skipped: " + result.getMethod().getMethodName());
		LogUtils.warn("Test case skipped: " + result.getMethod().getMethodName());
		
		ExtentReport.getTest().log(Status.SKIP,
				MarkupHelper.createLabel(result.getName() + " - Test Case SKIPPED", ExtentColor.ORANGE));
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println("Test failed but within success percentage: " + result.getMethod().getMethodName());
	}
}
