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

package com.autovend.software.utils;

import java.util.ArrayList;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;

public class BarcodeUtils {
	 private static Numeral[] stringToNumeralArray(String input) {
	        char[] chars = input.toCharArray();
	        ArrayList<Numeral> numerals = new ArrayList<Numeral>();
	        for (char c: chars) {
	            numerals.add(Numeral.valueOf((byte)Integer.parseInt(String.valueOf(c))));
	        }
	        return numerals.toArray(new Numeral[0]);
	    }

	    public static Barcode stringBarcodeToBarcode(String input) {
	        Numeral[] numerals = stringToNumeralArray(input);
	        return new Barcode(numerals);
	    }

	    public static PriceLookUpCode stringPLUToPLU(String input) {
	        Numeral[] numerals = stringToNumeralArray(input);
	        return new PriceLookUpCode(numerals);
	    }
}
