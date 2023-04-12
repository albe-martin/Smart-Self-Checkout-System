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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddItemPLU.class, AddItemTest.class,AddingOwnBagsTest.class, AttendantGUITest.class,
	AttendantIOTest.class, BillDispenserControllerTest.class, BillPaymentControllerTest.class, 
	BillDispenserControllerTest.class, CardPaymentTest.class, CoinDispenserControllerTest.class,
	CoinPaymentTest.class, LoginLogoutTest.class, LowInkPaperTest.class, MembershipTest.class,
	PurchaseBagsTest.class, RemoveItemTest.class, StartupShutdownStationTest.class,
	TestPrintReceipt.class})
public class AllTests {
}
