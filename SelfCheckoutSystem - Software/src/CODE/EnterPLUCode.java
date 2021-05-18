package CODE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

/**
 * Accepts the PLU code, adds the price to the total, and add it to the cart
 * @author Vivian Huynh and Hailey Allen
 */
public class EnterPLUCode {

	public static double PLUItemWeight;
	static double productPrice;
	static double totalProductPrice;

	public static Map<PriceLookupCode, PLUCodedProduct> PLU_PRODUCT_DATABASE = new HashMap<>();
	static SelfCheckoutStation checkoutStation;
	static itemInBaggingAreaV2 baggingArea;
	static Cart cart;
	
	/**
	 * Constructor
	 * @param checkoutStation
	 * @param cart
	 * @param baggingArea
	 */
	public EnterPLUCode(SelfCheckoutStation checkoutStation, Cart cart, itemInBaggingAreaV2 baggingArea) {
		this.checkoutStation = checkoutStation;
		this.cart = cart;
		this.baggingArea = baggingArea;
	}

	/**
	 * Adds PLU products to database. Makes PLU items.
	 */
	public void addToPLUDatabase() {

		// Create PLU products, price per kg
		PLUCodedProduct apple = new PLUCodedProduct(new PriceLookupCode("4205"), "Apples", new BigDecimal(4.30));
		PLUCodedProduct apricot = new PLUCodedProduct(new PriceLookupCode("4219"), "Apricots", new BigDecimal(2.32));
		PLUCodedProduct cabbage = new PLUCodedProduct(new PriceLookupCode("4556"), "Mushrooms", new BigDecimal(4.52));
		PLUCodedProduct bananas = new PLUCodedProduct(new PriceLookupCode("4014"), "Bananas", new BigDecimal(4.52));
		PLUCodedProduct lemon = new PLUCodedProduct(new PriceLookupCode("4053"), "Lemons", new BigDecimal(4.52));
		PLUCodedProduct broccoli = new PLUCodedProduct(new PriceLookupCode("3082"), "Broccoli", new BigDecimal(4.52));

		// Add to database
		PLU_PRODUCT_DATABASE.put(apple.getPLUCode(), apple);
		PLU_PRODUCT_DATABASE.put(apricot.getPLUCode(), apricot);
		PLU_PRODUCT_DATABASE.put(cabbage.getPLUCode(), cabbage);
		PLU_PRODUCT_DATABASE.put(bananas.getPLUCode(), bananas);
		PLU_PRODUCT_DATABASE.put(lemon.getPLUCode(), lemon);
		PLU_PRODUCT_DATABASE.put(broccoli.getPLUCode(), broccoli);
		
	}

	// Setters and getters
	public static void setWeightofItem(double weightOfItem) {
		EnterPLUCode.PLUItemWeight = weightOfItem;
	}
	
	/**
	 * Return weight of PLU item
	 * @return
	 */
	public double getPLUItemWeight() {
		return PLUItemWeight;
	}
	
	/**
	 * Return cart for easy implementation
	 * @return
	 */
	public static Cart getCart() {
		return cart;
	}
	
	/**
	 * Accepts PLU code, finds the item, gets the weight, adds the total to the
	 * cumulative total, and adds the item to the cart.
	 * 
	 * @param PLU_PRODUCT_DATABASE
	 * @param PLUcode
	 * @throws Exception 
	 */
	public void enterPLUItem(PriceLookupCode PLUcode) throws Exception {

		// If product exists in database
		if (PLU_PRODUCT_DATABASE.containsKey(PLUcode) == true) {
			System.out.println("Place item on the scale.");
			Random r = new Random();
			double weight = 70.0 + (400.0 - 70.0) * r.nextDouble();
			PLUCodedItem PLUCodedItem = new PLUCodedItem(PLUcode, weight);
			// Add item to scale
			checkoutStation.scale.add(PLUCodedItem); 
			PLUItemWeight = checkoutStation.scale.getCurrentWeight();
			// Get price of PLU product
			productPrice = PLU_PRODUCT_DATABASE.get(PLUcode).getPrice().doubleValue();
			// Multiply price of PLU item by quantity
			totalProductPrice = productPrice * (PLUItemWeight * 0.001);
			// Add item to cart
			cart.addPLUCodedProductToCart(PLU_PRODUCT_DATABASE.get(PLUcode), new BigDecimal(totalProductPrice).setScale(2, RoundingMode.HALF_UP));
			// Remove item from scanner scale
			checkoutStation.scale.remove(PLUCodedItem);
			// Prompt user to add item to the bagging area
			System.out.println("Please move your item to the bagging area");
		} else {
			System.out.println("PLU code not found in database.");
		}
	}
	
}