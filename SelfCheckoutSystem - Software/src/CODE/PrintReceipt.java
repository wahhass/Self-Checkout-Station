package CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ReceiptPrinterListener;

/** 
 * Control software which codes for the receipt printer 
 * @author Jaydin Lee and Yousaf Zahir
 */

public class PrintReceipt{
	
	/**
	 * printer: the main printer being used
	 * printerListener: the main printer listener being used
	 * cart: cart that contains all the products added during checkout 
	 * 	(contains description and prices of item, and the cumulative price of all items in cart)
	 * cartP: ArrayList that contains the cart's product description and price
	 * inkLevel: keeps track of the the amount of ink in the printer
	 * paperLevel: keeps track of the the amount of paper in the printer
	 * isLowPaper: boolean of printer is low on paper
	 * isLowInk: boolean of printer is low on ink
	 * MAXIMUM_INK: max ink printer allows
	 * MAXIMUM_PAPER: max paper printer allows
	 */
	
	public ReceiptPrinter printer;
	public ReceiptPrinterListenerInterface printerListener;
	public Cart cart;
	public ArrayList<CartProduct> cartP; 
	private int inkLevel;
	private int paperLevel;
	private boolean isLowPaper;
	private boolean isLowInk;
	private static final int MAXIMUM_INK = 1 << 20;
	private static final int MAXIMUM_PAPER = 1 << 10;

	/**
	 * Default constructor 
	 */
	public PrintReceipt() {
		printer = new ReceiptPrinter();
		printerListener = new ReceiptPrinterListenerInterface();
		printer.register(printerListener);
		printer.enable();
	}
	
	/**
	 * Constructor for GUI
	 * @param printer
	 */
	public PrintReceipt(ReceiptPrinter printer) {
		this.printer = printer;
		printerListener = new ReceiptPrinterListenerInterface();
		printer.register(printerListener);
		printer.enable();
	}
	
	/**
	 * Prints the customer's receipt.
	 * Takes the list of product description, price, and the total price from the Cart, 
	 * and then prints out the receipt and cuts it.
	 * 
	 * @param cart
	 */
	public void print(Cart cart) {
		this.cart = cart;
		
		detectLowOnInk(inkLevel);
		detectLowOnPaper(paperLevel);
		
		try {
			cartP = new ArrayList<CartProduct>();
			cartP = cart.returnCart();
			
			// print receipt header
			String receiptHeader = String.format("%32s\n%s\n%-4s%56s\n%-4s%56s", "CO-OP", 
			"************************************************************",
			"Item", "Price", "----", "-----");
			for (char c : receiptHeader.toCharArray()) {printer.print(c); inkLevel--;};
			
			printer.print('\n');
			paperLevel--;
			
			// print receipt body (item & item price)
			int i = 0;
				do {
					String description = "" + cartP.get(i).name;			
					String price = "$" + cartP.get(i).price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					if(!((description.length() + price.length()) > 60)) {
						String columnFormatString = "%-" + (60-price.length()) + "s" + "%s";
						String itemAndPrice = String.format(columnFormatString, description, price);
						for(char c : itemAndPrice.toCharArray()) {printer.print(c); inkLevel--;}
						
						printer.print('\n');
						paperLevel--;
					} else {
						
						throw new SimulationException("The line is too long!");
					}
					i++;
				} while (i < cartP.size());
			
			// printer receipt footer
			String footer = String.format("%s\n%60s\n%60s\n", 
					"************************************************************", "Total", "-----");
			
			for(char c : footer.toCharArray()) {printer.print(c); inkLevel--;}
			
			
			// print total price
			String totalCost = String.format("%60s", "$" + cart.getCumPrice());
			for(char c : totalCost.toCharArray()) {printer.print(c); inkLevel--;}
			
			// simulate cutting the paper (end of receipt)
			
			printer.cutPaper();
			
			detectLowOnInk(inkLevel);
			detectLowOnPaper(paperLevel);
				
		} catch (SimulationException se) {
			//out of ink/paper.
			if(getIsOutOfPaper() == true) {
				System.out.println("The printer is out of paper!");
			}
			else if(getIsOutOfInk() == true) {
				System.out.println("The printer is out of ink!");
			}
			
			System.out.print(se.getMessage());
			throw new SimulationException(se.getMessage());
		}
	}
	
