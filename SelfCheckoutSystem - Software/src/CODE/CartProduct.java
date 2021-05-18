package CODE;

import java.math.BigDecimal;

/**
 * 
 * @author Vivian and Hailey
 *
 */
public class CartProduct {

	public String name;
	public BigDecimal price;
	
	// Constructor
	public CartProduct(String name, BigDecimal price) {
		this.name = name;
		this.price = price;
	}
	
	/**
	 * Getter method for description
	 * @return name
	 */
	public String getDescription() {
		return this.name;
	}
	
	/**
	 * Getter method for price
	 * @return price
	 */
	public BigDecimal getPrice() {
		return this.price;
	}
	
	public String toString() {
		return name + ": $" + price;
	}
	
}
