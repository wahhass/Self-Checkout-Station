package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import CODE.Cart;
import CODE.CartProduct;
import CODE.productScan;

/**
 * Test class for productScan, cart, and cartproduct
 * @author Hailey and Abi
 *
 */
public class scanItemTest {
	
	productScan productScan; 
	Cart cart;
	Barcode barcode;
	Barcode barcode1;
	Barcode barcode2;
	Barcode barcode3;
	Barcode barcode4;
	BarcodedItem item;
	
	BarcodedProduct cheese;
	BarcodedProduct milk;
	BarcodedProduct meat;
	BarcodedProduct egg;
	
	BarcodedItem cheeseItem;
	BarcodedItem milkItem;
	BarcodedItem meatItem;
	BarcodedItem eggItem;
	
	//Initializes objects that will be used for testing
	@Before
	public void setup() {
		cart = new Cart();
		productScan = new productScan(cart); 
		
		cheese = new BarcodedProduct(new Barcode("1111"), "Cheese", new BigDecimal(5.00));
		
		milk = new BarcodedProduct(new Barcode("2222"), "Milk", new BigDecimal(4.50));
		
		meat = new BarcodedProduct(new Barcode("3333"), "Meat", new BigDecimal(10.00));

		egg = new BarcodedProduct(new Barcode("4444"), "", new BigDecimal(2.00));

		barcode1 = new Barcode("1111");
		cheeseItem = new BarcodedItem(barcode1, 5.00);
		
		barcode2 = new Barcode("2222");
		milkItem = new BarcodedItem(barcode2, 4.50);
		
		barcode3 = new Barcode("3333");
		meatItem = new BarcodedItem(barcode3, 10.00);
		
		barcode4 = new Barcode("4444");
		eggItem = new BarcodedItem(barcode4, 10.00);
		
		productScan.addToDatabase(barcode1, cheese);
		productScan.addToDatabase(barcode2, milk);
		productScan.addToDatabase(barcode3, meat);
		productScan.addToDatabase(barcode4, egg);

	}
	
	//Tests is product in system function when product is not in database
	@Test
	public void testProductInSystemNotFound() {
		
		barcode = new Barcode("12234");
		item = new BarcodedItem(barcode, 20);
		
		Assert.assertFalse(productScan.isProductInSystem(item));
		
	}
	
	//Tests is product in system function when product null
	@Test
	public void testProductInSystemNullOrEmpty() {
		
		Assert.assertFalse(productScan.isProductInSystem(null)); 
		
	}
	
	//Tests is product in system function when product is in database
	@Test
	public void testProductInSystemFound() {
		
		barcode = new Barcode("00000");
		item = new BarcodedItem(barcode, 10);
		BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, "lime", new BigDecimal(10));
		productScan.addToDatabase(barcode, barcodedProduct);
		
