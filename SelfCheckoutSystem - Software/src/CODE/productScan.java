package CODE;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.products.BarcodedProduct;



/**
 * Control software which codes for the 
 * ability to scan items with the self
 * checkout machine hardware
 * Version 2.0 update: Finish Adding Items
 * Customer can view total and description
 * of items added
 * @author Abinaya Subramanian and Hailey Allen and Vivian Huynh
 */
public class productScan {
	/**
	 * scanner : the main barcode scanning machine used
	 * listener : the main barcode scanning machine's listener
	 * BARCODED_PRODUCT_DATABASE : database of all products
	 * finish : boolean to determine if user is still scanning
	 */
	BarcodeScanner scanner;
	BarcodeScannerListenerInterface listener;
	public static final Map<Barcode, BarcodedProduct> BARCODED_PRODUCT_DATABASE = new HashMap<>(); //stub
	Cart cart = new Cart();
	
	boolean finish = false;
	LoginToControl loginToControl = new LoginToControl();
	boolean attendantLogin = loginToControl.isAttendantLoggedIn();
	
	public productScan(Cart cart) {
			
		this.cart = cart;
		scanner = new BarcodeScanner();
		listener = new BarcodeScannerListenerInterface();
		scanner.register(listener);

	}

	/**
	 * Trying to scan the item and find it in the database
	 * @param bar_item
	 * @return boolean (if the product is in the database or not)
	 */
	public boolean isProductInSystem(BarcodedItem bar_item) {
		try {
			scanner.scan(bar_item); //non-barcoded items will be silently ignored
			
			 if(BARCODED_PRODUCT_DATABASE.containsKey(bar_item.getBarcode()) == true) {
				return true;
			} else {
				System.out.println("Item not found!");
				return false;
			}
		} catch (SimulationException s){
			System.out.println("No item scanned!");
			s.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Adds the product(s) to the product database
	 * @param barcode
	 * @param product
	 */
	public void addToDatabase(Barcode barcode, BarcodedProduct product) {
		if(barcode == null) {
			throw new NullPointerException("Barcode is null");
		} else if (product == null) {
			throw new NullPointerException("Product is null");
		} else {
			BARCODED_PRODUCT_DATABASE.put(barcode, product); //add to database
		}
	}
	
	
	
	/**
	 * Getter method for the counter
	 * which shows how many items
	 * have been scanned
	 * @return int (number of items)
	 */
	public int getCounter() {
		return listener.counter;
	}
	
	/**
	 * Used for scanning an item,
	 * determining they are a real product,
	 * and adding them to the cart
	 * @param bar_item
	 */
	public void scanItem(BarcodedItem bar_item){
		
		boolean itemScanned = isProductInSystem(bar_item);
		Barcode itemBarcode = listener.getBarcode();

		if (itemScanned == true && itemBarcode == bar_item.getBarcode()) {
			BarcodedProduct product =  BARCODED_PRODUCT_DATABASE.get(itemBarcode); //determining it is a real product
			cart.addBarcodedProductToCart(product); //calls into Cart class to add to arraylist
		}
		else {
			System.out.println("Item not scanned!");
		}
			
		return;
		
	}
	
	/**
	 * Customer is finished adding items and needs to view total
	 * and item list before payment.
	 * @param productArray
	 */
	public void finishAdding(ArrayList<CartProduct> productArray) {
		BigDecimal total = cart.getCumPrice();
		String list = cart.printCart();

		if (total.doubleValue() > 0 && productArray.size()>0) {	
			System.out.println(list);
			System.out.println("Total: "+total); //print total amount
			finish = true; 					
			scanner.disable(); 					//scanner off
		} else {
			System.out.println("No Products Checked Out");
			finish = false;
		}
		
	}
	
	/**
	 * Returns true if the customer
	 * is complete their purchase
	 * @return finish
	 */
	public boolean isFinish() {
		return finish = finish;
	}

	/**
	 * The user can basically 'set' if
	 * they are finished shopping
	 * @param finish
	 */
	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	/**
	 * Calls into Cart to PRINT the 
	 * arraylist of products the user 
	 * scanned
	 * @return cart
	 */
	public String getProductArray() {
		// TODO Auto-generated method stub
		return cart.printCart();
	}

	/**
	 * Calls into Cart to return the 
	 * cumulative price 
	 * @return cumPrice
	 */
	public BigDecimal getCumPrice() {
		// TODO Auto-generated method stub
		return cart.getCumPrice();
	}

	/**
	 * Calls into the cart class to
	 * RETURN the string holding the
	 * cart products
	 * @return cart
	 */
	public ArrayList<CartProduct> isArray() {
		// TODO Auto-generated method stub
		return cart.returnCart();
	}
	
	/**
	 * Attendant can remove product from productArray 
	 * and the total price is updated
	 * @param CartProduct
	 */
	public void removeItem(CartProduct cart_prod){
		if (attendantLogin == false) {
			throw new SimulationException("Attendant Must Login To Remove Item From Cart");
		}  
		cart.removeFromCart(cart_prod);
	}
	
	/**
	 * Sets attendantLogin as true
	 */
	public void setTrueLoggedIn() {
		attendantLogin = true;
	}
	
	/**
	 * Stubbing class of BarcodeScannerListener,
	 * implemented as an interface
	 */
	class BarcodeScannerListenerInterface implements BarcodeScannerListener{
		
		Barcode barcode = null;
		int counter = 0;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
			this.barcode = barcode;
			counter++;
			
		}
		
		public Barcode getBarcode() {
			return this.barcode;
		}
		
	}
}