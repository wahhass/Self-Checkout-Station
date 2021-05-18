package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import javax.swing.DefaultListModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import CODE.Bag;
import CODE.Cart;
import CODE.EmptyBanknote;
import CODE.EmptyCoin;
import CODE.EnterPLUCode;
import CODE.GUI;
import CODE.LoginToControl;
import CODE.MemberCard;
import CODE.PayWithCard;
import CODE.PayWithCoin;
import CODE.PayWithNote;
import CODE.PrintReceipt;
import CODE.itemInBaggingAreaV2;
import CODE.productScan;


public class SystemTestingWeirdTestCases {
	// Parameters for SelfCheckoutStation (same paramater as GUI.java)
	public static final Currency CAD = Currency.getInstance(Locale.CANADA);
	public static final int[] banknoteDenominations = {5, 10, 20, 50, 100, 500};
	public static final BigDecimal[] coinDenominations = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};

	public static itemInBaggingAreaV2 baggingArea;
	public static productScan scanner;
	public static EnterPLUCode plu;
	public static Cart cart = new Cart();
	public static DefaultListModel dlm = new DefaultListModel();
	public static PayWithCard cardPayment;
	public static PayWithCoin coinPayment;
	public static PayWithNote notePayment;
	public static PrintReceipt receiptPrinter;
	public static LoginToControl staffLogin;
	public static EmptyBanknote emptyNotes;
	public static EmptyCoin emptyCoins;

	public static Calendar expiry = Calendar.getInstance();
	public static Item itemForBag;

	BarcodedItem cheeseItem;
	BarcodedItem milkItem;
	BarcodedItem meatItem;
	BarcodedItem eggItem;
	BarcodedItem overWeightLimitItem;
	
	Barcode barcode1;
	Barcode barcode2;
	Barcode barcode3;
	Barcode barcode4;
	Barcode barcode5;

	BarcodedProduct cheese;
	BarcodedProduct milk;
	BarcodedProduct meat;
	BarcodedProduct eggInvalid;
	BarcodedProduct overWeightLimit;


	Card debitCard;
	Card creditCard;
	Card notIssuedDebit;
	Card notIssuedCredit;
	Card giftCard;
	Card notIssuedGiftCard;

	CardIssuer Bank;
	CardIssuer Coop;

	private static final int MAXIMUM_INK = 1 << 20;
	private static final int MAXIMUM_PAPER = 1 << 10;

	//member cards 
	Card memberCard;
	Card memberCard2;
	Card memberCard3;
	Card memberCard4;

	MemberCard member;
	MemberCard member2;
	MemberCard member3;
	MemberCard member4;

	Item reusableBag;

	// Nearly identical from GUI.java's main method
	@ Before
	public void setUp() {
		SelfCheckoutStation selfCheckout = new SelfCheckoutStation(CAD, banknoteDenominations, coinDenominations, 1000, 1);
		baggingArea = new itemInBaggingAreaV2(selfCheckout, cart, 1000, 1);
		scanner = new productScan(cart);
		plu = new EnterPLUCode(selfCheckout, cart, baggingArea);
		cardPayment = new PayWithCard();
		coinPayment = new PayWithCoin(selfCheckout);
		notePayment = new PayWithNote(selfCheckout);
		receiptPrinter = new PrintReceipt(selfCheckout.printer);
		staffLogin = new LoginToControl();
		emptyNotes = new EmptyBanknote(selfCheckout.banknoteStorage);
		emptyCoins = new EmptyCoin(selfCheckout.coinStorage);


		expiry.add(Calendar.MONTH, 2);
		expiry.add(Calendar.YEAR, 2);
		staffLogin.addToDatabase("1234", "Sammy");
		GUI.populateBarcodeDatabase(scanner);
		plu.addToPLUDatabase();
		cart.clearCart();

		// Item and product initialization

		cheese = new BarcodedProduct(new Barcode("1111"), "Cheese", new BigDecimal(5.00));

		milk = new BarcodedProduct(new Barcode("2222"), "Milk", new BigDecimal(4.50));

		meat = new BarcodedProduct(new Barcode("3333"), "Meat", new BigDecimal(10.00));

		eggInvalid = new BarcodedProduct(new Barcode("4444"), "", new BigDecimal(2.00));
		
		overWeightLimit = new BarcodedProduct(new Barcode("5555"), "OverWeightLimit", new BigDecimal(1200.00));

		barcode1 = new Barcode("1111");
		cheeseItem = new BarcodedItem(barcode1, 5.00);

		barcode2 = new Barcode("2222");
		milkItem = new BarcodedItem(barcode2, 4.50);

		barcode3 = new Barcode("3333");
		meatItem = new BarcodedItem(barcode3, 10.00);

		barcode4 = new Barcode("4444");
		eggItem = new BarcodedItem(barcode4, 10.00);
		
		barcode5 = new Barcode("5555");
		overWeightLimitItem = new BarcodedItem(barcode5, 1200.00);

		scanner.addToDatabase(barcode1, cheese);
		scanner.addToDatabase(barcode2, milk);
		scanner.addToDatabase(barcode3, meat);
		scanner.addToDatabase(barcode4, eggInvalid);
		scanner.addToDatabase(barcode5, overWeightLimit);

		// Initialize gift, debit and credit cards
		debitCard = new Card("debit", "6750094156347755", "Billy", "056", "1234", true, true);
		creditCard = new Card("credit", "6750094154567755", "Billy", "043", "9876", true, true);
		notIssuedDebit = new Card("debit", "1928374654783829", "Billy", "077", "1234", true, true);
		notIssuedCredit = new Card("credit", "1928374654783829", "Billy", "077", "1234", true, true);
		giftCard = new Card("gift card", "6750056347755", "Andrew", null, null, false, false);
		notIssuedGiftCard = new Card("gift card", "6750056347755678", "Bill", null, null, false, false);


		Bank = new CardIssuer("TD Canada");

		Coop = new CardIssuer("Coop");

		Bank.addCardData("6750094156347755", "Billy", expiry, "056", new BigDecimal(2500));
		Bank.addCardData("6750094154567755", "Billy", expiry, "043", new BigDecimal(500));

		Coop.addCardData("6750056347755", "Andrew", expiry, "000", new BigDecimal(100));

		//Print receipt 
		receiptPrinter.printer.addPaper(MAXIMUM_PAPER);
		receiptPrinter.printer.addInk(MAXIMUM_INK);
		receiptPrinter.setPaperLevel(MAXIMUM_PAPER);
		receiptPrinter.setInkLevel(MAXIMUM_INK);

		//initialize member info
		memberCard = new Card("member", "0019284527102937", "Joe", null, null, false, false);
		memberCard2 = new Card("member", "0019284527102937", "Joe", null, null, false, false);
		memberCard3 = new Card("member", "0019284527102937", "Joe", null, null, false, false);
		memberCard4 = new Card("member", "0123345678901234", "Tim", null, null, false, false);

		member = new MemberCard("member", "0019284527102937", "Joe", new BigDecimal(30));
		member2 = new MemberCard("member", "0019284527102937", "Joe", new BigDecimal(0));
		member3 = new MemberCard("member", "0019284527102937", "Joe", null);
		member4 = new MemberCard("member", "0123345678901234", "Tim", new BigDecimal(20));

		member.addToDatabase("0019284527102937", "Joe");
		reusableBag = new Bag(9.3);
		baggingArea.removePurchasedItems(); //reset for future tests
	}
	
	//Tests someone purchasing nothing.  
	@ Test
	public void NoItemsPurchased() throws Exception {
		cart.clearCart();
		System.out.println();

		// Set own bags
		baggingArea.setOwnBag(false);

		Assert.assertEquals(baggingArea.predictedWeightOnBagging, 0.00, 0); //check scale weight

		Assert.assertEquals(0.0, cart.getCumPrice().doubleValue(), 0); //check current total

		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(0); 

		// Payment
		Assert.assertTrue(cardPayment.PayWithGiftCard(giftCard, cart.getCumPrice(), Coop)); //make sure paid		

		// Receipt
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
	}
	
	//Tests for scanning an item over the scale weight limit. 
	@ Test
	public void OverWeightLimit() {
		cart.clearCart();
		System.out.println();

		// Set own bags
		baggingArea.setOwnBag(false);

		// Scan items
		scanner.scanItem(overWeightLimitItem);
		//Assert.assertEquals("OverWeightLimitItem: $1200\n", scanner.getProductArray());

		try {
			//since the item is over the weight limit, cannot place item on bagging area
			baggingArea.AddItemNoBag(overWeightLimitItem); 
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertEquals(baggingArea.predictedWeightOnBagging, 0.0, 0); //check scale weight. should have no change

		// Select number of bags used		
		baggingArea.addNumberPlasticBagsUsed(1);

		//member info 
		boolean memberBool = member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200));
		Assert.assertTrue(memberBool); //make sure membership valid 


		Assert.assertEquals(1200.10, cart.getCumPrice().doubleValue(), 0); //check current total

		// Payment
		Assert.assertTrue(cardPayment.PayWithDebitCard(debitCard, 1, cart.getCumPrice(), "1234", Bank)); //make sure paid		

		// Receipt
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
	}
	
	//Tests for adding lots of items of the same product
	@ Test
	public void TestAlotOfSmallItemsAdded() throws Exception {
		cart.clearCart();
		System.out.println();

		// Set own bags
		baggingArea.setOwnBag(false);

		// Scan item
		for (int x = 0; x < 50; x++) {
		scanner.scanItem(cheeseItem);

		try {
			// Bag item
			baggingArea.AddItem(cheeseItem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		}
		Assert.assertEquals(250.00, cart.getCumPrice().doubleValue(), 0); //check current total

		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(8);

		Assert.assertEquals(250.80, cart.getCumPrice().doubleValue(), 0); //check current total

		// Payment
		Assert.assertTrue(cardPayment.PayWithDebitCard(debitCard, 1, cart.getCumPrice(), "1234", Bank)); //make sure paid

		// Receipt
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
	}
	
	//Tests for if a customer enters an invalid number of bags ( less than 0)
	@ Test
	public void InvalidNumberOfBags() {
		cart.clearCart();
		System.out.println();

		// Set own bags
		baggingArea.setOwnBag(false);

		// Scan items
		scanner.scanItem(cheeseItem);
		Assert.assertEquals("Cheese: $5\n", scanner.getProductArray());


		try {
			// Bag item
			baggingArea.AddItem(cheeseItem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertEquals(baggingArea.predictedWeightOnBagging, 5.00, 0); //check scale weight

		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(-1);

		Assert.assertEquals(5.0, cart.getCumPrice().doubleValue(), 0); //check current total

		// Payment
		Assert.assertTrue(cardPayment.PayWithGiftCard(giftCard, cart.getCumPrice(), Coop)); //make sure paid

		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
	}
	
	
	//Tests for a non member enter the PLU of a single item and paying with a credit card
	@ Test
	public void invalidPLU() throws Exception {
		cart.clearCart();
		System.out.println();

		// Set own bags
		baggingArea.setOwnBag(false);

		// Enter PLU items
		try {
		plu.enterPLUItem(new PriceLookupCode("12")); //invalid
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof SimulationException);
			return;
		//In GUI, customer can go back to re-enter the PLU, but here we'll show that not all PLU's work (if not registered, they won't work)
		}
		
		try {
			// Bag item
			cart.addPLUCodedProductToCart(plu.PLU_PRODUCT_DATABASE.get(new PriceLookupCode("12")), null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(2);

		// Payment
		Assert.assertTrue(cardPayment.PayWithCreditCard(creditCard, 0, cart.getCumPrice(), "9876", Bank)); //make sure paid

		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
	}
	
	//Tests for if a customer decides not to put the item in the bagging area 
	@ Test
	public void ItemNotBagged() {
		cart.clearCart();
		System.out.println();

		// Set own bags
		baggingArea.setOwnBag(false);
		

		// Scan items
		scanner.scanItem(meatItem);
		Assert.assertEquals("Meat: $10\n", scanner.getProductArray());

		try {
			// Bag item
			baggingArea.AddItemNoBag(meatItem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertEquals(baggingArea.predictedWeightOnBagging, 0.0, 0);

		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(0);
		
		Assert.assertEquals(10.0, cart.getCumPrice().doubleValue(), 0); //check current total
		
		// Payment
		Assert.assertTrue(cardPayment.PayWithDebitCard(debitCard, 1, cart.getCumPrice(), "1234", Bank));		

		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
		}
	
	//Tests removing an item from the cart and then adding an item afterwards
	@ Test
	public void RemoveFromCart() throws Exception {
		cart.clearCart();
		System.out.println();

		// Set own bags
		baggingArea.setOwnBag(false);

		// Scan items
		scanner.scanItem(meatItem);
		Assert.assertEquals("Meat: $10\n", scanner.getProductArray());

		try {
			// Bag item
			baggingArea.AddItem(meatItem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cart.removeBarcodedProductFromCart(meat);
		baggingArea.RemoveItem(meatItem);
		Assert.assertEquals(baggingArea.predictedWeightOnBagging, 0.0, 0);

		// Scan items
		scanner.scanItem(cheeseItem);

		try {
			// Bag item
			baggingArea.AddItem(cheeseItem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertEquals(baggingArea.predictedWeightOnBagging, 5.00, 0); //check scale weight
		
		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(1);


		Assert.assertEquals(5.1, cart.getCumPrice().doubleValue(), 0); //check current total

		// Payment
		Assert.assertTrue(cardPayment.PayWithDebitCard(debitCard, 1, cart.getCumPrice(), "1234", Bank));		

		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
	}
}