	/**
	 * Simulates the customer removing the receipt.
	 * 
	 * @return receipt (string)
	 */
	public String removePrintedreceipt() {
		return printer.removeReceipt();
	}
	
	/**
	 * station detects receipt printer is low on ink
	 * 
	 * @param inkLevel
	 */
	public void detectLowOnInk(int inkLevel) {
		int tenPercentInk = MAXIMUM_INK/10;
		
		if (inkLevel < tenPercentInk) {
			this.isLowInk = true; 
			System.out.println("Low on ink.");
		}
		
	}
	
	/**
	 *  station detects receipt printer is low on paper
	 * 
	 * 	@param paperLevel
	 */
	public void detectLowOnPaper(int paperLevel) {
		int tenPercentPaper = MAXIMUM_PAPER/10;
		
		if (paperLevel < tenPercentPaper) {
			this.isLowPaper = true; 
			System.out.println("Low on paper.");
		}
	}
	
	// Add ink to print receipt
	public void addingInk(int ink) {
		printer.addInk(ink);
		promptInkAdded(ink);
	}
	
	// Add paper to print receipt
	public void addingPaper(int paper) {
		printer.addPaper(paper);
		promptPaperAdded(paper);
	}
	/**
	 * 	station prompts attendee to inform how much paper was added to machine
	 * 		functionality completed through GUI
	 * @param paperAdded
	 */
	public void promptPaperAdded(int paperAdded) {
		if(getPaperAdding() == true) {
			this.paperLevel += paperAdded;
			setPaperAdding(true); 
		}
	}
	
	/**
	 * 	station prompts attendee to inform how much ink was added to machine
	 * 		functionality completed through GUI
	 * 	@param inkAdded
	 */
	public void promptInkAdded(int inkAdded) {
		if(getInkAdding() == true) {
			this.inkLevel += inkAdded;
			setInkAdding(false); 
		}
	}
	
	/*
	 * setters and getters
	 */
	public void setPaperLevel(int paperLevel) {
		this.paperLevel = paperLevel;
	}

	public void setInkLevel(int inkLevel) {
		this.inkLevel = inkLevel;
	}
	
	public int getPaperLevel() {
		return this.paperLevel;
	}

	public int getInkLevel() {
		return this.inkLevel;
	}
	
	public boolean getIsLowPaper() {
		return this.isLowPaper;
	}
	
	public boolean getIsLowInk() {
		return this.isLowInk;
	}
	
	public boolean getIsOutOfPaper() {
		return this.printerListener.outOfPaperFlag;
	}

	public boolean getIsOutOfInk() {
		return this.printerListener.outOfInkFlag;
	}
	
	public boolean getInkAdding() {
		return this.printerListener.inkAdding;
	}
	
	public void setInkAdding(boolean state) {
		this.printerListener.inkAdding = state;
	}
	
	public boolean getPaperAdding() {
		return this.printerListener.paperAdding;
	}
	
	public void setPaperAdding(boolean state) {
		this.printerListener.paperAdding = state;
	}
	
	class ReceiptPrinterListenerInterface implements ReceiptPrinterListener{
		
		boolean outOfPaperFlag = false;
		boolean outOfInkFlag = false;
		
		boolean paperAdding = false;
		boolean inkAdding = false;
		
		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void outOfPaper(ReceiptPrinter printer) {
			outOfPaperFlag = true;
		}

		@Override
		public void outOfInk(ReceiptPrinter printer) {
			outOfInkFlag = true;
		}

		@Override
		public void paperAdded(ReceiptPrinter printer) {
			outOfPaperFlag = false;
			paperAdding = true;
			
		}

		@Override
		public void inkAdded(ReceiptPrinter printer) {
			outOfInkFlag = false;
			inkAdding = true;
		}
	}
	
}