package com.TestCases;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.Pages.ProductPage;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import WebUtils.WebUtil;

public class SearchProduct {
	private ExtentReports extRepo;
	private ExtentTest test;
	private WebUtil we;
	private ProductPage prPage;

	@BeforeMethod
	@Parameters({ "browser", "url" })
	public void setUp(String browser, String url) { // Fixed parameter name
		// Setup Extent Reports
		extRepo = new ExtentReports();
		ExtentSparkReporter extSpark = new ExtentSparkReporter("test-output/ProductReport.html");
		extRepo.attachReporter(extSpark);

		test = extRepo.createTest("Product Search on Amazon");
		we = new WebUtil(test);
		we.launchBrowser(browser, 10); // Use corrected parameter name
		we.openURL(url);
		prPage = new ProductPage(we);
	}

	@Test
	@Parameters({ "productName" }) // Fixed parameter name and syntax
	public void testSearchProduct(String productName) {

		prPage.ProductAm(productName);

	}

	@AfterMethod
	public void tearDown() {
		if (extRepo != null) {
			extRepo.flush();
		}
		if (we != null && we.getDriver() != null) {
			we.quitBrowser();
		}
	}
}