/*
SENG 300 Project Iteration 2
Group 7
Niran Malla 30086877
Saksham Puri 30140617
Fatema Chowdhury 30141268
Janet Tesgazeab 30141335
Fabiha Fairuzz Subha 30148674
Ryan Janiszewski 30148838
Umesh Oad 30152293
Manvi Juneja 30153525
Daniel Boettcher 30153811
Zainab Bari 30154224
Arie Goud 30163410
Amasil Rahim Zihad 30164830
*/

package com.autovend.software.pojo;

import java.math.BigDecimal;

public class CartLineItem {
	public enum CODETYPE {
		BARCODE, PLU
	}

	private String productCode;
	private CODETYPE codeType;
	private BigDecimal price;
	private boolean isPerUnit;
	private String description;
	private double expectedWeight;
	private double quantity;
	private double lineTotalPrice;

	public CartLineItem(String productCode, CODETYPE codeType, BigDecimal price, boolean isPerUnit, String description,
			double expectedWeight, double quantity) {
		this.productCode = productCode;
		this.codeType = codeType;
		this.price = price;
		this.isPerUnit = isPerUnit;
		this.description = description;
		this.expectedWeight = expectedWeight;
		this.quantity = quantity;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public CODETYPE getCodeType() {
		return codeType;
	}

	public void setCodeType(CODETYPE codeType) {
		this.codeType = codeType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
		recalculate();
	}

	public boolean isPerUnit() {
		return isPerUnit;
	}

	public String getDescription() {
		return description;
	}

	public double getExpectedWeight() {
		return expectedWeight;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
		recalculate();
	}

	public double getLineTotalPrice() {
		return lineTotalPrice;
	}

	private void recalculate() {
		this.lineTotalPrice = this.price.doubleValue() * quantity;
	}
}
