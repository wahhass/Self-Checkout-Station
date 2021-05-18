package org.lsmr.seng300.SoftwareTest;




import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import CODE.PayWithCoin;

public class PayWithCoinTest {

	BigDecimal[] denominations;
	BigDecimal nickel;
	BigDecimal dime;
	BigDecimal quarter;
	BigDecimal loonie;
	BigDecimal toonie;
	BigDecimal orderPrice;
	Currency CDN;

	Coin nickelCoin;
	Coin dimeCoin;
	Coin quarterCoin;
	Coin invalidCoin;
	Coin invalidCoin2;

	PayWithCoin payWithCoinSoftware;
	BigDecimal zero;

	int five;
	int ten;
	int twenty;
	int fifty;
	int hundred;
	int[] noteDenominations;

	PayWithCoin payWithCoinGUI;

	@Before
	public void setup() {

		nickel = new BigDecimal("0.05");
		dime = new BigDecimal("0.10");
		quarter = new BigDecimal("0.25");
		loonie = new BigDecimal("1.00");
		toonie = new BigDecimal("2.00");
		five = 5;
		ten = 10;
		twenty = 20;
		fifty = 50;
		hundred = 100;
		denominations = new BigDecimal[] { nickel, dime, quarter, loonie, toonie };
		noteDenominations = new int[]{ five, ten, twenty, fifty, hundred };

		CDN = Currency.getInstance(Locale.CANADA);

		nickelCoin = new Coin(new BigDecimal("0.05"), CDN);
		dimeCoin = new Coin(new BigDecimal("0.10"), CDN);
		quarterCoin = new Coin(new BigDecimal("0.25"), CDN);

		invalidCoin = new Coin(new BigDecimal("0.33"), CDN);
		invalidCoin2 = new Coin(new BigDecimal("0.25"), Currency.getInstance(Locale.US));

		payWithCoinSoftware = new PayWithCoin(CDN, 10000, denominations);
		zero = new BigDecimal("0");
		
		SelfCheckoutStation SCS = new SelfCheckoutStation(CDN, noteDenominations, denominations, 1000, 1);
		payWithCoinGUI = new PayWithCoin(SCS);
	}

	// Tests that when a null coin is added it throws exception
	@Test
	public void testAddCoinNull() {

		try {
			payWithCoinSoftware.payCoin(null);
			Assert.fail("Expected exception: Coin value is null.");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof SimulationException);
		}
	}

	// Tests that when a coin is added, total coin inserted increases
	@Test
	public void testAddCoin() throws DisabledException, OverloadException {

		payWithCoinSoftware.payCoin(nickelCoin);

		Assert.assertEquals(new BigDecimal("0.05"), payWithCoinSoftware.getTotalCoinInserted());

		payWithCoinSoftware.payCoin(dimeCoin);

		Assert.assertEquals(new BigDecimal("0.15"), payWithCoinSoftware.getTotalCoinInserted());

		payWithCoinSoftware.payCoin(quarterCoin);

		Assert.assertEquals(new BigDecimal("0.40"), payWithCoinSoftware.getTotalCoinInserted());
	}

	// Tests that when an invalid coin is added, total coin inserted stays the same
	@Test
	public void testAddInvalidCoin() throws DisabledException, OverloadException {

		payWithCoinSoftware.payCoin(invalidCoin);

		Assert.assertEquals(new BigDecimal("0"), payWithCoinSoftware.getTotalCoinInserted());

		payWithCoinSoftware.payCoin(invalidCoin2);

		Assert.assertEquals(new BigDecimal("0"), payWithCoinSoftware.getTotalCoinInserted());

	}

	// Tests for GUI constructor using a quarter
	@Test
	public void testPayWithCoinGUI() throws DisabledException, OverloadException {

		payWithCoinGUI.payCoin(quarterCoin);

		Assert.assertEquals(new BigDecimal("0.25"), payWithCoinGUI.getTotalCoinInserted());

	}
}