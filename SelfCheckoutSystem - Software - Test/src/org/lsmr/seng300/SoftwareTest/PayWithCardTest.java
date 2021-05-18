package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;

import CODE.PayWithCard;

public class PayWithCardTest {
	
	Card debitCard;
	Card creditCard;
	Card notIssuedDebit;
	Card notIssuedCredit;
	Card giftCard;
	Card notIssuedGiftCard;
	
	CardIssuer Bank; 
	CardIssuer Coop;
	
	PayWithCard payWithCard;
	
	
	/**
	 * Initializes all cards and sets them in bank
	 */
	@Before
	public void setup() {
		debitCard = new Card("debit", "6750094156347755", "Billy", "056", "1234", true, true);
		creditCard = new Card("credit", "6750094154567755", "Billy", "043", "9876", true, true);
		notIssuedDebit = new Card("debit", "1928374654783829", "Billy", "077", "1234", true, true);
		notIssuedCredit = new Card("credit", "1928374654783829", "Billy", "077", "1234", true, true);
		giftCard = new Card("gift card", "6750056347755", "Andrew", null, null, false, false);
		notIssuedGiftCard = new Card("gift card", "6750056347755678", "Bill", null, null, false, false);
		
		payWithCard = new PayWithCard();
		Bank = new CardIssuer("TD Canada");
		
		Coop = new CardIssuer("Coop");
		
		Calendar expiry = Calendar.getInstance();
		expiry.add(Calendar.MONTH, 2);
		expiry.add(Calendar.YEAR, 2);
		
		Bank.addCardData("6750094156347755", "Billy", expiry, "056", new BigDecimal(2500));
		Bank.addCardData("6750094154567755", "Billy", expiry, "043", new BigDecimal(500));
		
		Coop.addCardData("6750056347755", "Andrew", expiry, "000", new BigDecimal(100));
	}
	
