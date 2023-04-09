package com.autovend.software.test;

import java.math.BigDecimal;

import com.autovend.Barcode;
import com.autovend.devices.SimulationException;
import com.autovend.products.Product;

public class StubBarcodedProduct extends Product{
	private Barcode barcode;
	private String description;
	private double expectedWeight;

	public StubBarcodedProduct(Barcode barcode, String description, BigDecimal price, double expectedWeight, boolean isPerUnit) {
		super(price, isPerUnit);

		if(barcode == null)
			throw new SimulationException(new NullPointerException("barcode is null"));

		if(description == null)
			throw new SimulationException(new NullPointerException("description is null"));

		this.barcode = barcode;
		this.description = description;
		this.expectedWeight = expectedWeight;
	}

	/**
	 * Get the barcode.
	 * 
	 * @return The barcode. Cannot be null.
	 */
	public Barcode getBarcode() {
		return barcode;
	}

	/**
	 * Get the description.
	 * 
	 * @return The description. Cannot be null.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the expected weight of one unit of the product.
	 * 
	 * @return The expected weight of one unit of the product.
	 */
	public double getExpectedWeight() {
		return expectedWeight;
	}
	

}
