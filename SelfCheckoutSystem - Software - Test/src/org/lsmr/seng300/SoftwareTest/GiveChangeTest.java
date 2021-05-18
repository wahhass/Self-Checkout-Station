package org.lsmr.seng300.SoftwareTest;


import java.math.BigDecimal;


import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import CODE.GiveChange;

public class GiveChangeTest {
	BigDecimal[] coinDenominations;
	int[] noteDenominations;
	BigDecimal nickel;
	BigDecimal dime;
	BigDecimal quarter;
	BigDecimal loonie;
	BigDecimal toonie;
	BigDecimal orderPrice;
	Currency CAD;

	BigDecimal justCoins1;
	BigDecimal justCoins2;
	BigDecimal justCoins3;
	BigDecimal justCash1;
	BigDecimal justCash2;
	BigDecimal justCash3;
	BigDecimal nullChange;
	BigDecimal CashCoin1;
	BigDecimal CashCoin2;
	BigDecimal CashCoin3;

	GiveChange giveChangeSoftware;
	GiveChange giveChangeGUI;
	BigDecimal zero;

	@Before
	public void setup() {
		int[] noteDenominations = {5, 10, 20, 50, 100};
		nickel = new BigDecimal("0.05");
		dime = new BigDecimal("0.10");
		quarter = new BigDecimal("0.25");
		loonie = new BigDecimal("1.00");
		toonie = new BigDecimal("2.00");
		coinDenominations = new BigDecimal[] {nickel, dime, quarter, loonie, toonie};
		CAD = Currency.getInstance(Locale.CANADA);

		justCoins1 = new BigDecimal("0.05");
		justCoins2 = new BigDecimal("0.15");
		justCoins3 = new BigDecimal("1.50");
		justCash1 = new BigDecimal("10.00");
		justCash2 = new BigDecimal("25.00");
		justCash3 = new BigDecimal("155.00");
		CashCoin1 = new BigDecimal("10.45");
		CashCoin2 = new BigDecimal("50.55");
		CashCoin3 = new BigDecimal("133.30");
		giveChangeSoftware = new GiveChange(CAD, true, noteDenominations, coinDenominations);
		zero = new BigDecimal("0.00");
		
		/**
		 * Test for GUI constructor
		 */
		SelfCheckoutStation SCS = new SelfCheckoutStation(CAD, noteDenominations, coinDenominations, 1000, 1);
		giveChangeGUI = new GiveChange(SCS.banknoteOutput, SCS.coinTray, SCS.banknoteDispensers, SCS.coinDispensers, coinDenominations, noteDenominations);
		
	}

