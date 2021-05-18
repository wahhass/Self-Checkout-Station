package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import javax.swing.DefaultListModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import CODE.Bag;
import CODE.Cart;
import CODE.EmptyBanknote;
import CODE.EmptyCoin;
import CODE.EnterPLUCode;
import CODE.GUI;
import CODE.GiveChange;
import CODE.LoginToControl;
import CODE.MemberCard;
import CODE.PayWithCard;
import CODE.PayWithCoin;
import CODE.PayWithNote;
import CODE.PrintReceipt;
import CODE.itemInBaggingAreaV2;
import CODE.productScan;




public class SystemTestingSingleItemMember {
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
	public static GiveChange giveChangeSoftware;
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
	
	Barcode barcode1;
	Barcode barcode2;
	Barcode barcode3;
	Barcode barcode4;
	
	BarcodedProduct cheese;
	BarcodedProduct milk;
	BarcodedProduct meat;
	BarcodedProduct eggInvalid;
	
	Card debitCard;
	Card creditCard;
	Card notIssuedDebit;
	Card notIssuedCredit;
	Card giftCard;
	Card notIssuedGiftCard;
	
	CardIssuer Bank;
	CardIssuer Coop;
	
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
	
	Banknote fiveDollar;
	Banknote tenDollar;
	Banknote twentyDollar;
	
	int five;
	int ten;
	int twenty;
	int fifty;
	int hundred;
	int[] noteDenominations;
	
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
	
	private static DecimalFormat df = new DecimalFormat("0.00");

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

		baggingArea.removePurchasedItems(); //reset for future tests

		expiry.add(Calendar.MONTH, 2);
		expiry.add(Calendar.YEAR, 2);
		staffLogin.addToDatabase("1234", "Sammy");
		GUI.populateBarcodeDatabase(scanner);
		plu.addToPLUDatabase();

		baggingArea.removePurchasedItems();
		cart.clearCart();
		
		
		// Item and product initialization

		cheese = new BarcodedProduct(new Barcode("1111"), "Cheese", new BigDecimal(5.00));

		milk = new BarcodedProduct(new Barcode("2222"), "Milk", new BigDecimal(4.50));

		meat = new BarcodedProduct(new Barcode("3333"), "Meat", new BigDecimal(10.00));

		eggInvalid = new BarcodedProduct(new Barcode("4444"), "", new BigDecimal(2.00));

		barcode1 = new Barcode("1111");
		cheeseItem = new BarcodedItem(barcode1, 5.00);

		barcode2 = new Barcode("2222");
		milkItem = new BarcodedItem(barcode2, 4.50);

		barcode3 = new Barcode("3333");
		meatItem = new BarcodedItem(barcode3, 10.00);

		barcode4 = new Barcode("4444");
		eggItem = new BarcodedItem(barcode4, 10.00);

		scanner.addToDatabase(barcode1, cheese);
		scanner.addToDatabase(barcode2, milk);
		scanner.addToDatabase(barcode3, meat);
		scanner.addToDatabase(barcode4, eggInvalid);

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
		
		//initializing cash and coins
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
		
		fiveDollar = new Banknote(5, CDN);
		tenDollar = new Banknote(10, CDN);
		twentyDollar = new Banknote(20, CDN);
		
