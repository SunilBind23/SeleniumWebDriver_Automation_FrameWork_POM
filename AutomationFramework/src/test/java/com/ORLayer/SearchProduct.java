package com.ORLayer;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import WebUtils.WebUtil;

public class SearchProduct {
	
	public SearchProduct(WebUtil we) {
				PageFactory.initElements(we.getDriver(), this);
	}

	// amazone search id
	@FindBy(id = "twotabsearchtextbox")
	private WebElement searchBoxAm;

	@FindBy(xpath = "//span[@class='a-price-whole']")
	private WebElement amProduct;

	public WebElement getSearchBoxAm() {
		return searchBoxAm;
	}

	public WebElement getAmProduct() {
		return amProduct;
	}

	// flipcart search

	@FindBy(name = "q")
	private WebElement searchBoxFC;

	@FindBy(xpath = "//div[@class='Nx9bqj _4b5DiR']")
	private WebElement FCProduct;

	public WebElement getSearchBoxFC() {
		return searchBoxFC;
	}

	public WebElement getFCProduct() {
		return FCProduct;
	}

}
