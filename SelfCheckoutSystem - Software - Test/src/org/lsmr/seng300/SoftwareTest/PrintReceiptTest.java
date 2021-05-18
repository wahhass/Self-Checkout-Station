package org.lsmr.seng300.SoftwareTest;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import CODE.Cart;
import CODE.PrintReceipt;

/**
 * Unit testing for PriceReceipt Class
 * 
 * @author Jaydin Lee and Yousaf Zahir
 *
 */

public class PrintReceiptTest {

	PrintReceipt printReceipt;
	BarcodedProduct bread;
	PLUCodedProduct milk;
	BarcodedProduct eggs;
	Cart cart;
	BigDecimal totalPrice = BigDecimal.ZERO;
	
	private static final int MAXIMUM_INK = 1 << 20;
	private static final int MAXIMUM_PAPER = 1 << 10;
	
	@Before
	public void setUp() throws Exception {
		bread = new BarcodedProduct(new Barcode("11111"), "Bread", new BigDecimal("2.00"));
		milk = new PLUCodedProduct(new PriceLookupCode("22222"), "Milk", new BigDecimal("4.50"));
		eggs = new BarcodedProduct(new Barcode("33333"), "Eggs", new BigDecimal("3.00"));
		
		cart = new Cart();
		
		cart.addBarcodedProductToCart(bread);
		
		cart.addPLUCodedProductToCart(milk, new BigDecimal(9.00));
		cart.addBarcodedProductToCart(eggs);
		
		printReceipt = new PrintReceipt();
		
	}

	// test for GUI constructor
	@Test
	public void testPrintReceiptConstructor() {
		ReceiptPrinter testPrinter = new ReceiptPrinter(); 
		PrintReceipt printReceiptConstructor = new PrintReceipt(testPrinter);
		
		printReceiptConstructor.addingInk(1000);
		Assert.assertEquals(printReceiptConstructor.getInkLevel(), 1000);
	}
	
