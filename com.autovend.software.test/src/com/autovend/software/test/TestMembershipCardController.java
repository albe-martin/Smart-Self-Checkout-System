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

package com.autovend.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.IllegalDigitException;

@SuppressWarnings("unused")

public class TestMembershipCardController {

	@Before
	public void setup() {
	}

	MembershipCardController mcc = new MembershipCardController();
	Scanner scanner = new Scanner(System.in);
	InputStreamReader inputReader;

	@After
	public void teardown() {
	}

	@Test
	public void testIsValidNullValue() throws IllegalDigitException {
		String expectedMessage = "The Membership number should be exactly 12 digits long.";
		Exception exception = assertThrows(IllegalDigitException.class, () -> MembershipCardController.isValid(null));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testIsValidLessDigits() throws IllegalDigitException {
		String expectedMessage = "The Membership number should be exactly 12 digits long.";
		Exception exception = assertThrows(IllegalDigitException.class,
				() -> MembershipCardController.isValid("123456"));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testIsValidNan() throws IllegalDigitException {
		String expectedMessage = "The Membership number should only contain digits between 0-9.";
		Exception exception = assertThrows(IllegalDigitException.class,
				() -> MembershipCardController.isValid("abc234567890"));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testIsValidReturnsTrue() {
		boolean expected = true;
		boolean actual = MembershipCardController.isValid("564823890124");
		assertEquals(expected, actual);
	}

	@Test
	public void testGetValidMembershipNumberValidInput() {
		String input = "564823890124";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in); // create a new Scanner object
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner); // pass the Scanner object
																							// as an argument
		assertEquals(input, expectedOutput);
	}

	@Test
	public void testGetValidMembershipNumber_ValidOnFirstTry() {
		String input = "564823890124\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in); // create a new Scanner object
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner); // pass the Scanner object
																							// as an argument
		assertEquals(input.trim(), expectedOutput);
	}

	@Test
	public void testGetValidMembershipNumber_ValidOnSecondTry() {
		String input = "123456\n564823890124\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in); // create a new Scanner object
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner); // pass the Scanner object
																							// as an argument
		assertEquals("564823890124", expectedOutput);
	}

	@Test
	public void testGetValidMembershipNumber_InvalidAndCancel() {
		String input = "123456\n789012\n345678\ncancel\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in); // create a new Scanner object
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner); // pass the Scanner object
																							// as an argument
		assertNull(expectedOutput);
	}

	@Test
	public void testGetValidMembershipNumber_InvalidAndValidInput() {
		String input = "123456\n564823890124\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in);
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner);
		assertEquals("564823890124", expectedOutput);
	}

	@Test
	public void testGetValidMembershipNumber_ValidInput() throws IllegalDigitException {
		String input = "564823890124\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in); // create a new Scanner object
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner); // pass the Scanner object
																							// as an argument
		assertTrue(MembershipCardController.isValid(expectedOutput)); // check if the membership number is valid
	}

	@Test
	public void testGetValidMembershipNumber_ValidInputButNotValidated() {
		String input = "123456789012";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in);
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner);
		assertEquals(input, expectedOutput);
	}

	@Test
	public void testGetValidMembershipNumber_MaxTriesReached() {
		String input = "123456\n789012\n345678\n901234\n567890\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		Scanner scanner = new Scanner(System.in);
		String expectedOutput = MembershipCardController.getValidMembershipNumber(scanner);
		assertNull(expectedOutput);
	}

	@Test
	public void testGetValidMembershipNumberReturnsValidNumber() throws IllegalDigitException {
		String expected = "123456789012";
		InputStream in = new ByteArrayInputStream(expected.getBytes());
		Scanner scanner = new Scanner(in);
		String actual = MembershipCardController.getValidMembershipNumber(scanner);
		assertEquals(expected, actual);
		assertTrue(MembershipCardController.isValid(actual));
	}

	@Test
	public void testUpdateMembershipStatus_WithValidMember() {
		String input = "yes\n564823890124\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertTrue(mcc.getIsActive());
	}

	@Test
	public void testUpdateMembershipStatus_WithoutdMember() {
		String input = "no\nyes\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertFalse(mcc.getIsActive());
	}

	/*
	 * Tests the class UpdateMembership when the customer says 'yes' to enroll
	 * without membership, and says 'no' when asked if they want to continue without
	 * a membership no and then finally provides a valid membership no.
	 */
	@Test
	public void testUpdateMembershipStatus_AfterTwoInputs() {
		String input = "no\nno\n564823890124\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertTrue(mcc.getIsActive());
	}

	@Test
	public void testUpdateMembershipStatus_AfterYesMaxOutTries() {
		String input = "yes\n1234\n564823890\n56294\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertFalse(mcc.getIsActive());
	}

	@Test
	public void testUpdateMembershipStatus_AfterNoMaxOutTries() {
		String input = "no\nno\n1234\n564823890\n56294\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertFalse(mcc.getIsActive());
	}

	@Test
	public void testUpdateMembershipStatus_Cancel() {
		String input = "cancel\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertFalse(mcc.getIsActive());
	}

	@Test
	public void testUpdateMembershipStatus_InvalidInput() {
		String input = "no\ninvalid\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertFalse(mcc.getIsActive());
	}

	@Test
	public void testUpdateMembershipStatus_IllegalDigitException() {
		String input = "1234567a89\nno\n";
		InputStream sysInBackup = System.in;
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		mcc.updateMembershipStatus();
		assertFalse(mcc.getIsActive());
	}

}
