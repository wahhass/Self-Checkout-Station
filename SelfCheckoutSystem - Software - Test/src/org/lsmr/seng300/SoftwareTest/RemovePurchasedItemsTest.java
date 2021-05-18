package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import CODE.Bag;
import CODE.Cart;
import CODE.itemInBaggingAreaV2;

/**
 * RemovePurchasedItemsTest.java
 * Tests to make sure all purchased items are removed 
 * at the end of the checkout process 
 * 
 * The method removePurchasedItems() for the use case: 
 * "Customer removes purchased items from bagging area"
 * is in itemInBaggingAreaV2.java
 */

public class RemovePurchasedItemsTest {
	//Variables needed for testing
	itemInBaggingAreaV2 itemInBaggingArea;
	
	BarcodedItem orange;
	BarcodedItem apple;
	BarcodedItem banana;
	
	Cart cart;
	SelfCheckoutStation checkoutStation;
	
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
		
		//itemInBaggingArea = new itemInBaggingAreaV2(checkoutStation, cart, 2500, 1);
		
		orange = new BarcodedItem(new Barcode("12234"), 20);
		apple = new BarcodedItem(new Barcode("14334"), 15);
		banana = new BarcodedItem(new Barcode("15334"), 25);
	}
	
	
	//Tests to make sure if no items are added, the system has no items 
	//on the bagging scale at the end of the checkout process
	@Test
	public void testRemoveNoItem() {
		//remove all items on the bagging scale (but does nothing here sense no items added)
		itemInBaggingArea.removePurchasedItems(); 
		
		//creating a list of the items in the scale
		ArrayList<Item> itemsOnScale = new ArrayList<Item>();
		itemsOnScale = itemInBaggingArea.getItemsOnScale(itemInBaggingArea.items);
		
		Assert.assertEquals(itemsOnScale.size(), 0); //make sure the items list is empty
		Assert.assertEquals(itemInBaggingArea.predictedWeightOnBagging, 0.0,0.0); //making sure no weight registered
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);
	}
		
	//Tests to make sure if only 1 item is added, the system has no items 
	//on the bagging scale at the end of the checkout process	
	@Test
	public void testRemoveSingleItem() throws Exception {
		//adding items to the bagging scale
		Assert.assertEquals(itemInBaggingArea.AddItem(orange), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);
		
		//after the customer is done purchasing items, remove all the items from the scale
		itemInBaggingArea.removePurchasedItems();
		
		//creating a list of the items in the scale
		ArrayList<Item> itemsOnScale = new ArrayList<Item>();
		itemsOnScale = itemInBaggingArea.getItemsOnScale(itemInBaggingArea.items);
		
		Assert.assertEquals(itemsOnScale.size(), 0); //make sure the items list is empty
		Assert.assertEquals(itemInBaggingArea.predictedWeightOnBagging, 0.0,0.0); //making sure no weight registered
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);
	}
	
	//Tests to make sure if multiple items are added, the system has no items 
	//on the bagging scale at the end of the checkout process	
	@Test
	public void testRemoveMultipleItem() throws Exception {
		
		//adding 3 items to the bagging scale
		Assert.assertEquals(itemInBaggingArea.AddItem(apple), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);
		
		Assert.assertEquals(itemInBaggingArea.AddItem(orange), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);
			
		Assert.assertEquals(itemInBaggingArea.AddItem(banana), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);
			
		//after the customer is done purchasing items, remove all the items from the scale 
		itemInBaggingArea.removePurchasedItems();
		
		//creating a list of the items in the scale
		ArrayList<Item> itemsOnScale = new ArrayList<Item>();
		itemsOnScale = itemInBaggingArea.getItemsOnScale(itemInBaggingArea.items);

		Assert.assertEquals(itemsOnScale.size(), 0); //make sure the items list is empty
		Assert.assertEquals(itemInBaggingArea.predictedWeightOnBagging, 0.0,0.0); //making sure no weight registered
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);
	}

}