	// test if the cart is empty
	@Test
	public void testProductArrayIsEmpty() {
		Cart cartEmpty = new Cart();
		try {
			printReceipt.print(cartEmpty);
			Assert.fail("Expected: Cart is empty, SimulationException.");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof SimulationException);
		}
	}

	// test if the printer's output is 'printed' correctly
	@Test
	public void testPrintReceiptOutput() {
		printReceipt.printer.addPaper(MAXIMUM_PAPER);
		printReceipt.printer.addInk(MAXIMUM_INK);
		printReceipt.setPaperLevel(MAXIMUM_PAPER);
		printReceipt.setInkLevel(MAXIMUM_INK);
		
		String expectedReceiptOutput = String.format(
				"%32s\n%s\n%-4s%56s\n%-4s%56s\n%-55s%s\n%-55s%s\n%-55s%s\n%s\n%60s\n%60s\n%60s", 
				"CO-OP", 
				"************************************************************",
				"Item", "Price", "----", "-----",
				"Bread", "$2.00", "Milk", "$9.00", "Eggs", "$3.00",
				"************************************************************", 
				"Total", "-----", "$14.00");
		printReceipt.print(cart);
		String actualReceiptOutput = printReceipt.removePrintedreceipt();
		
		Assert.assertEquals(true, expectedReceiptOutput.equals(actualReceiptOutput));
	}
	
	// test if system detects low on paper
	@Test
	public void testLowOnPaper() {
		printReceipt.printer.addPaper(MAXIMUM_PAPER/11);
		printReceipt.setPaperLevel(MAXIMUM_PAPER/11);
		printReceipt.printer.addInk(MAXIMUM_INK);
		printReceipt.setInkLevel(MAXIMUM_INK);
		
		printReceipt.print(cart);
		
		Assert.assertEquals(true, printReceipt.getIsLowPaper());
		
	}
	
	// test if system detects low on ink
	@Test
	public void testLowOnInk() {
		printReceipt.printer.addPaper(MAXIMUM_PAPER);
		printReceipt.setPaperLevel(MAXIMUM_PAPER);
		printReceipt.printer.addInk(MAXIMUM_INK/11);
		printReceipt.setInkLevel(MAXIMUM_INK/11);
		
		printReceipt.print(cart);
		
		Assert.assertEquals(true, printReceipt.getIsLowInk());
	}
	
	// tests if printer is out of paper
	@Test
	public void testRunOutOfPaper() {
		printReceipt.printer.addPaper(1);
		printReceipt.setPaperLevel(1);
		printReceipt.printer.addInk(MAXIMUM_INK);
		printReceipt.setInkLevel(MAXIMUM_INK);
		
		try {
			printReceipt.print(cart);
			Assert.fail("Expected: no paper, ran out");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof SimulationException);
			Assert.assertEquals(true, printReceipt.getIsOutOfPaper());
		}
		
	}
	
	// tests if printer is out of ink
	@Test
	public void testRunOutOfInk() {
		printReceipt.printer.addPaper(MAXIMUM_PAPER);
		printReceipt.setPaperLevel(MAXIMUM_PAPER);
		printReceipt.printer.addInk(1);
		printReceipt.setInkLevel(1);
		
		try {
			printReceipt.print(cart);
			Assert.fail("Expected: no ink, ran out");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof SimulationException);
			Assert.assertEquals(true, printReceipt.getIsOutOfInk());
		}
	}
	
	// tests if line is too long (over 60 characters) throws SimulationException
	@Test 
	public void testTooManyLines() {
		Cart cartProductLongDescription = new Cart();
		BarcodedProduct longDescProduct = new BarcodedProduct(new Barcode("33333"), 
				"*****************************************************************", new BigDecimal("3.00"));
		cartProductLongDescription.addBarcodedProductToCart(longDescProduct);
		
		printReceipt.printer.addPaper(MAXIMUM_PAPER);
		printReceipt.printer.addInk(MAXIMUM_INK);
		printReceipt.setPaperLevel(MAXIMUM_PAPER);
		printReceipt.setInkLevel(MAXIMUM_INK);
		
		try {
			printReceipt.print(cartProductLongDescription);
			Assert.fail("Expected: SimulationException line too long.");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	// test whether paper is added to system
	@Test
	public void testPaperPrompt() {
		int startingPaperInSystem = printReceipt.getPaperLevel();
		int paperToAdd = 2;
		printReceipt.printer.addPaper(paperToAdd);
		printReceipt.promptPaperAdded(paperToAdd);
		
		Assert.assertEquals(printReceipt.getPaperLevel(), startingPaperInSystem + paperToAdd);
	}
	
	// test whether ink is added to system
	@Test
	public void testInkPrompt() {
		int startingInkInSystem = printReceipt.getInkLevel();
		int inkToAdd = 3;
		printReceipt.printer.addInk(inkToAdd);
		printReceipt.promptInkAdded(inkToAdd);
		
		Assert.assertEquals(printReceipt.getInkLevel(), startingInkInSystem + inkToAdd);
	}
	
	// test whether paper in system is changed when no paper is added
	@Test
	public void testNoPaperAdded() {
		int startingPaperInSystem = printReceipt.getPaperLevel();
		printReceipt.promptPaperAdded(3);
		
		Assert.assertEquals(printReceipt.getPaperLevel(), startingPaperInSystem);
	}
	
	// test whether ink in system is changed when no ink is added
	@Test
	public void testNoInkAdded() {
		int startingInkInSystem = printReceipt.getInkLevel();
		printReceipt.promptInkAdded(3);
		
		Assert.assertEquals(printReceipt.getInkLevel(), startingInkInSystem);
		
	}
	
	// test ink added using software
	@Test
	public void testInkAdded() {
		printReceipt.addingInk(1000);
		Assert.assertEquals(printReceipt.getInkLevel(), 1000);
	}
	
	// test paper added using software
	@Test
	public void testPaperAdded() {
		printReceipt.addingPaper(1000);
		Assert.assertEquals(printReceipt.getPaperLevel(), 1000);
	}
}
