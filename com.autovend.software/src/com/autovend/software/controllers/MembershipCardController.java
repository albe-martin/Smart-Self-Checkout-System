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

package com.autovend.software.controllers;

import java.util.Scanner;

import com.autovend.IllegalDigitException;
import com.autovend.MembershipCard;

public class MembershipCardController {
	private String membershipNumber;
	private boolean isActive = false;
	// Did a max tries of 3, having a limit would help with like not having a
	// infinite input that is invalid,
	// and after the three invalid attempts it will return null
	private static final int MAX_TRIES = 3;

	public boolean getIsActive() {
		return this.isActive;
	}

	/*
	 * The isValid method will first check if memberNUM is null or if its length is
	 * not equal to 12. If any of those conditions are true then it will through the
	 * exception "IllegalDigitException" and say that it needs to be exactly 12
	 * digits long. Then it will use the Character.isDigit to check each digit in
	 * memberNum and see if there are any non-digits and that it is a digit between
	 * 0-9. If there is a non-digit it will throw the "IllegalDigitException" saying
	 * that it should be a digit between 0-9.
	 */

	public static boolean isValid(String memberNum) throws IllegalDigitException {
		if (memberNum == null || memberNum.length() != 12) {
			throw new IllegalDigitException("The Membership number should be exactly 12 digits long.");
		}
		for (int i = 0; i < memberNum.length(); i++) {
			char c = memberNum.charAt(i);
			if (!Character.isDigit(c)) {
				throw new IllegalDigitException("The Membership number should only contain digits between 0-9.");
			}
		}
		return true;
	}

	/*
	 * The getValidMembershipNumber method prompts the user to enter a Membership
	 * number and checks whether the input is valid. If the input is valid (only
	 * digits between 0-9 and is exactly 12 digits long), the method returns the
	 * Membership number. If the input is invalid, the method prompts the user to
	 * try again or continue without a Membership number, up to a maximum number of
	 * tries (MAX_TRIES). If the user exceeds the maximum number of tries without
	 * entering a valid Membership number, the method returns null.
	 */

	public static String getValidMembershipNumber(Scanner scan) throws IllegalDigitException {
		int numTries = 0;
		String memberNum = null;
		while (numTries < MAX_TRIES) {
			System.out.println("Enter your Membership number: ");
			memberNum = scan.nextLine();
			try {
				if (isValid(memberNum)) {
					return memberNum;
				}
			} catch (IllegalDigitException e) {
				System.out.println(e.getMessage());
			}
			numTries++;
			if (numTries < MAX_TRIES) {
				System.out.println(
						"Invalid membership number. Please try again or enter 'yes' to continue without a Membership number.");
			}
		}
		return null;
	}

	/*
	 * The updateMembershipStatus method prompts the user to enter whether they have
	 * a membership or not. "yes" would call the getValidMembershipNumber method to
	 * retrieve and validate the membership number. If it is valid the Membership
	 * Card object is created with the given membership number and isActive would be
	 * set to true. If the membership number is invalid the user will be informed
	 * that they will be enrolled without a membership number. If the user responds
	 * with "no," they are prompted again to confirm whether they want to continue
	 * without a membership number. If the user responds with "no," the same process
	 * as described above for the "yes" response is followed. If the user responds
	 * with any other input, they are informed that their input is invalid and the
	 * method returns. If the user responds with "cancel," the method prints a
	 * message indicating that the membership enrollment has been cancelled and
	 * returns. Finally, the method prints out the membership status and the
	 * information about the membership card. If an IllegalDigitException is caught
	 * during the execution of the method, the user is informed of the error and
	 * prompted to try again. The method is then called recursively to repeat the
	 * process.
	 */

	@SuppressWarnings("resource")
	public void updateMembershipStatus() {
		Scanner scan = new Scanner(System.in);
		MembershipCard mc = new MembershipCard("Membership Card", "0000", "XYZ", false);
		System.out.println("Do you have a Membership number? (yes or no or cancel)");
		String response1 = scan.nextLine();
		if (response1.equalsIgnoreCase("yes")) {
			membershipNumber = getValidMembershipNumber(scan);
			if (membershipNumber != null) {
				mc = new MembershipCard("Membership Card", membershipNumber, "Regular Shopper", false);
				isActive = true;
			} else {
				System.out.println("You will be enrolled without a membership number.");
			}
		} else if (response1.equalsIgnoreCase("no")) {
			System.out.println("Do you want to continue without a Membership number? (yes or no)");
			String response2 = scan.nextLine();
			if (!response2.equalsIgnoreCase("no") && !response2.equalsIgnoreCase("yes")) {
				System.out.println("Invalid input. Please enter 'yes' or 'no'.");
				return;
			} else if (response2.equalsIgnoreCase("no")) {
				membershipNumber = getValidMembershipNumber(scan);
				if (membershipNumber != null) {
					mc = new MembershipCard("Membership Card", membershipNumber, "Regular Shopper", false);
					isActive = true;
				} else {
					System.out.println("You will be enrolled without a membership number.");
				}
			}
		} else if (response1.equalsIgnoreCase("cancel")) {
			System.out.println("Membership enrollment has been cancelled.");
			return;
		}
		System.out.println("Membership status: " + isActive);
		System.out.println("Membership card information: " + mc.toString());
	}

}
