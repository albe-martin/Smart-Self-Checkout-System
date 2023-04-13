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
