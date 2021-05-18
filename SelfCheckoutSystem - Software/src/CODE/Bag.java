package CODE;

import org.lsmr.selfcheckout.Item;

/**
 * Represents bags as items with a particular weight
 * @author Vivian Huynh
 *
 */
public class Bag extends Item {
	
	/**
	 * Basic constructor.
	 * 
	 * @param weightInGrams The weight of the bag.
	 */
	public Bag(double weightInGrams) {
		super(weightInGrams);
	}

}
