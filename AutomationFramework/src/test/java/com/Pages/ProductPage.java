package com.Pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.support.PageFactory;

import com.ORLayer.SearchProduct;

import WebUtils.WebUtil;

public class ProductPage extends SearchProduct {
	private WebUtil wbe;

	public ProductPage(WebUtil we) {
		super(we);
		this.wbe = we;

	}

	public void ProductAm(String Value) {
		wbe.click(getSearchBoxAm(), "Search Box");
		wbe.type(getSearchBoxAm(), Value, "Search Box");
		getSearchBoxAm().sendKeys(Keys.ENTER);

		String str = wbe.getInnerText(getAmProduct(), "Price");
		System.out.println(str);

	}
}