		Assert.assertTrue(productScan.isProductInSystem(item));
		
	}
	
	//Tests if a product has an empty description
	@Test
	public void testEmptyDescription() {
		
		try {
			productScan.scanItem(eggItem);
			Assert.fail("Expected exception: Description is empty");
		} catch (Exception e) {
			Assert.assertTrue("Description is empty", e instanceof SimulationException);
		}
		
	}
	
	//tests adding a null barcode to the database
	@Test
	public void testAddNullBarcodeToDatabase() {
		
		try {
			productScan.addToDatabase(null, milk);
			Assert.fail("Expected exception: Barcode is null");
		}
		catch (Exception e){
			Assert.assertTrue("Barcode is null", e instanceof NullPointerException);
		}
			
	}
	
	//tests adding a null product to the database
		@Test
		public void testAddNullProductToDatabase() {
			
			try {
				productScan.addToDatabase(new Barcode("0000"), null);
				Assert.fail("Expected exception: Product is null");
			}
			catch (Exception e){
				Assert.assertTrue("Product is null", e instanceof NullPointerException);
			}
				
		}
	
	//Tests adding price of products to cumulative total
	@Test
	public void testAddPrice() {
		
		cart = new Cart();
		productScan = new productScan(cart);
		
		productScan.scanItem(cheeseItem);
		
		Assert.assertEquals(new BigDecimal(5.00), cart.getCumPrice());
		
		productScan.scanItem(meatItem);
		
		Assert.assertEquals(new BigDecimal(15.00), cart.getCumPrice());
		
	}
	
	//Tests that descriptions print correct
	@Test
	public void testDescriptions() {
		
		cart = new Cart();
		productScan = new productScan(cart);
		
		productScan.scanItem(cheeseItem);
		
		Assert.assertEquals("Cheese: $5\n", productScan.getProductArray());
		
		productScan.scanItem(meatItem);
		
		Assert.assertEquals("Cheese: $5\nMeat: $10\n", productScan.getProductArray());
		
		productScan.scanItem(milkItem);
		
		Assert.assertEquals("Cheese: $5\nMeat: $10\nMilk: $4.5\n", productScan.getProductArray());
		
	}
	
	//Tests scanning items
	@Test
	public void testScanItem() {
		
		cart = new Cart();
		productScan = new productScan(cart);
		
		barcode = new Barcode("1111");
		item = new BarcodedItem(barcode, 5.00);
		
		productScan.scanItem(item);
		Assert.assertEquals("Cheese: $5\n", productScan.getProductArray());
		Assert.assertEquals(new BigDecimal(5), productScan.getCumPrice());
		
		barcode = new Barcode("2222");
		BarcodedItem otherItem = new BarcodedItem(barcode, 4.50);
		productScan.scanItem(otherItem);
		Assert.assertEquals("Cheese: $5\nMilk: $4.5\n", productScan.getProductArray());
		Assert.assertEquals(new BigDecimal(9.5), productScan.getCumPrice());
	}
	
	//Tests scanning item that is not in database
		@Test
		public void testScanItemNotInDatabase() {
			
			cart = new Cart();
			productScan = new productScan(cart);
			
			barcode = new Barcode("11111");
			item = new BarcodedItem(barcode, 10);;
			
			productScan.scanItem(item);
			Assert.assertEquals("", productScan.getProductArray());
			Assert.assertEquals(new BigDecimal(0), productScan.getCumPrice());
			
			barcode = new Barcode("22222");
			BarcodedItem otherItem = new BarcodedItem(barcode, 15);;
			productScan.scanItem(otherItem);
			Assert.assertEquals("", productScan.getProductArray());
			Assert.assertEquals(new BigDecimal(0), productScan.getCumPrice());
		}
		
		//Tests for the random scan fails
		@Test
		public void randomItemScanFail() {
			//Ensures that item scan probability of fail is accurate even if item is in database
			
			barcode = new Barcode("00000");
			BarcodedItem item = new BarcodedItem(barcode, 10);
			
			//For 1,000 scans roughly 900 should be successful 
			for (int i=1; i<=1000; i++) {
				productScan.scanItem(item);
			}
			int counter = productScan.getCounter();
			Assert.assertTrue(875 <= counter && counter <= 925);
			
		
		}
		
	//Tests Finishing Adding Items	
		@Test
		public void finishNullAddTest() {
			
			ArrayList<CartProduct> productList = null;
			
			productScan.finishAdding(productList);
			boolean finish = productScan.isFinish();
			Assert.assertFalse(finish = false);
		}
		
	//Tests finishing adding items
		@Test
		public void finishAddTest() {
			barcode = new Barcode("00000");
			item = new BarcodedItem(barcode, 10);
			productScan.scanItem(item);
			productScan.finishAdding(productScan.isArray());
			System.out.println(((BigDecimal) productScan.getCumPrice()).doubleValue());
			boolean finish = productScan.isFinish();
			Assert.assertTrue(finish = true);
			
		}
	
	//Tests removing an item while logged in
		@Test
		public void removeItemLoggedIn() {
			
			productScan.setTrueLoggedIn();
			
			productScan.scanItem(cheeseItem);
			productScan.scanItem(milkItem);
			
			ArrayList<CartProduct> list = cart.returnCart();
			CartProduct car_prod = list.get(0);
			productScan.removeItem(car_prod);
		
		}
		
		
	//Test Set Finish 
		@Test
		public void setFinishTest() {
			productScan.setFinish(true);
		}
		
	//Tests removing an item while not logged in
		@Test
		public void removeItemNotLogin() {
			productScan.scanItem(cheeseItem);
			productScan.scanItem(milkItem);
			
			ArrayList<CartProduct> list = cart.returnCart();
			CartProduct car_prod = list.get(0);
			
			try {
				productScan.removeItem(car_prod);
				Assert.fail("Attendant Must Login To Remove Item From Cart");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof SimulationException);
			}
	
		}
}