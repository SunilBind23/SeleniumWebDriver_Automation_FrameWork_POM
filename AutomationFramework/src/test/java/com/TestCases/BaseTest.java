package com.TestCases;

import java.time.Duration;

import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import WebUtils.WebUtil;

public class BaseTest {
	protected ExtentReports extent;
	protected ExtentTest test;
	protected WebUtil wu;

	@BeforeTest
	public void setUpSuite() {
		ExtentSparkReporter spark = new ExtentSparkReporter("test-output/Index.html");
		extent = new ExtentReports();
		extent.attachReporter(spark);
		spark.config().setTheme(Theme.DARK);
	}

	@BeforeMethod
	public void setUp(org.testng.ITestContext context) {
		String testName = context.getName();
		test = extent.createTest(testName);

	}

	@AfterMethod
	public void tearDown(WebUtil wu) {
		if (test.getStatus() == Status.FAIL) {
			wu.takeScreenShot("Test Case Faild");
		} else {
			wu.closeBrowser();
		}
	}

	@AfterSuite
	public void tearDownSuite(WebUtil wu) {
		wu.quitBrowser();
		extent.flush();
	}

}
