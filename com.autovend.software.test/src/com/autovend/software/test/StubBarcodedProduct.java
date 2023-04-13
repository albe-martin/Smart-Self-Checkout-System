/*
 * SENG 300 Project Iteration 3 - Group P3-2
 * Braedon Haensel -         UCID: 30144363
 * Umar Ahmed -             UCID: 30145076
 * Bartu Okan -             UCID: 30150180
 * Arie Goud -                 UCID: 30163410
 * Abdul Biderkab -         UCID: 30156693
 * Hamza Khan -             UCID: 30157097
 * James Hayward -             UCID: 30149513
 * Christian Salvador -     UCID: 30089672
 * Fatema Chowdhury -         UCID: 30141268
 * Sankalp Bartwal -         UCID: 30132025
 * Avani Sharma -             UCID: 30125040
 * Albe Martin -             UCID: 30161964 
 * Omar Khan -                 UCID: 30143707
 * Samantha Liu -             UCID: 30123255
 * Alex Chen -                 UCID: 30140184
 * Auric Adubofour-Poku -     UCID: 30143774
 * Grant Tkachyk -             UCID: 30077137
 * Amandeep Kaur -             UCID: 30153923
 * Tashi Labowka-Poulin -     UCID: 30140749
 * Daniel Chang -             UCID: 30110252
 * Jacob Braun -             UCID: 30124507
 * Omar Ragab -             UCID: 30148549
 * Artemy Gavrilov -         UCID: 30143698
 * Colton Gowans -             UCID: 30143979
 * Hada Rahadhi Hafiyyan -     UCID: 30186484
 * 
 */

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