	// Tests that when a null value is detected for change it throws a null
	// exception
	@Test
	public void testNullChange() {

		try {
			giveChangeSoftware.returnChange(null);
			Assert.fail("Expected exception: Change value is null.");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof SimulationException);
		}
	}

	// Tests that when total change is in coins, the correct amount is dispensed
	@Test
	public void testCoinChange() throws DisabledException, OverloadException, EmptyException {
		
		giveChangeSoftware.refillCoinDispenser(80);
		

		giveChangeSoftware.returnChange(justCoins1);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());

		giveChangeSoftware.returnChange(justCoins2);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());

		giveChangeSoftware.returnChange(justCoins3);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());
	}

	// Tests that when total change is in cash, the correct amount is dispensed
	@Test
	public void testCashChange() throws DisabledException, OverloadException, EmptyException {
		
		giveChangeSoftware.refillBanknoteDispensers(80);

		giveChangeSoftware.returnChange(justCash1);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());

		giveChangeSoftware.returnChange(justCash2);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());

		giveChangeSoftware.returnChange(justCash3);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());
	}

	// Tests that when total change is in both cash and coins, the correct amount is
	// dispensed
	@Test
	public void testCashCoinChange() throws DisabledException, OverloadException, EmptyException {
		
		giveChangeSoftware.refillBanknoteDispensers(80);
		giveChangeSoftware.refillCoinDispenser(80);

		giveChangeSoftware.returnChange(CashCoin1);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());

		giveChangeSoftware.returnChange(CashCoin2);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());

		giveChangeSoftware.returnChange(CashCoin3);

		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());
	}
	//Test if constructor for GUI works
	@Test
	public void testGiveChangeGUI() throws DisabledException, OverloadException, EmptyException {
		giveChangeGUI.refillBanknoteDispensers(80);
		giveChangeGUI.refillCoinDispenser(80);
		
		giveChangeGUI.returnChange(CashCoin1);

		Assert.assertEquals(true, giveChangeGUI.isChangeDispensed());

		giveChangeGUI.returnChange(CashCoin2);

		Assert.assertEquals(true, giveChangeGUI.isChangeDispensed());

		giveChangeGUI.returnChange(CashCoin3);

		Assert.assertEquals(true, giveChangeGUI.isChangeDispensed());
	}
	
	//Test BanknoteDispenser Empty Exception for GiveChange
	@Test
	public void emptyBanknoteDispenser() {
		
		try {
			giveChangeSoftware.returnChange(justCash1);
			Assert.fail("Expected exception: Cash Dispenser Empty");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof Exception);
		}
	}
	
	//Test Coin Dispenser Empty Exception for GiveChange
	@Test
	public void emptyCoinDispenser() {
		
		try {
			giveChangeSoftware.returnChange(justCoins1);
			Assert.fail("Expected exception: Cash Dispenser Empty");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof Exception);
		}
	}
	
	//Tests Refill BankNoteDispenser 
	@Test
	public void testRefillAllBankNoteDispensers() {
		giveChangeSoftware.refillBanknoteDispensers(3);
		
		Assert.assertEquals(3, giveChangeSoftware.getBankNoteDispensers().get(5).size());
		Assert.assertEquals(3, giveChangeSoftware.getBankNoteDispensers().get(10).size());
		Assert.assertEquals(3, giveChangeSoftware.getBankNoteDispensers().get(20).size());
		Assert.assertEquals(3, giveChangeSoftware.getBankNoteDispensers().get(50).size());
		Assert.assertEquals(3, giveChangeSoftware.getBankNoteDispensers().get(100).size());
		
		
	}
	
	//Tests Overload BankNote Dispenser 
	@Test
	public void testOverloadAllBankNoteDispensers() {
		try {
			
			giveChangeSoftware.refillBanknoteDispensers(1000);
			
			Assert.fail("Expected exception: Cash Dispenser Overloaded");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof Exception);
		}
	}
	
	//Tests Individual BankNote Dispenser Refill
	@Test
	public void RefillOneBankNoteDispensers() throws EmptyException, DisabledException, OverloadException {
		giveChangeSoftware.refillBanknoteDispensers(1);
		
		giveChangeSoftware.returnChange(new BigDecimal("5.00"));
		
		giveChangeSoftware.refillBanknoteDispensers(10);
		
		Assert.assertEquals(10, giveChangeSoftware.getBankNoteDispensers().get(5).size());
		Assert.assertEquals(1, giveChangeSoftware.getBankNoteDispensers().get(10).size());
		
		
	}
	
	//Tests Refill Coin Dispenser 
	@Test
	public void testRefillAllCoinDispensers() {
		giveChangeSoftware.refillCoinDispenser(10); ;
	
		Assert.assertEquals(10, giveChangeSoftware.getCoinDispensers().get(nickel).size()); 
		Assert.assertEquals(10, giveChangeSoftware.getCoinDispensers().get(dime).size());
		Assert.assertEquals(10, giveChangeSoftware.getCoinDispensers().get(quarter).size());
		Assert.assertEquals(10, giveChangeSoftware.getCoinDispensers().get(loonie).size());
		Assert.assertEquals(10, giveChangeSoftware.getCoinDispensers().get(toonie).size());
	}
		
	
	@Test
	public void testOverloadAllCoinDispensers() {
		try {
			giveChangeSoftware.refillCoinDispenser(1000);
			Assert.fail("Expected exception: Coin Dispenser Overloaded");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof Exception);
		}
	}
	
	//Tests Individual Coin Dispenser Refill
	@Test
	public void RefillOneCoinDispensers() throws EmptyException, DisabledException, OverloadException {
		giveChangeSoftware.refillCoinDispenser(1);
		
		giveChangeSoftware.returnChange(new BigDecimal("2.00"));
		
		giveChangeSoftware.refillCoinDispenser(10);
		
		Assert.assertEquals(10, giveChangeSoftware.getCoinDispensers().get(toonie).size());
		Assert.assertEquals(1, giveChangeSoftware.getCoinDispensers().get(nickel).size());
		
		
	}
	
	
		
		
}