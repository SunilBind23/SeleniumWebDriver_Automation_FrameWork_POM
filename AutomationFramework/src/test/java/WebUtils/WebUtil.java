package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class WebUtil {

	private WebDriver driver;
	private ExtentTest et;

	public WebUtil(ExtentTest et) {
		this.et = et;
	}

	public WebDriver getDriver() {
		return driver;
	}

	// Browser Management Methods
	public WebDriver launchBrowser(String browserName, int timeInSeconds) {
		switch (browserName.toLowerCase()) {
		case "chrome":
			driver = new ChromeDriver();
			break;
		case "firefox":
			driver = new FirefoxDriver();
			break;
		case "edge":
			driver = new EdgeDriver();
			break;
		default:

			et.log(Status.FAIL, "Invalid browser name: " + browserName);
			throw new IllegalArgumentException("Invalid browser name: " + browserName);
		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeInSeconds));
		et.log(Status.INFO, browserName + " browser launched successfully");
		return driver;
	}

	/* ==========================Close Browser Method======================== */
	public void closeBrowser() {
		try {
			if (driver != null) {
				driver.quit();
				et.log(Status.INFO, "Browser session closed successfully.");
				System.out.println("Browser session closed successfully.");
			}
		} catch (WebDriverException e) {
			et.log(Status.FAIL, "WebDriverException while quitting driver: " + e.getMessage());
		} catch (Exception e) {
			et.log(Status.FAIL, "Unexpected exception while quitting driver: " + e.getMessage());
		}
	}

	/* ==========================Quit Browser Method======================== */

	public void quitBrowser() {
		try {
			if (driver != null) {
				driver.quit();
				et.log(Status.INFO, "Browser session quit successfully.");
			}
		} catch (WebDriverException e) {
			et.log(Status.FAIL, "WebDriverException while quitting browser: " + e.getMessage());
		} catch (Exception e) {
			et.log(Status.FAIL, "Unexpected exception while quitting browser: " + e.getMessage());
		}
	}

	/* ==========================Navigation Methods======================== */

	public String getPageTitle() {
		try {
			String title = driver.getTitle();
			et.log(Status.INFO, "Page title retrieved successfully : " + title);
			return title;

		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to retrieve page title: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public String getPageURL() {
		try {
			String url = driver.getCurrentUrl();
			et.log(Status.INFO, "Page URL retrieved successfully: " + url);
			return url;
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to retrieve page URL: " + e.getMessage());
			return null;
		}
	}

	public void openURL(String url) {
		try {
			driver.get(url);
			et.log(Status.INFO, "URL opened successfully: " + url);

		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to open URL: " + url + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/* ==========================Element Location Methods======================== */
	public WebElement searchElement(String xpath, String element) {
		WebElement we = null;
		try {
			we = driver.findElement(By.xpath(xpath));

			et.log(Status.PASS, element + " found successfully");
		} catch (NoSuchElementException e) {
			et.log(Status.WARNING, element + " not found on first attempt, retrying after wait...");
			System.out.println(element + " not found on first attempt, retrying...");
			try {
				Thread.sleep(5000);
				we = driver.findElement(By.xpath(xpath));
				et.log(Status.PASS, element + " found successfully after retry");
			} catch (NoSuchElementException e2) {
				et.log(Status.FAIL, element + " not found after retry");
				e2.printStackTrace();
				throw e2;
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} catch (InvalidSelectorException e) {
			et.log(Status.FAIL, element + " has invalid XPath syntax: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception while searching for " + element + ": " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return we;
	}

	/*
	 * ==========================Element Interaction Methods========================
	 */

	public void type(WebElement we, String value, String element) {
		try {

			we.sendKeys(value);
			et.log(Status.INFO, element + " entered '" + value + "' successfully");
		} catch (ElementNotInteractableException e) {
			try {
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("arguments[0].value='" + value + "';", we);
				et.log(Status.INFO, element + " entered '" + value + "' successfully by JavaScriptExecutor");
			} catch (Exception jsEx) {
				et.log(Status.FAIL, "JS typing failed on " + element + ". Error: " + jsEx.getMessage());
				jsEx.printStackTrace();
				throw jsEx;
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Typing failed on " + element + ". Error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public void click(WebElement we, String element) {
		try {
			we.click();
			et.log(Status.INFO, element + " clicked successfully");

		} catch (ElementClickInterceptedException e) {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.elementToBeClickable(we));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", we);
			we.click();
			et.log(Status.INFO, element + " clicked successfully");
		} catch (ElementNotInteractableException e) {
			try {
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("arguments[0].click();", we);
				et.log(Status.INFO, e.getMessage() + "\n" + element + " clicked successfully by JavaScript");
			} catch (Exception jsEx) {
				et.log(Status.FAIL, "JS click failed on " + element + ". Error: " + jsEx.getMessage());
				jsEx.printStackTrace();
				throw jsEx;
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Click failed on " + element + ". Error: " + e.getMessage());
			e.printStackTrace();

		}
	}

	/*
	 * ==========================JavaScript Methods========================
	 */

	public void jsClick(WebElement we, String element) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].click();", we);
			et.log(Status.INFO, element + " clicked successfully by JavaScriptExecutor");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to click " + element + " by JavaScriptExecutor. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void jsType(WebElement we, String value, String element) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].value=arguments[1];", we, value);
			et.log(Status.INFO, element + " typed successfully by JavaScript");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to type " + element + " by JavaScript. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void jsScrollToBottom() {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			et.log(Status.INFO, "Scrolled to the bottom of the page successfully.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to scroll to the bottom. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void jsScrollByAmount(int x, int y) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
			et.log(Status.INFO, "Scrolled by amount X: " + x + ", Y: " + y + " using JavaScript.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to scroll by amount X: " + x + ", Y: " + y + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void jsScrollToElement(WebElement we, String elementName) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].scrollIntoView(true);", we);
			et.log(Status.INFO, "Scrolled to element: " + elementName + " using JavaScript.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to scroll to element: " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * ==========================Select Dropdown Methods========================
	 */

	public void selectTextFromListBox(WebElement we, String selectText, String elementName) {
		try {
			Select select = new Select(we);
			select.selectByVisibleText(selectText);
			et.log(Status.INFO, elementName + " selected successfully with text: " + selectText);
		} catch (Exception e) {
			et.log(Status.FAIL,
					"Failed to select " + elementName + " with text: " + selectText + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void selectTextFromListBoxByIndex(WebElement we, int index, String elementName) {
		try {
			Select select = new Select(we);
			select.selectByIndex(index);
			et.log(Status.INFO, elementName + " selected successfully by index: " + index);
		} catch (Exception e) {
			et.log(Status.FAIL,
					"Failed to select " + elementName + " by index: " + index + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void selectTextByValue(WebElement we, String value) {
		try {
			Select select = new Select(we);
			select.selectByValue(value);
			et.log(Status.INFO, "Selected value '" + value + "' successfully.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to select value '" + value + "'. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * ==========================Actions Class Methods========================
	 */

	public void rightClick(WebElement we, String elementName) {
		try {
			Actions act = new Actions(driver);
			act.contextClick(we).build().perform();
			et.log(Status.PASS, "Right click performed successfully on element: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL,
					"Failed to perform right click on element: " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void doubleClick(WebElement we, String elementName) {
		try {
			Actions act = new Actions(driver);
			act.doubleClick(we).build().perform();
			et.log(Status.INFO, "Double clicked successfully on element: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to double click on element: " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void moveToElement(WebElement we, String elementName) {
		try {
			Actions act = new Actions(driver);
			act.moveToElement(we).build().perform();
			et.log(Status.PASS, "Hovered successfully on element: " + elementName);
			System.out.println(elementName + " hover successfuly");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to hover on element: " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void scrollByAmount(int x, int y) {
		try {
			Actions act = new Actions(driver);
			act.scrollByAmount(x, y).build().perform();
			et.log(Status.PASS, "Scrolled successfully by amount x: " + x + ", y: " + y);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to scroll by amount x: " + x + ", y: " + y + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void scrollToElement(WebElement we, String elementName) {
		try {
			Actions act = new Actions(driver);
			act.scrollToElement(we).build().perform();
			et.log(Status.INFO, "Scrolled successfully to element: " + elementName);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to scroll to element: " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * ==========================Text and Attribute Methods========================
	 */

	public String getInnerText(WebElement we, String elementName) {
		String text = null;
		try {
			text = we.getText();
			et.log(Status.INFO, elementName + " inner text found successfully: " + text);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to get inner text of element: " + elementName + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
		return text;
	}

	public String getAttributeValue(WebElement we, String attributeName, String elementName) {
		String attrValue = "";
		try {
			attrValue = we.getDomAttribute(attributeName);
			et.log(Status.INFO, elementName + " attribute '" + attributeName + "' found: " + attrValue);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to get attribute '" + attributeName + "' from " + elementName + ". Error: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return attrValue;
	}

	/*
	 * ==========================Multiple Elements Methods========================
	 */

	public List<String> getAllElementsText(String xpath) {
		List<String> elementTextList = new ArrayList<>();
		try {
			List<WebElement> elements = driver.findElements(By.xpath(xpath));

			if (elements.isEmpty()) {
				et.log(Status.WARNING, "No elements found for locator : " + xpath);
			} else {
				for (WebElement element : elements) {
					elementTextList.add(element.getText().trim());
				}
				et.log(Status.PASS, "Found " + elements.size() + " elements for XPath: " + xpath);
			}
		} catch (Exception e) {
			et.log(Status.FAIL,
					"Exception while getting elements text for XPath: " + xpath + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
		return elementTextList;
	}

	public void clickAllElements(String xpath) {
		List<WebElement> list = driver.findElements(By.xpath(xpath));
		for (WebElement we : list) {
			we.click();
		}
	}

	/*
	 * ========================== Window Handling Methods========================
	 */

	public void switchTowindowByUrl(String expectedURL) {
		Set<String> handleValues = driver.getWindowHandles();
		boolean isSwitched = false;

		for (String handleValue : handleValues) {
			driver.switchTo().window(handleValue);
			String currentWindowURL = driver.getCurrentUrl();

			if (currentWindowURL.equalsIgnoreCase(expectedURL)) {
				et.log(Status.INFO, "Switched to window with URL: " + expectedURL);
				isSwitched = true;
				break;
			}
		}

		if (!isSwitched) {
			et.log(Status.FAIL, "Failed to switch to window with URL: " + expectedURL);
		}
	}

	public void switchToWindowByTitle(String expectedTitle) {
		Set<String> handles = driver.getWindowHandles();
		boolean isSwitched = false;

		for (String handle : handles) {
			driver.switchTo().window(handle);
			String currentTitle = driver.getTitle();

			if (currentTitle.equalsIgnoreCase(expectedTitle)) {
				et.log(Status.INFO, "Switched to window with Title: " + expectedTitle);
				isSwitched = true;
				break;
			}
		}

		if (!isSwitched) {
			et.log(Status.FAIL, "Failed to switch to window with Title: " + expectedTitle);
		}
	}

	public void switchToWindowByIndex(int index) {
		Set<String> handles = driver.getWindowHandles();

		if (index < 0 || index >= handles.size()) {
			et.log(Status.FAIL, "Invalid window index: " + index);
			return;
		}

		String[] handlesArray = handles.toArray(new String[0]);
		driver.switchTo().window(handlesArray[index]);
		et.log(Status.INFO, "Switched to window at index: " + index);
	}

	public void closeAllChildWindowsAndSwitchToParent() {
		String parentHandle = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();

		for (String handle : handles) {
			if (!handle.equals(parentHandle)) {
				driver.switchTo().window(handle);
				driver.close();
				et.log(Status.INFO, "Closed child window: " + handle);
			}
		}

		driver.switchTo().window(parentHandle);
		et.log(Status.INFO, "Switched back to parent window.");
	}

	/*
	 * ========================== Frame Handling Methods ========================
	 */
	public void switchToFrameByIndex(int index) {
		try {
			driver.switchTo().frame(index);
			et.log(Status.INFO, "Switched to frame with index: " + index);
		} catch (NoSuchFrameException e) {
			et.log(Status.FAIL, "No frame found at index: " + index + ". Error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			et.log(Status.FAIL, "Error while switching to frame by index: " + index + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void switchToFrameByWebElement(WebElement weFrame) {
		try {
			driver.switchTo().frame(weFrame);
			et.log(Status.INFO, "Switched to frame successfully using WebElement.");
		} catch (NoSuchFrameException e) {
			et.log(Status.FAIL, "No such frame found using the provided WebElement. Error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			et.log(Status.FAIL, "Error while switching to frame using WebElement. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void switchToFrameByNameOrId(String nameOrId) {
		try {
			driver.switchTo().frame(nameOrId);
			et.log(Status.INFO, "Switched to frame: " + nameOrId);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to switch to frame: " + nameOrId + ". Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void switchToDefaultContent() {
		try {
			driver.switchTo().defaultContent();
			et.log(Status.INFO, "Switched back to default content");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to switch to default content. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * ========================== Wait Methods ========================
	 */
	public void staticWait(int timeInSecond) {
		try {
			Thread.sleep(timeInSecond * 1000);
			et.log(Status.INFO, "Static wait applied for " + timeInSecond + " seconds.");
		} catch (InterruptedException e) {
			et.log(Status.FAIL, "Static wait interrupted. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void implicityWait() {
		try {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
			et.log(Status.INFO, "Implicit wait of 60 seconds applied successfully.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to apply implicit wait. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void waitForVisibility(WebElement we, int timeouts) throws TimeoutException {
		try {
			WebDriverWait wt = new WebDriverWait(driver, Duration.ofSeconds(timeouts));
			wt.until(ExpectedConditions.visibilityOf(we));
			et.log(Status.INFO, "Element became visible within " + timeouts + " seconds.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Error during waitForVisibility: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void waitForEnabling(WebElement we, int timeouts) throws TimeoutException {
		try {
			WebDriverWait wt = new WebDriverWait(driver, Duration.ofSeconds(timeouts));
			wt.until(ExpectedConditions.elementToBeClickable(we));
			et.log(Status.INFO, "Element is enabled and clickable within " + timeouts + " seconds.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Error during waitForEnabling: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void waitForText(WebElement we, int timeouts) throws TimeoutException {
		try {
			WebDriverWait wt = new WebDriverWait(driver, Duration.ofSeconds(timeouts));
			wt.until(ExpectedConditions.textToBePresentInElement(we, "ReLead"));
			et.log(Status.INFO, "Text 'ReLead' is present in the element within " + timeouts + " seconds.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Error occurred in waitForText: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void waitForInvisibility(WebElement we, int timeouts) throws TimeoutException {
		try {
			WebDriverWait wt = new WebDriverWait(driver, Duration.ofSeconds(timeouts));
			wt.until(ExpectedConditions.invisibilityOf(we));
			et.log(Status.INFO, "Element became invisible within " + timeouts + " seconds.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Error occurred while waiting for invisibility: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void changePageLoadTimeout(int timeouts) {
		try {
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeouts));
			et.log(Status.INFO, "Page load timeout set to " + timeouts + " seconds successfully.");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to set page load timeout. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * ========================== Window Management Methods ========================
	 */

	public void maximizeWindow() {
		try {
			driver.manage().window().maximize();
			et.log(Status.INFO, "Browser window maximized successfully");
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to maximize browser window. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void setWindowSize(int width, int height) {
		try {
			Dimension dim = new Dimension(width, height);
			driver.manage().window().setSize(dim);
			et.log(Status.INFO, "Browser window size set to: Width = " + width + ", Height = " + height);
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to set browser window size. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * ========================== Element Status Methods ========================
	 */
	public boolean getElementDisplayStatus(WebElement we, String elementName) {
		boolean status = false;
		try {
			Dimension dim = we.getSize();
			if (dim.getHeight() > 0 && dim.getWidth() > 0) {
				status = true;
				et.log(Status.PASS, elementName + " is displayed with dimensions: " + dim);
			} else {
				et.log(Status.FAIL, elementName + " is not displayed properly (zero size).");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception while checking display status of " + elementName + ": " + e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public boolean getElementDisplayStatus1(WebElement we, String elementName) {
		boolean status = false;
		try {
			Dimension dim = we.getSize();
			if (dim.getHeight() > 0 && dim.getWidth() > 0) {
				status = true;
				et.log(Status.PASS, elementName + " is displayed with size: " + dim);
				System.out.println(elementName + " is visible with dimensions: " + dim);
			} else {
				et.log(Status.FAIL, elementName + " is not displayed (zero size).");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Error checking display status of " + elementName + ": " + e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	/*
	 * ========================== Validation Methods ========================
	 */
	public void validateInnerText(WebElement we, String expectedText, String elementName) {
		try {
			String actualText = we.getText();
			if (actualText.equalsIgnoreCase(expectedText)) {
				et.log(Status.PASS, "Validation passed for " + elementName + ". Actual: '" + actualText
						+ "', Expected: '" + expectedText + "'");

			} else {
				et.log(Status.FAIL, "Validation failed for " + elementName + ". Actual: '" + actualText
						+ "', Expected: '" + expectedText + "'");

			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception while validating text for " + elementName + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void validateAttribute(WebElement we, String expectedAttribute, String attributeName, String elementName) {
		try {
			String actualAttribute = we.getDomAttribute(attributeName);

			if (actualAttribute != null && actualAttribute.equalsIgnoreCase(expectedAttribute)) {
				et.log(Status.PASS, "Validation passed for attribute '" + attributeName + "' of " + elementName
						+ ". Actual: '" + actualAttribute + "', Expected: '" + expectedAttribute + "'");

			} else {
				et.log(Status.FAIL, "Validation failed for attribute '" + attributeName + "' of " + elementName
						+ ". Actual: '" + actualAttribute + "', Expected: '" + expectedAttribute + "'");

			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception while validating attribute '" + attributeName + "' for " + elementName + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void ElementIsVisible(WebElement we, String elementName) {
		try {
			boolean actualStatus = we.isDisplayed();
			if (actualStatus) {
				et.log(Status.PASS, elementName + " is visible on the page.");
			} else {
				et.log(Status.FAIL, elementName + " is NOT visible on the page.");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception while verifying visibility of " + elementName + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void validateElementIsInVisible(WebElement we) {
		boolean actualStetus = we.isDisplayed();
		if (actualStetus == true) {
			et.log(Status.PASS, actualStetus + " is visible on the page.");
		} else {
			et.log(Status.FAIL, actualStetus + " is NOT visible on the page.");
		}
	}

	public void validateElementIsEnabled(WebElement we) {
		boolean actualStetus = we.isEnabled();
		if (actualStetus == true) {
			et.log(Status.PASS, "passed. actual" + actualStetus + "&& expected- true");
		} else {
			et.log(Status.FAIL, "faild. actual" + actualStetus + "&& expected- true");
		}
	}

	public void validateElementIsDisabled(WebElement we) {
		boolean actualStetus = we.isEnabled();
		if (actualStetus == false) {
			et.log(Status.PASS, "passed. actual" + actualStetus + "&& expected- true");
		} else {
			et.log(Status.FAIL, "faild. actual" + actualStetus + "&& expected- true");
		}
	}

	public void validatePageTitle(String expectedTitle) {
		try {
			String actualTitle = driver.getTitle();
			if (actualTitle.equalsIgnoreCase(expectedTitle)) {
				et.log(Status.PASS, "Page title validation passed. Actual: '" + actualTitle + "' | Expected: '"
						+ expectedTitle + "'");
			} else {
				et.log(Status.FAIL, "Page title validation failed. Actual: '" + actualTitle + "' | Expected: '"
						+ expectedTitle + "'");
			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception during page title validation: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void validateDropDownSelectedText(WebElement we, String expectedSelectedText) {
		try {
			Select select = new Select(we);
			String actualSelectedText = select.getFirstSelectedOption().getText();

			if (actualSelectedText.equalsIgnoreCase(expectedSelectedText)) {
				et.log(Status.PASS, "Dropdown selected text validation passed. Actual: '" + actualSelectedText
						+ "' | Expected: '" + expectedSelectedText + "'");

			} else {
				et.log(Status.FAIL, "Dropdown selected text validation failed. Actual: '" + actualSelectedText
						+ "' | Expected: '" + expectedSelectedText + "'");

			}
		} catch (Exception e) {
			et.log(Status.FAIL, "Exception during dropdown selected text validation: " + e.getMessage());

			e.printStackTrace();
		}
	}

	/*
	 * ========================== Alert Handling Methods ========================
	 */

	public void alertAccept() {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			et.log(Status.INFO, "Alert text: " + alertText);
			alert.accept();
			et.log(Status.PASS, "Alert accepted successfully");
		} catch (NoAlertPresentException e) {
			et.log(Status.FAIL, "No alert present to accept. Error: " + e.getMessage());
		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to accept alert. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void alertDismiss() {
		try {
			driver.switchTo().alert().dismiss();
			et.log(Status.PASS, "Alert dismissed successfully");
		} catch (NoAlertPresentException e) {
			et.log(Status.FAIL, "No alert present to dismiss. Error: " + e.getMessage());

		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to dismiss alert. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String getAlertText() {
		String alertText = "";
		try {
			Alert alert = driver.switchTo().alert();
			alertText = alert.getText();
			et.log(Status.INFO, "Alert text: " + alertText);

		} catch (NoAlertPresentException e) {
			Alert alert = driver.switchTo().alert();
			alertText = alert.getText();
			et.log(Status.FAIL, "No alert present to getting text. Error: " + e.getMessage());

		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to getText alert. Error: " + e.getMessage());

			e.printStackTrace();
		}
		return alertText;
	}

	public void alertSendKey(String sendText) {
		try {
			Alert alert = driver.switchTo().alert();
			alert.sendKeys(sendText);
			et.log(Status.INFO, "Text sent to alert successfully: " + sendText);
		} catch (NoAlertPresentException e) {
			et.log(Status.FAIL, "No alert present to send keys. Error: " + e.getMessage());

		} catch (Exception e) {
			et.log(Status.FAIL, "Failed to send keys to alert. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * ========================== Utility Methods ========================
	 */

	public void takeScreenShot(String screenshotName) {
		try {
			TakesScreenshot ts = (TakesScreenshot) driver;
			File sourceFile = ts.getScreenshotAs(OutputType.FILE);

			String filePath = "ScreenShot/" + screenshotName + ".png";
			File targetLocation = new File(filePath);

			Files.copy(sourceFile.toPath(), targetLocation.toPath());

			et.log(Status.INFO, "Screenshot taken: " + filePath);
		} catch (IOException e) {
			et.log(Status.FAIL, "Failed to save screenshot. Error: " + e.getMessage());
		}
	}

// Reading Data From Excel Sheet
	public static int getRowNumberByTestCaseID(String excelPath, String sheetName, String testCaseID) {

		int rowNumber = -1;

		try {

			FileInputStream fis = new FileInputStream(excelPath);
			Workbook workbook = WorkbookFactory.create(fis); // xls + xlsx
			Sheet sheet = workbook.getSheet(sheetName);

			int lastRow = sheet.getLastRowNum();

			for (int i = 1; i <= lastRow; i++) { // start from 1 (skip header)

				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				String tcIDFromExcel = cell.getStringCellValue();

				if (tcIDFromExcel.equalsIgnoreCase(testCaseID)) {
					rowNumber = i;
					break;
				}
			}

			workbook.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowNumber;
	}

	public static Map<String, String> readDataAsKeyValue(String excelPath, String sheetName, String testCaseID) {

		Map<String, String> dataMap = new HashMap<>();

		try {
			FileInputStream fis = new FileInputStream(excelPath);
			Workbook workbook = WorkbookFactory.create(fis); // xls + xlsx
			Sheet sheet = workbook.getSheet(sheetName);

			Row headerRow = sheet.getRow(0);
			int lastRow = sheet.getLastRowNum();
			int targetRow = -1;

			// Find row by TestCaseID (assume column 0)
			for (int i = 1; i <= lastRow; i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				Cell tcCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (tcCell.getStringCellValue().equalsIgnoreCase(testCaseID)) {
					targetRow = i;
					break;
				}
			}

			// TestCaseID not found
			if (targetRow == -1) {
				workbook.close();
				fis.close();
				return dataMap;
			}

			Row dataRow = sheet.getRow(targetRow);
			int cellCount = headerRow.getLastCellNum();

			// Header = key, Row data = value
			for (int i = 0; i < cellCount; i++) {
				Cell cellkeyObj = headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				String key = cellkeyObj.getStringCellValue();
				Cell CellvalueObj = dataRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				String value = CellvalueObj.getStringCellValue();
				dataMap.put(key, value);
			}

			workbook.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataMap;
	}

	public void brokenLink() {
		List<WebElement> links = driver.findElements(By.tagName("a"));
		System.out.println("Total links found: " + links.size());

		for (WebElement link : links) {
			String url = link.getDomAttribute("href");

			if (url == null || url.isEmpty()) {
				et.log(Status.INFO, "URL is either null or empty for link: " + link.getText());
				continue;
			}

			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setRequestMethod("HEAD");
				connection.connect();

				int responseCode = connection.getResponseCode();

				if (responseCode >= 400) {
					et.log(Status.INFO, url + " is a broken link. Response code: " + responseCode);

				} else {

					et.log(Status.PASS, url + " is a valid link.");
				}

			} catch (Exception e) {
				et.log(Status.FAIL, "Exception while checking URL: " + url + " Error: " + e.getMessage());
			}
		}
	}
}