	//Checks that null gift card throws exception
	@Test
	public void NullGiftCard() {
		try {
			payWithCard.PayWithGiftCard(null, new BigDecimal(5), Bank);
			Assert.fail("Expected exception: Debit Card is null.");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	//Tests a valid gift card can be used to make payment
	@Test
	public void ValidGiftCard() {
		
		Assert.assertTrue(payWithCard.PayWithGiftCard(giftCard, new BigDecimal(5), Coop));
	
	}
	
	//Checks that a credit card can not be used for pay with gift card
	@Test
	public void NotGiftCard() {
		try {
			payWithCard.PayWithGiftCard(creditCard, new BigDecimal(5), Bank);
			Assert.fail("Expected exception: Not a gift card");
		}
		catch (Exception e){
			Assert.assertTrue("Not a gift card", e instanceof SimulationException);
		}
	}
	
	//Checks that a gift card not issued cannot by used to pay
	@Test
	public void NotIssuedGiftCard() {
		try {
			payWithCard.PayWithGiftCard(notIssuedGiftCard, new BigDecimal(5), Coop);
			Assert.fail("Coop Rejected Gift Card");
		}
		catch (Exception e){
			Assert.assertTrue("Coop Rejected Gift Card", e instanceof SimulationException);
		}
	}
	
	//Checks that a gift card cannot pay an amount that exceeds its funds
	@Test
	public void InsufficientFundsGiftCard() {
		try {
			payWithCard.PayWithGiftCard(giftCard, new BigDecimal(200), Coop);
			Assert.fail("Coop Rejected Gift Card");
		}
		catch (Exception e){
			Assert.assertTrue("Coop Rejected Gift Card", e instanceof SimulationException);
		}
	}
	
	
	
	//Checks that null debit card throws exception
	@Test
	public void NullDebitCard() {
		try {
			payWithCard.PayWithDebitCard(null, 0, new BigDecimal(5), "0000", Bank);
			Assert.fail("Expected exception: Debit Card is null.");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	//Checks that a valid debit card can be tapped
	@Test
	public void TapDebitCard() {
		
		Assert.assertTrue(payWithCard.PayWithDebitCard(debitCard, 0, new BigDecimal(5), "1234", Bank));
	
	}
	
	//Checks that a valid debit card can be inserted
	@Test
	public void InsertDebitCard() {
		
		Assert.assertTrue(payWithCard.PayWithDebitCard(debitCard, 1, new BigDecimal(5), "1234", Bank));
	
	}
	
	//Checks that a valid debit card can be swiped
	@Test
	public void SwipeDebitCard() {
		
		Assert.assertTrue(payWithCard.PayWithDebitCard(debitCard, 2, new BigDecimal(5), "1234", Bank));
	
	}
	
	//Checks that a credit card can not be used for pay with debit
	@Test
	public void NotDebitCard() {
		try {
			payWithCard.PayWithDebitCard(creditCard, 0, new BigDecimal(5), "1234", Bank);
			Assert.fail("Expected exception: Not a debit card");
		}
		catch (Exception e){
			Assert.assertTrue("Not a debit card", e instanceof SimulationException);
		}
	}
	
	//Checks that a debit card with incorrect pin throws rejection when inserted
	@Test
	public void InvalidPinDebitCard() {
		try {
			payWithCard.PayWithDebitCard(debitCard, 1, new BigDecimal(5), "1232", Bank);
			Assert.fail("Invalid Pin");
		}
		catch (Exception e){
			Assert.assertTrue("Invalid Pin", e instanceof SimulationException);
		}
	}
	
	//Checks that a debit card not issued cannot by used to pay
	@Test
	public void NotIssuedDebitCard() {
		try {
			payWithCard.PayWithDebitCard(notIssuedDebit, 1, new BigDecimal(5), "1234", Bank);
			Assert.fail("Bank Rejected Card");
		}
		catch (Exception e){
			Assert.assertTrue("Bank Rejected Card", e instanceof SimulationException);
		}
	}
	
	//Checks that a debit card cannot pay an amount that exceeds its funds
	@Test
	public void InsufficientFundsDebitCard() {
		try {
			payWithCard.PayWithDebitCard(debitCard, 1, new BigDecimal(3000), "1234", Bank);
			Assert.fail("Bank Rejected Card");
		}
		catch (Exception e){
			Assert.assertTrue("Bank Rejected Card", e instanceof SimulationException);
		}
	}
	
	//Checks that null credit card throws exception
	@Test
	public void NullCreditCard() {
		try {
			payWithCard.PayWithCreditCard(null, 0, new BigDecimal(5), "9876", Bank);
			Assert.fail("Expected exception: Debit Card is null.");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	//Checks that a valid credit card can be tapped
	@Test
	public void TapCreditCard() {
		
		Assert.assertTrue(payWithCard.PayWithCreditCard(creditCard, 0, new BigDecimal(5), "9876", Bank));
	
	}
	
	//Checks that a valid credit card can be inserted
	@Test
	public void InsertCreditCard() {
		
		Assert.assertTrue(payWithCard.PayWithCreditCard(creditCard, 1, new BigDecimal(5), "9876", Bank));
	
	}
	
	//Checks that a valid credit card can be swiped
	@Test
	public void SwipeCreditCard() {
		
		Assert.assertTrue(payWithCard.PayWithCreditCard(creditCard, 2, new BigDecimal(5), "9876", Bank));
	
	}
	
	//Checks that a debit card can not be used for pay with credit
	@Test
	public void NotCreditCard() {
		try {
			payWithCard.PayWithCreditCard(debitCard, 0, new BigDecimal(5), "9876", Bank);
			Assert.fail("Expected exception: Not a debit card");
		}
		catch (Exception e){
			Assert.assertTrue("Not a debit card", e instanceof SimulationException);
		}
	}
	
	//Checks that a credit card with incorrect pin throws exception when inserted 
	@Test
	public void InvalidPinCreditCard() {
		try {
			payWithCard.PayWithCreditCard(creditCard, 1, new BigDecimal(5), "1499", Bank);
			Assert.fail("Invalid Pin");
		}
		catch (Exception e){
			Assert.assertTrue("Invalid Pin", e instanceof SimulationException);
		}
	}
	
	//Checks that a credit card not issued cannot by used to pay
	@Test
	public void NotIssuedCreditCard() {
		try {
			payWithCard.PayWithCreditCard(notIssuedCredit, 1, new BigDecimal(5), "9876", Bank);
			Assert.fail("Bank Rejected Card");
		}
		catch (Exception e){
			Assert.assertTrue("Bank Rejected Card", e instanceof SimulationException);
		}
	}
	
	//Checks that a credit card cannot be used to pay an amount which exceeds its credit limit
	@Test
	public void InsufficientFundsCreditCard() {
		try {
			payWithCard.PayWithCreditCard(creditCard, 1, new BigDecimal(600), "9876", Bank);
			Assert.fail("Bank Rejected Card");
		}
		catch (Exception e){
			Assert.assertTrue("Bank Rejected Card", e instanceof SimulationException);
		}
	}
	
	//Ensures an invalid payment type throws error
	@Test
	public void InvalidPaymentTypeCredit() {
		try {
			payWithCard.PayWithCreditCard(creditCard, 3, new BigDecimal(10), "9876", Bank);
			Assert.fail("Invalid Payment Option");
		}
		catch (Exception e){
			Assert.assertTrue("Invalid Payment Option", e instanceof SimulationException);
		}
	}
	
	@Test
	public void InvalidPaymentTypeDebit() {
		try {
			payWithCard.PayWithDebitCard(debitCard, -1, new BigDecimal(5), "1234", Bank);
			Assert.fail("Invalid Payment Option");
		}
		catch (Exception e){
			Assert.assertTrue("Invalid Payment Option", e instanceof SimulationException);
		}
	}
	
	//Checks that random error for tap, swipe and insert is working properly
	// Tap prob failure = 0.015
	// Insert = 0.011
	//Swipe = 0.11
	@Test 
	public void RandomErrorWorking() {
		
		int randomInsertFailure = 0;
		int randomSwipeFailure = 0;
		int randomTapFailure = 0;
		
		for (int i = 0; i < 10000; i ++) {
			
			try {
				payWithCard.PayWithDebitCard(debitCard, 0, new BigDecimal(0.01), "1234", Bank);
			}
			catch(Exception e){
				randomTapFailure++;
			}
		}
		
		for (int i = 0; i < 10000; i ++) {
			
			try {
				payWithCard.PayWithDebitCard(debitCard, 1, new BigDecimal(0.01), "1234", Bank);
				payWithCard.removeCard();
			}
			catch(Exception e){
				payWithCard.removeCard();
				randomInsertFailure++;
			}
		}
		
		for (int i = 0; i < 10000; i ++) {
			
			try {
				payWithCard.PayWithDebitCard(debitCard, 2, new BigDecimal(0.01), "1234", Bank);
			}
			catch(Exception e){
				randomSwipeFailure++;
			}
		}
		
		
		Assert.assertTrue(randomInsertFailure > 80 && randomInsertFailure < 140);
		Assert.assertTrue(randomTapFailure > 100 && randomTapFailure < 200);
		Assert.assertTrue(randomSwipeFailure > 1000 && randomSwipeFailure < 1200);
		
	}
	

}
