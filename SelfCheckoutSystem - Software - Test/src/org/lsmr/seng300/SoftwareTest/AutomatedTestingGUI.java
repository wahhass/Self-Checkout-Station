package org.lsmr.seng300.SoftwareTest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import CODE.Cart;
import CODE.EmptyBanknote;
import CODE.EmptyCoin;
import CODE.EnterPLUCode;
import CODE.GUI;
import CODE.LoginToControl;
import CODE.PayWithCard;
import CODE.PayWithCoin;
import CODE.PayWithNote;
import CODE.PrintReceipt;
import CODE.itemInBaggingAreaV2;
import CODE.productScan;
import junit.framework.Assert;


/**
 * README: 
 * 
 * this class is the skeleton class for how we wanted to implemented automated 
 * GUI system testing. As we were testing the buttons, we realized that some buttons worked, 
 * while other buttons didn't work. We spent time and could not find the reason why 
 * some buttons worked and why some buttons didn't work. 
 * 
 * At the last minute, we had to scrap this way of System testing and transitioned into simulating 
 * the methods that would be called/used by the GUI during the checkout process. While not ideal, 
 * this is the a way to automate the GUI logic.  
 * 
 * The current state of the code executes without any problems, but can't be used to test anything 
 * meaningful. Removing the comments for the commented out section will show some of the buttons 
 * that lead to an error that we could not solve. 
 */


public class AutomatedTestingGUI {
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
	
	
	//Nearly identical from GUI.java's main method
	@Before 
	public void setUp() {
		SelfCheckoutStation selfCheckout = new SelfCheckoutStation(CAD, banknoteDenominations, coinDenominations, 1000, 1);
		baggingArea = new itemInBaggingAreaV2(selfCheckout, cart, 100000000, 1);
		scanner = new productScan(cart);
		plu = new EnterPLUCode(selfCheckout, cart, baggingArea);
		cardPayment = new PayWithCard();
		coinPayment = new PayWithCoin(selfCheckout);
		notePayment = new PayWithNote(selfCheckout);
		receiptPrinter = new PrintReceipt(selfCheckout.printer);
		staffLogin = new LoginToControl();
		emptyNotes = new EmptyBanknote(selfCheckout.banknoteStorage);
		emptyCoins = new EmptyCoin(selfCheckout.coinStorage);
		
		receiptPrinter.printer.addPaper(9);
		receiptPrinter.printer.addInk(19);
		expiry.add(Calendar.MONTH, 2);
		expiry.add(Calendar.YEAR, 2);
		staffLogin.addToDatabase("1234", "Sammy");
		GUI.populateBarcodeDatabase(scanner);
		plu.addToPLUDatabase();
		
		new GUI(selfCheckout);
	}
	
	/**
	 * Can start writing test cases here. 
	 * 
	 * Make sure you know which buttons/text boxes on the GUI you need to use
	 * 
	 * For the buttons, to simulate clicking the button automatically: 
	 * use: GUI.BUTTON_NAME.doClick(); 
	 * 
	 * For entering an input into the text boxes automatically: 
	 * use: GUI.TEXT_BOX.setText("Value To Enter");
	 * 
	 * Asserts can be placed through the code where appropriate. 
	 * @throws InterruptedException 
	 */
	
	@Test 
	public void TestRun() throws InterruptedException {
		GUI.startButton.doClick();
		Thread.sleep(1000);
		
		//comment out to see error
		/*
		GUI.scanButton.doClick();
		Thread.sleep(1000);
		GUI.bagItemButton.doClick();
		Thread.sleep(1000);
		*/
		
		//comment out to see error
		/*
		GUI.lookUpItemButton.doClick(); //works
		Thread.sleep(1000);
		GUI.apples.doClick(); //but these product buttons don't work 
		Thread.sleep(1000);
		GUI.lemons.doClick();
		Thread.sleep(1000);
		*/
		
		//comment out to see error
		/*
		GUI.keyInCodeButton.doClick(); //works
		Thread.sleep(1000);
		GUI.itemCodeField.setText("4053"); //works 
		Thread.sleep(5000);
		GUI.itemCodeEnterButton.doClick();//stop working here
		Thread.sleep(1000);
		*/
		
		//comment out to see error
		/*
		GUI.scanHelpButton.doClick(); //button doesn't work 
		Thread.sleep(1000);
		GUI.attendantLoginField.setText("1234");
		Thread.sleep(1000);
		GUI.attendantControlGoBackButton.doClick();
		Thread.sleep(1000);
		*/
		
		GUI.finishAndPayButton.doClick();
		Thread.sleep(1000);
		GUI.numberOfBagsField.setText("0");
		Thread.sleep(1000);
		GUI.bagEnterButton.doClick();
		Thread.sleep(1000);
		GUI.nonMemberButton.doClick();
		Thread.sleep(1000);

		//comment out to see error
		/*
		GUI.cashButton.doClick(); //button doesn't work
		Thread.sleep(1000);
		GUI.nickel.doClick(); 
		Thread.sleep(1000);
		GUI.nickel.doClick(); 
		Thread.sleep(1000);
		*/
	}
}
