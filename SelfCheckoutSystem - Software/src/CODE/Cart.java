package CODE;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

/**
 * Class for cart with cart ArrayList
 * @author Vivian Huynh and Hailey Allen and Abinaya Subramanian
 */
public class Cart {

	public ArrayList<CartProduct> cart = new ArrayList<CartProduct>(); 
	BigDecimal cumPrice = new BigDecimal(0);
	CartProduct cartProduct;
	
	public Cart() {
		this.cumPrice= BigDecimal.ZERO;
	}

	/**
	 * Add product of CardProduct type to the cart
	 * @param product
	 */
	public void addToCart(CartProduct cartProduct) {
		if (cartProduct.getPrice() == null) {
			throw new NullPointerException("Price is null");
		} else if(cartProduct.getPrice() == BigDecimal.ZERO) {
			throw new SimulationException("Price is zero");
		} else if (cartProduct.getDescription() == ""){
			throw new SimulationException("Description is null");
		} else {
			cart.add(cartProduct);
			cumPrice = cumPrice.add(cartProduct.getPrice());
		}
	}

	/**
	 * Access to the cumulative price of all products scanned
	 * 
	 * @return cumulative price
	 */
	public BigDecimal getCumPrice() {
		return this.cumPrice;
	}
	
	public void setCumPrice(BigDecimal price) {
		this.cumPrice = price;
	}

	/**
	 * Allows the user to remove an item from 
	 * their cart
	 * @param cartProduct
	 */
	public void removeFromCart(CartProduct cartProduct) {
		try {
			cart.remove(cartProduct);
			cumPrice = cumPrice.subtract(cartProduct.getPrice());
		} catch (SimulationException s){
			
		}
	}
	
	public void clearCart() {
		cart.removeAll(cart);
		cumPrice = BigDecimal.ZERO;
	}
	
	/**
	 * Returns the ArrayList of carts
	 * 
	 * @return
	 */
	public ArrayList returnCart() {
		return cart;
	}
	
	/**
	 * Add PLU Coded product to cart
	 * @param product
	 */
	public void addPLUCodedProductToCart(PLUCodedProduct product, BigDecimal price) {
		CartProduct cartProduct = new CartProduct(product.getDescription(), price);
		addToCart(cartProduct);
	}
	
	/**
	 * Add barcoded product to cart
	 * 
	 * @param product
	 */
	public void addBarcodedProductToCart(BarcodedProduct product) {
		CartProduct cartProduct = new CartProduct(product.getDescription(), product.getPrice());
		addToCart(cartProduct);
	}
	
	public void removeBarcodedProductFromCart(BarcodedProduct product) {
		CartProduct cartProduct = new CartProduct(product.getDescription(), product.getPrice());
		removeFromCart(cartProduct);
	}
	
	/**
	 * Add total plastic bags to cart
	 * @param price
	 */
	public void addPlasticBagToCart(BigDecimal price) {
		MathContext m = new MathContext(2); //fixes rounding
		price = price.round(m); 
		CartProduct cartProduct = new CartProduct("Plastic bag(s)", price);
		addToCart(cartProduct);
	}
	
	public void removePlasticBagFromCart (BigDecimal price) {
		CartProduct cartProduct = new CartProduct("Plastic bag(s)", price);
		removeFromCart(cartProduct);
	}
	
	public String getDescription() {
		return this.cartProduct.getDescription();
	}

	public BigDecimal getPrice() {
		return this.cartProduct.getPrice();
	}

	/**
	 * Prints descriptions and price of items in cart
	 * @return cartArray
	 */
	public String printCart() {
		int i = 0;

		String cartArray = "";
		if (cart.size() == 0) {
			return cartArray;
		}
		do {
			String description = "" + cart.get(i).name;
			String price = "" + cart.get(i).price;
			cartArray += description + ": $" + price + "\n";
			i++;
		} while (i < cart.size());
		return cartArray;
	}
}