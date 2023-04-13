package com.autovend.software.utils;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;

import java.math.BigDecimal;
import java.util.Map;

/**
 * A simple class used to represent a database of miscellaneous products
 * like reusable bags and whatnot.
 */
public class MiscProductsDatabase {
    public static Numeral[] bagNumb = BarcodeUtils.stringToNumeralArray("000000000000");

    public static class Bag extends BarcodedProduct {
        private double expectedWeight = 5.0;
        //average plastic bag is 8 grams so this checks out
        /**
         * Create a product instance.
         *
         * @param price     The price per unit or per kilogram.
         */
        public Bag(BigDecimal price) {
            super(new Barcode(bagNumb), "A reusable bag", price, 0.5);
        }
        public double getExpectedWeight() {
            return expectedWeight;
        }

    }
    private MiscProductsDatabase(){}

    /**
     * Just a simple example of a database used for misc items
     */
    public static final Map<Numeral[], Product> MISC_DATABASE = Map.of(
        bagNumb,new Bag(BigDecimal.valueOf(0.5))
    );
}
