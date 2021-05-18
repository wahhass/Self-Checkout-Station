package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import CODE.Cart;
import CODE.EnterPLUCode;
import CODE.itemInBaggingAreaV2;

public class EnterPLUCodeTest {

	SelfCheckoutStation checkoutStation;
	itemInBaggingAreaV2 itemInBaggingArea;
	EnterPLUCode enterPLUCode;
	Cart cart;
	
	@Before
	public void setUp() throws Exception {

		EnterPLUCode enterPLUCode = new EnterPLUCode(checkoutStation, EnterPLUCode.getCart(), itemInBaggingArea);
		Cart cart = enterPLUCode.getCart();
		
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

		checkoutStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), billDenominations,
				coinDenominations, 2500, 1);
		itemInBaggingArea = new itemInBaggingAreaV2(checkoutStation, EnterPLUCode.getCart(), (int) checkoutStation.scale.getWeightLimit(),
				(int) checkoutStation.scale.getSensitivity());
		
		enterPLUCode.addToPLUDatabase();

	}

	@After
	public void tearDown() throws Exception {
	}

	//test if a PLUcode is not in the database
	@Test
	public void testPLUItemNotInDatabase() {
		PLUCodedItem banana = new PLUCodedItem(new PriceLookupCode("1111"), 90);
		Assert.assertEquals(false, enterPLUCode.PLU_PRODUCT_DATABASE.containsKey(banana.getPLUCode()));
	}
	
	//tests if a PLUCode is in the database
	@Test
	public void testEnterPLUItemInDatabase() throws Exception  {

		Assert.assertEquals(true, enterPLUCode.PLU_PRODUCT_DATABASE.containsKey(new PriceLookupCode("3082")));
		
	}
	
	//tests trying to add a invalid code
	@Test
	public void testInvalidItem() {
		
		PLUCodedItem banana = new PLUCodedItem(new PriceLookupCode("1111"), 90);
		
		try {
			enterPLUCode.enterPLUItem(banana.getPLUCode());
			Assert.fail("Expected exception:Product does not exist");
		} catch (Exception e) {
			Assert.assertTrue("Product does not exist", e instanceof Exception);
		}
	}
	

}