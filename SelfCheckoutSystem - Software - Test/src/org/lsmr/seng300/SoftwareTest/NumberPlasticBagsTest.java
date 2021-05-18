package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import CODE.Bag;
import CODE.Cart;
import CODE.itemInBaggingAreaV2;
import junit.framework.Assert;

/**
 * NumberPlasticBagsTest.java
 * 
 * Tests to make sure that the system receives the 
 * number of bags requested by the customer properly.
 * 
 * The method removePurchasedItems() for the use case: 
 * "Customer enters number of plastic bags used"
 * is in itemInBaggingAreaV2.java
 */

public class NumberPlasticBagsTest {
	
	
	itemInBaggingAreaV2 itemInBaggingArea; //bagging area variable
	SelfCheckoutStation checkout;
	Cart cart;
	
	//setting up the bagging area class
	@Before
	public void setup() {	
		//initializing to setup the SelfCheckoutStation from here 
		int five = 5;
		int ten = 10;
		int twenty = 20;
		int fifty = 50;
		int hundred = 100;
		int[] billDenominations;
				
		billDenominations = new int[]{ five, ten, twenty, fifty, hundred };

		BigDecimal nickel = new BigDecimal("0.05");
		BigDecimal dime = new BigDecimal("0.10");
		BigDecimal quarter = new BigDecimal("0.25");
		BigDecimal loonie = new BigDecimal("1.00");
		BigDecimal toonie = new BigDecimal("2.00");
		
		BigDecimal[] coinDenominations = new BigDecimal[] {nickel, dime, quarter, loonie, toonie};
		//end of initialization to setup the SelfCheckoutStation here 
				
		Cart cart = new Cart();
		SelfCheckoutStation checkoutStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), billDenominations, coinDenominations, 2500, 1);
		itemInBaggingArea = new itemInBaggingAreaV2(checkoutStation, cart,(int)checkoutStation.scale.getWeightLimit(), (int)checkoutStation.scale.getSensitivity());
	}
	
	
	
	//Testing if the customer doesn't want to use any bags
	@SuppressWarnings("deprecation")
	@Test 
	public void ZeroBagsUsedTest () {
		itemInBaggingArea.addNumberPlasticBagsUsed(0);
		Assert.assertEquals(itemInBaggingArea.getNumberPlasticBagsUsed(), 0);
		Assert.assertEquals(itemInBaggingArea.getPlasticBagsPrice(), 0.00);
	}
	
	//Testing if the customer wants to use at least one bag 
	@SuppressWarnings("deprecation")
	@Test 
	public void AtLeastOneBagUsedTest () {
		itemInBaggingArea.addNumberPlasticBagsUsed(4);
		Assert.assertEquals(itemInBaggingArea.getNumberPlasticBagsUsed(), 4);
		Assert.assertEquals(itemInBaggingArea.getPlasticBagsPrice(), 0.40);
	}
	
	//Testing if the customer changes the number of bags used
	@SuppressWarnings("deprecation")
	@Test 
	public void ChangedNumberBagsTest () {
		itemInBaggingArea.addNumberPlasticBagsUsed(3);
		Assert.assertEquals(itemInBaggingArea.getNumberPlasticBagsUsed(), 3);

		itemInBaggingArea.addNumberPlasticBagsUsed(5);
		Assert.assertEquals(itemInBaggingArea.getNumberPlasticBagsUsed(), 5);
		Assert.assertEquals(itemInBaggingArea.getPlasticBagsPrice(), 0.50);

	}
	
	//Testing if invalid number of bags entered. The number of bags used won't change and
	//remain as the previously set value (or default of 0 if bags used not set/changed)
	@SuppressWarnings("deprecation")
	@Test 
	public void InvalidBagsUsedTest () {
		itemInBaggingArea.addNumberPlasticBagsUsed(-2);
		Assert.assertEquals(itemInBaggingArea.getNumberPlasticBagsUsed(), 0);
		Assert.assertEquals(itemInBaggingArea.getPlasticBagsPrice(), 0.00);
	}
}
