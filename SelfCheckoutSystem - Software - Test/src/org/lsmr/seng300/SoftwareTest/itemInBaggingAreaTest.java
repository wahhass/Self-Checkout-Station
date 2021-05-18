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
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import CODE.Bag;
import CODE.Cart;
import CODE.itemInBaggingAreaV2;

public class itemInBaggingAreaTest {

	itemInBaggingAreaV2 itemInBaggingArea;

	BarcodedItem orange;
	BarcodedItem apple;
	BarcodedItem banana;
	BarcodedItem itemTooBig;
	BarcodedItem itemTooSmall;
	PLUCodedItem pear;
	
	Item reusableBag;

	SelfCheckoutStation checkoutStation;
	
	// Initializes objects that will be used for tests
	@Before
	public void setup() {
		int five = 5;
		int ten = 10;
		int twenty = 20;
		int fifty = 50;
		int hundred = 100;
		int[] billDenominations;

		billDenominations = new int[] { five, ten, twenty, fifty, hundred };

		BigDecimal nickel = new BigDecimal("0.05");
		BigDecimal dime = new BigDecimal("0.10");
		BigDecimal quarter = new BigDecimal("0.25");
		BigDecimal loonie = new BigDecimal("1.00");
		BigDecimal toonie = new BigDecimal("2.00");
		BigDecimal[] coinDenominations = new BigDecimal[] { nickel, dime, quarter, loonie, toonie };

		Cart cart = new Cart();
		checkoutStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA),
				billDenominations, coinDenominations, 2500, 1);
		itemInBaggingArea = new itemInBaggingAreaV2(checkoutStation, cart, (int) checkoutStation.scale.getWeightLimit(),
				(int) checkoutStation.scale.getSensitivity());

		orange = new BarcodedItem(new Barcode("12234"), 20);
		apple = new BarcodedItem(new Barcode("14334"), 15);
		banana = new BarcodedItem(new Barcode("15334"), 25);
		
		pear = new PLUCodedItem(new PriceLookupCode("4201"), 100);

		itemTooBig = new BarcodedItem(new Barcode("15334"), 5000);
		itemTooSmall = new BarcodedItem(new Barcode("15354"), 0.5);

		reusableBag = new Bag(9.3);
		
	}

	// Make sure a null item cannot be indexed
	@Test
	public void testIndexItemNull() {

		try {
			itemInBaggingArea.IndexItem(null);
			Assert.fail("Expected exception: Item is null!");
		} catch (Exception e) {
			Assert.assertTrue("Item is null!", e instanceof SimulationException);
		}

	}

	// Make sure a null item cannot be added
	@Test
	public void testAddItemNull() {

		try {
			itemInBaggingArea.AddItem(null);
			Assert.fail("Expected exception: Item is null!");
		} catch (Exception e) {
			Assert.assertTrue("Item is null!", e instanceof SimulationException);
		}

	}

	// Make sure a null item cannot be removed
	@Test
	public void testRemoveItemNull() {

		try {
			itemInBaggingArea.RemoveItem(null);
			Assert.fail("Expected exception: Item is null!");
		} catch (Exception e) {
			Assert.assertTrue("Item is null!", e instanceof SimulationException);
		}

	}

	// Make sure item index works and properly adds and item when it is not present
	@Test
	public void testIndexItem() {

		itemInBaggingArea.IndexItem(orange);

		ArrayList<Item> itemsOnScale = new ArrayList<Item>();

		itemsOnScale = itemInBaggingArea.getItemsOnScale(itemsOnScale);

		Assert.assertEquals(itemsOnScale.get(0), orange);
	}

	// Make sure item index can add multiple items
	@Test
	public void testMultipleItems() {

		itemInBaggingArea.IndexItem(orange);
		itemInBaggingArea.IndexItem(apple);
		itemInBaggingArea.IndexItem(banana);

		ArrayList<Item> itemsOnScale = new ArrayList<Item>();

		itemsOnScale = itemInBaggingArea.getItemsOnScale(itemsOnScale);

		Assert.assertEquals(itemsOnScale.get(0), orange);
		Assert.assertEquals(itemsOnScale.get(1), apple);
		Assert.assertEquals(itemsOnScale.get(2), banana);
	}

	// Make sure the same item cannot be added twice
	@Test
	public void testIndexSameItem() {

		itemInBaggingArea.IndexItem(apple);
		itemInBaggingArea.IndexItem(apple);

		ArrayList<Item> itemsOnScale = new ArrayList<Item>();

		itemsOnScale = itemInBaggingArea.getItemsOnScale(itemsOnScale);

		Assert.assertEquals(itemsOnScale.get(0), apple);

		try {
			itemsOnScale.get(1);
			Assert.fail("Expected exception: Out of Bounds");
		} catch (Exception e) {
			Assert.assertTrue("Out of Bounds!", e instanceof IndexOutOfBoundsException);
		}
	}

	// Tests adding an item to the scale
	@Test
	public void testAddItem() throws Exception {

		Assert.assertEquals(itemInBaggingArea.AddItem(apple), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);

		Assert.assertEquals(itemInBaggingArea.AddItem(orange), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);

	}

	// Makes sure the same item cannot be added to the scale twice
	@Test
	public void testAddSameItem() throws Exception {

		Assert.assertEquals(itemInBaggingArea.AddItem(apple), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);

		Assert.assertEquals(itemInBaggingArea.AddItem(apple), 2);

	}

	// Makes sure an item cannot be removed from the scale when there are no items
	// present
	@Test
	public void testRemoveNullItem() throws Exception {

		Assert.assertEquals((int) itemInBaggingArea.RemoveItem(apple), -1);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), false);

	}

	// Makes sure an item can be successfully removed once it has been added
	@Test
	public void testRemoveItem() throws Exception {

		Assert.assertEquals(itemInBaggingArea.AddItem(apple), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);

		Assert.assertEquals((int)itemInBaggingArea.RemoveItem(apple), 0);
		Assert.assertEquals(itemInBaggingArea.getListenerBoolean(), true);

	}

	/**
	 * Tests for adding own bag use case
	 */

	// Tests placing own bag on scale when ownBag is true
	@Test
	public void testPlaceOwnBagTrue() {
		itemInBaggingArea.setOwnBag(true);
		try {
			itemInBaggingArea.placeOwnBag(reusableBag);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(true, itemInBaggingArea.getListenerBoolean());
		Assert.assertEquals(true, itemInBaggingArea.getBagPlacedOnScale());
		Assert.assertEquals(9.3, itemInBaggingArea.predictedWeightOnBagging, 0);
	}

	// Tests placing own bag on scale when ownBag is false
	@Test
	public void testPlaceOwnBagFalse() {
		itemInBaggingArea.setOwnBag(false);
		try {
			itemInBaggingArea.placeOwnBag(reusableBag);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(false, itemInBaggingArea.getListenerBoolean());
		Assert.assertEquals(false, itemInBaggingArea.getBagPlacedOnScale());
		Assert.assertEquals(0.0, itemInBaggingArea.predictedWeightOnBagging, 0);
	}

	// Tests placing own bag on scale that is too heavy
	@Test
	public void testPlaceOwnBagOverload() {
		reusableBag = new Bag(3000);
		itemInBaggingArea.setOwnBag(true);
		try {
			itemInBaggingArea.placeOwnBag(reusableBag);
		} catch (OverloadException e) {
			Assert.assertTrue("Item is null!", e instanceof OverloadException);
		}
	}
	
	// Tests for when item doesn't conform to expectations based on barcoded item scanned
	@Test
	public void testCheckWeightDiscrepancyBarcodedItem() throws Exception {
		// Pretend we scanned it
		itemInBaggingArea.checkWeightDiscrepancy(orange, 1000, orange.getWeight());
		Assert.assertEquals(false, itemInBaggingArea.getCorrectItemAccepted());
	}
	
	// Tests for when item doesn't conform to expectations based on PLU item weighed
	@Test
	public void testWeightDiscrepancyPLUItem() throws Exception {
		// pear
		itemInBaggingArea.checkWeightDiscrepancy(pear, 1000, pear.getWeight());
		Assert.assertEquals(false, itemInBaggingArea.getCorrectItemAccepted());
	}
	
	// Test when attendant notified of weight change but is not logged in
	@SuppressWarnings("deprecation")
	@Test
	public void testAttendantNotifiedWeightLoggedIn() throws OverloadException {
		checkoutStation.baggingArea.add(orange);
		itemInBaggingArea.attendantNotifiedWeight(true);
		Assert.assertEquals(true, itemInBaggingArea.getCorrectItemAccepted());
		Assert.assertEquals(itemInBaggingArea.predictedWeightOnBagging, checkoutStation.baggingArea.getCurrentWeight(), 1e-15);
	}
	
	
	

}