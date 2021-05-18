package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;

import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import CODE.PayWithNote;

public class PayWithCashTest {
	
	boolean invert;
	int five;
	int ten;
	int twenty;
	int fifty;
	int hundred;
	int[] denominations;
	Currency CDN;
	Banknote noteObj;
	PayWithNote example;
	BigDecimal zero;
	
	Banknote fiveDollar;
	Banknote tenDollar;
	Banknote twentyDollar;
	
	Banknote USD;
	Banknote thirtyDollar;
	
	BigDecimal nickel;
	BigDecimal dime;
	BigDecimal quarter;
	BigDecimal loonie;
	BigDecimal toonie;
	BigDecimal[] coinDenominations;
	
	PayWithNote payWithNoteSoftware;
	PayWithNote payWithNoteGUI;
	
	@Before
	public void setup() {
		invert = false;
		five = 5;
		ten = 10;
		twenty = 20;
		fifty = 50;
		hundred = 100;
		CDN = Currency.getInstance(Locale.CANADA);
		
		fiveDollar = new Banknote(5, CDN);
		tenDollar = new Banknote(10, CDN);
		twentyDollar = new Banknote(20, CDN);
		
		USD = new Banknote(20, Currency.getInstance(Locale.US));
		thirtyDollar = new Banknote(30, CDN);
		
		denominations = new int[]{ five, ten, twenty, fifty, hundred };
		
		payWithNoteSoftware = new PayWithNote(CDN, 10000, denominations, invert);
		
		nickel = new BigDecimal("0.05");
		dime = new BigDecimal("0.10");
		quarter = new BigDecimal("0.25");
		loonie = new BigDecimal("1.00");
		toonie = new BigDecimal("2.00");
		coinDenominations = new BigDecimal[] {nickel, dime, quarter, loonie, toonie};
		
		SelfCheckoutStation SCS = new SelfCheckoutStation(CDN, denominations, coinDenominations, 1000, 1);

		payWithNoteGUI = new PayWithNote(SCS);
		
	}
	
	//Tests that when a null note is added it throws exception
	@Test
	public void testAddNoteNull() {
		
		try {
			payWithNoteSoftware.payNote(null);
			Assert.fail("Expected exception: Coin value is null.");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	//Tests that when a note is added, total cash inserted increases
	@Test
	public void testAddNote() throws DisabledException, OverloadException {
		
		payWithNoteSoftware.payNote(fiveDollar);
		
		Assert.assertEquals(5, payWithNoteSoftware.getTotalCashInserted());
		
		payWithNoteSoftware.payNote(tenDollar);
		
		Assert.assertEquals(15, payWithNoteSoftware.getTotalCashInserted());
		
		payWithNoteSoftware.payNote(twentyDollar);
		
		Assert.assertEquals(35, payWithNoteSoftware.getTotalCashInserted());
	}
	
	//Tests that when an invalid note is added, total cash inserted stays the same
	@Test
	public void testAddInvalidNote() throws DisabledException, OverloadException {
		
		payWithNoteSoftware.payNote(USD);
		
		Assert.assertEquals(0, payWithNoteSoftware.getTotalCashInserted());
		
		payWithNoteSoftware.payNote(thirtyDollar);
		
		Assert.assertEquals(0, payWithNoteSoftware.getTotalCashInserted());
		
	}
	//Tests for GUI constructor using a five dollar bill
	@Test
	public void testPayWithNoteGUI() throws DisabledException, OverloadException {
		
		payWithNoteGUI.payNote(fiveDollar);
		
		Assert.assertEquals(5, payWithNoteGUI.getTotalCashInserted());
	}

}
