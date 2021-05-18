package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import CODE.Cart;
import CODE.itemInBaggingAreaV2;

/**
 * DontBagItemTest.java
 * Tests the use case: Customer does not want to bag a scanned item (also not putting on bagging scale)
 * 
 * Most of the tests cases are taken from itemInBaggingAreaTest.java and modified 
 * to test only the relevant cases. This test emulates not placing an item in the bag or 
 * in the bagging scale in real life (such as leaving the item in your actual shopping cart if its too
 * large to put on the scale, or you just don't want to put it on the scale). 
 * 
 * The methods AddItemNoBag()  and RemoveItemNotInBag for the use case: 
 * "Customer does not want to bag a scanned item"
 * is in itemInBaggingAreaV2.java
 */

public class DontBagItemTest {
	//Variables needed for testing
	itemInBaggingAreaV2 itemInBaggingArea;
	SelfCheckoutStation checkoutStation;
	Cart cart;
	
	BarcodedItem orange;
	BarcodedItem apple;
	BarcodedItem banana;
	BarcodedItem itemTooBig;
	BarcodedItem itemTooSmall;
	
	//Initializes objects that will be used for tests
	//copied from itemInBaggingAreaTest.java setup()
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
				
		orange = new BarcodedItem(new Barcode("12234"), 20);
		apple = new BarcodedItem(new Barcode("14334"), 15);
		banana = new BarcodedItem(new Barcode("15334"), 25);
		
		itemTooBig = new BarcodedItem(new Barcode("15334"), 5000);
		itemTooSmall = new BarcodedItem(new Barcode("15354"), 0.5);
	}
	
	
	//Test to make sure a null item cannot be added 
	@Test
	public void testAddItemNull() {
		try {
			itemInBaggingArea.AddItemNoBag(null);
			Assert.fail("Expected exception: Item is null!");
		}
		catch (Exception e){
			Assert.assertTrue("Item is null!", e instanceof SimulationException);
		}	
	}
	
	//Test to make sure a null item cannot be added 		
	@Test
	public void testRemoveItemNull() {
		try {
			itemInBaggingArea.RemoveItemNotInBag(null);
			Assert.fail("Expected exception: Item is null!");
		}
		catch (Exception e){
			Assert.assertTrue("Item is null!", e instanceof SimulationException);
		}	
	}
		
	//Tests adding a single item without the bagging area
	@SuppressWarnings("deprecation")
	@Test
	public void testAddSingleItem() {
		//Assert.assertEquals((int)itemInBaggingArea.AddItemNoBag(banana), 25);
		try {
			itemInBaggingArea.AddItemNoBag(banana);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 25, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);
	}
			
	//Tests adding multiple items without bagging area
	@SuppressWarnings("deprecation")
	@Test
	public void testAddMultipleItem() {	
		try {
		itemInBaggingArea.AddItemNoBag(apple);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 15 , 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);
		
		try {
			itemInBaggingArea.AddItemNoBag(orange);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 35, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);	
	}
		
	//Tests to make sure items too bigger (than scale limit) and items too small (below sensitivity)  can be added without the bagging area
	@Test
	public void testItemTooSmallAndBig() {	
		try {
		itemInBaggingArea.AddItemNoBag(itemTooSmall);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 0.5, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);
		try { 
		itemInBaggingArea.AddItemNoBag(itemTooBig);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 5000.5, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);
	}
			
	//Tests to make sure the same item is allowed to be added without placing in the bagging area twice
	@Test
	public void testAddSameItem() {
		try {
			itemInBaggingArea.AddItemNoBag(apple);
			} catch (OverloadException e) {
				e.printStackTrace();
			}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 15, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);
		
		try {
			itemInBaggingArea.AddItemNoBag(apple);
			} catch (OverloadException e) {
				e.printStackTrace();
			}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 30, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);
	}
		
	//Tests to make sure an item can be successfully removed once it has been added 
	@Test
	public void testRemoveItem() {
		try {
			itemInBaggingArea.AddItemNoBag(apple);
			} catch (OverloadException e) {
				e.printStackTrace();
			}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 15, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);

		try {
			boolean removeItem = itemInBaggingArea.isInNotBaggedList(apple);
			if (removeItem)
			itemInBaggingArea.RemoveItemNotInBag(apple);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertEquals(itemInBaggingArea.weightNotInBag, 0, 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);	
	}
		

}