		//give change
		giveChangeSoftware = new GiveChange(CAD, true, noteDenominations, coinDenominations);
		giveChangeSoftware.refillCoinDispenser(80);
		giveChangeSoftware.refillBanknoteDispensers(80);
		
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

	}
	
	
	//Tests for a member scanning a single item and paying with cash and coin
	@ Test
	public void MemberCashAndCoin() throws Exception{
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
		baggingArea.addNumberPlasticBagsUsed(2);
		
		//member info 
		boolean memberBool = member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200));
		Assert.assertTrue(memberBool); //make sure membership valid 
		
		
		Assert.assertEquals(5.2, cart.getCumPrice().doubleValue(), 0); //check current total
		
		// Payment
		// Payment with coin
		coinPayment.payCoin(dimeCoin);
		coinPayment.payCoin(dimeCoin);
		
		Assert.assertEquals(new BigDecimal("0.20"), coinPayment.getTotalCoinInserted()); //coins inserted properly

		//Payment with banknote
		notePayment.payNote(fiveDollar);
		
		Assert.assertEquals(5, notePayment.getTotalCashInserted()); //cash inserted properly
		
		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
		}
	
	//Tests for a member scanning a single item and paying with cash and coin, gets change
	@ Test
	public void MemberCashAndCoinWithChange() throws Exception{
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
		baggingArea.addNumberPlasticBagsUsed(2);
		
		//member info 
		boolean memberBool = member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200));
		Assert.assertTrue(memberBool); //make sure membership valid 
		
		
		Assert.assertEquals(5.2, cart.getCumPrice().doubleValue(), 0); //check current total
		
		// Payment
		// Payment with coin
		coinPayment.payCoin(dimeCoin);
		coinPayment.payCoin(dimeCoin);
		
		Assert.assertEquals(new BigDecimal("0.20"), coinPayment.getTotalCoinInserted()); //coins inserted properly

		//Payment with banknote
		notePayment.payNote(tenDollar);
		
		Assert.assertEquals(10, notePayment.getTotalCashInserted()); //cash inserted properly
		
		//give $5 change
		giveChangeSoftware.returnChange(new BigDecimal(5));
		
		Assert.assertEquals(true, giveChangeSoftware.isChangeDispensed());
		
		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
		}
	
	//Tests for a member scanning a single item and paying with a debit card
	@ Test
	public void MemberDebitCard() {
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
		baggingArea.addNumberPlasticBagsUsed(2);
		
		//member info 
		boolean memberBool = member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200));
		Assert.assertTrue(memberBool); //make sure membership valid 
		
		
		Assert.assertEquals(5.2, cart.getCumPrice().doubleValue(), 0); //check current total
		
		// Payment
		Assert.assertTrue(cardPayment.PayWithDebitCard(debitCard, 1, cart.getCumPrice(), "1234", Bank)); //make sure paid		

		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
		}
	
	//Tests for a member looking up the PLU for a single item and paying with cash and coin using their own bag 
	@ Test
	public void MemberOwnBagCashAndCoin() throws Exception {
		cart.clearCart();
		System.out.println();
		
		// Set own bags
		baggingArea.setOwnBag(true);
		try {
			baggingArea.placeOwnBag(reusableBag);
		} catch (OverloadException e1) {
			e1.printStackTrace();
		}
		
		// Enter PLU items
		plu.enterPLUItem(new PriceLookupCode("4205")); //enter the PLU for an apple
				
		try {
		// Bag item
		cart.addPLUCodedProductToCart(plu.PLU_PRODUCT_DATABASE.get(new PriceLookupCode("4205")), null);
		} catch (Exception e) {
		e.printStackTrace();
		}
					
		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(0);
		
		//member info 
		boolean memberBool = member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200));
		Assert.assertTrue(memberBool); //make sure membership valid 
		

		// Payment with coin
		coinPayment.payCoin(dimeCoin);
		coinPayment.payCoin(dimeCoin);
		coinPayment.payCoin(dimeCoin);
				
		Assert.assertEquals(new BigDecimal("0.30"), coinPayment.getTotalCoinInserted()); //coins inserted properly

		//Payment with banknote
		notePayment.payNote(fiveDollar);
			
		Assert.assertEquals(5, notePayment.getTotalCashInserted()); //cash inserted properly

		BigDecimal value = cart.getCumPrice().subtract(new BigDecimal(5.30));
		
		//give change 
		giveChangeSoftware.returnChange(value);

		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
	}
	
	
	//Tests for a member scanning a single item and paying with a debit card using their own bag 
	@ Test
	public void MemberOwnBagDebit() {
		cart.clearCart();
		System.out.println();
		
		// Set own bags
		baggingArea.setOwnBag(true);
		try {
			baggingArea.placeOwnBag(reusableBag);
		} catch (OverloadException e1) {
			e1.printStackTrace();
		}
		
		// Scan items
		scanner.scanItem(meatItem);
		Assert.assertEquals("Meat: $10\n", scanner.getProductArray());		
		
		try {
			// Bag item
			baggingArea.AddItem(meatItem);
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		Assert.assertEquals(baggingArea.predictedWeightOnBagging, 19.3, 0); //check scale weight
		
		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(0);
		
		//member info 
		boolean memberBool = member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200));
		Assert.assertTrue(memberBool); //make sure membership valid 
		
		Assert.assertEquals(10.0, cart.getCumPrice().doubleValue(), 0); //check current total
		
		// Payment
		Assert.assertTrue(cardPayment.PayWithDebitCard(debitCard, 1, cart.getCumPrice(), "1234", Bank)); //make sure paid	
		
		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
		}
	
	//Tests for a member looking up the PLU for a single item and paying with a credit card using their own bag 
	@ Test
	public void MemberOwnBagCredit() throws Exception {
		cart.clearCart();
		System.out.println();
		
		// Set own bags
		baggingArea.setOwnBag(true);
		try {
			baggingArea.placeOwnBag(reusableBag);
		} catch (OverloadException e1) {
			e1.printStackTrace();
		}
		
		// Enter PLU items
		plu.enterPLUItem(new PriceLookupCode("4205")); //enter the PLU for an apple
				
		try {
		// Bag item
		cart.addPLUCodedProductToCart(plu.PLU_PRODUCT_DATABASE.get(new PriceLookupCode("4205")), null);
		} catch (Exception e) {
		e.printStackTrace();
		}
				
		// Select number of bags used
		baggingArea.addNumberPlasticBagsUsed(0);
		
		//member info 
		boolean memberBool = member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200));
		Assert.assertTrue(memberBool); //make sure membership valid 
		
		
		// Payment
		Assert.assertTrue(cardPayment.PayWithCreditCard(creditCard, 0, cart.getCumPrice(), "9876", Bank)); //make sure paid

		// Receipt
		System.out.print(cart.printCart());
		receiptPrinter.print(cart);
		System.out.print(receiptPrinter.removePrintedreceipt());
		}
	
    @Test
    public void MemberKeyItemBagCreditCard() throws Exception {

// Set own bags

        baggingArea.setOwnBag(false);

// Scan items

        plu.enterPLUItem(new PriceLookupCode("4205"));

        double productPrice = 4.30;

        double predictedPrice = productPrice * (plu.getPLUItemWeight() * 0.001);

        String formattedPrice = df.format(predictedPrice);

        System.out.println(scanner.getProductArray());

        Assert.assertEquals("Apples: $" + formattedPrice + "\n", scanner.getProductArray());

        PLUCodedItem itemForBag = new PLUCodedItem(new PriceLookupCode("4205"), plu.getPLUItemWeight());

        System.out.println(baggingArea.predictedWeightOnBagging);
        System.out.println("yea");

        try {
// Bag item
            baggingArea.AddItem(itemForBag);
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        Assert.assertEquals(baggingArea.predictedWeightOnBagging, itemForBag.getWeight(), 0);

// Select number of bags used

        baggingArea.addNumberPlasticBagsUsed(1);

        Assert.assertEquals(predictedPrice + 0.10, cart.getCumPrice().doubleValue(), 0.01);

// Payment

        Assert.assertTrue(member.useMemberCard(memberCard, cart.getCumPrice()));

        Assert.assertTrue(cardPayment.PayWithCreditCard(creditCard, 0, cart.getCumPrice(), "9876", Bank));

// Change

// Receipt

        receiptPrinter.print(cart);

        String actualReceiptOutput = receiptPrinter.removePrintedreceipt();

        System.out.println(actualReceiptOutput);

    }
    
    @Test


    public
    void
    MemberScanItemBagCreditCard()
            throws
            Exception
    {

// Set own bags

        baggingArea.setOwnBag(false);

// Scan items

        scanner.scanItem(milkItem);

        Assert.assertEquals("Milk: $4.5\n", scanner.getProductArray());

        try {
// Bag item
            baggingArea.AddItem(milkItem);
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        Assert.assertEquals(baggingArea.predictedWeightOnBagging, 4.50, 0);

// Select number of bags used

        baggingArea.addNumberPlasticBagsUsed(1);

        Assert.assertEquals(4.60, cart.getCumPrice().doubleValue(), 0);

// Payment

        Assert.assertTrue(member.useMemberCard(memberCard, cart.getCumPrice()));

        Assert.assertTrue(cardPayment.PayWithGiftCard(giftCard, cart.getCumPrice(), Coop));

        receiptPrinter.print(cart);

        String
                actualReceiptOutput = receiptPrinter.removePrintedreceipt();

        System.out.println(actualReceiptOutput);

    }
	
}
