package CODE;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.devices.listeners.*;

/**
 * Places customer's own bag, places items in bagging area
 * 
 * @author Yousaf, Abi, Vivian, Eric
 */
public class itemInBaggingAreaV2 {

	/*
	 * ElectronicsScaleListener: Listener for baggingScale, doesn't work; points to
	 * null. baggingScale: Scale in bagging area. items: array list of all items.
	 * 
	 * apple: just a test item. pear: just a test item. melon: just a test item.
	 * 
	 * predictedWeightOnBagging: What weight should be on the baggingScale, if not
	 * matching then some item has been removed.
	 * 
	 * ownBag: Whether the customer has their own bag bagPlacedOnScale: Whether the
	 * bag has been placed on the scale
	 */
	public ArrayList<Item> items; 
	public double predictedWeightOnBagging;
	ElectronicScaleListenerInterface listener;
	private boolean ownBag;
	boolean bagPlacedOnScale;
	LoginToControl loginToControl = new LoginToControl();
	boolean attendantLogin = loginToControl.isAttendantLoggedIn();
	SelfCheckoutStation checkoutStation;
	int scaleSensitivity; 
	int weightLimit; 
	boolean correctItemAccepted;

	Cart cart;
	double scannedWeight;
	double changeInWeight;

	private int numberOfPlasticBags = 0; // number of plastic bags used
	public double plasticBagsCost = 0.00; //cost of plastic bags
    public boolean plasticBagsCostAdded = false; //flag used to make sure bag price not incorrectly added 
    public double previousPlasticBagsCost = 0.00; //previous cost of plastic bags (in case it changes).
    public double weightNotInBag = 0.00; //weight of all the scanned items not placed in bag. 
	public ArrayList<Item> itemsNotBagged; // CHANGE made public

	// Constructor
	public itemInBaggingAreaV2(SelfCheckoutStation checkoutStation, Cart cart, int weightLimitInGrams, int sensitivity) {

		this.checkoutStation = checkoutStation;
		this.cart = cart;
		this.scaleSensitivity = sensitivity;
		this.weightLimit = weightLimitInGrams;
		items = new ArrayList<>();
		itemsNotBagged = new ArrayList<>();
		
		listener = new ElectronicScaleListenerInterface();
		checkoutStation.baggingArea.register(listener);

		predictedWeightOnBagging = 0.0;

	}

	// Gets the listener boolean which indicates whether an item has been
	// successfully added or removed from the scanner
	public boolean getListenerBoolean() {
		return this.listener.itemAcceptedorRemoved;
	}

	// Gets an arraylist of all items on scale
	public ArrayList<Item> getItemsOnScale(ArrayList<Item> itemsOnScale) {
		
		itemsOnScale = items;
		return itemsOnScale;
	}

	/*
	 * PopulateItems: Adds item to item list.
	 */
	private void PopulateItems(Item item) {

		items.add(item);

	}

	/*
	 * IndexItem: Returns item at the index in items. If not in items list, adds to
	 * list.
	 */
	public Item IndexItem(Item item) {
		if (item == null) {
			throw new SimulationException(new NullPointerException("Item is null!"));
		}

		if (items.contains(item)) {
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i) == item) {
					return items.get(i);
				}
			}
		} else {
			PopulateItems(item);
			return IndexItem(item);
		}

		return null;

	}
	
	/**
	 * @throws OverloadException 
	 * 
	 */
	public void attendantNotifiedWeight(boolean approved) throws OverloadException {
		if (approved == true) {
			// If attendant gives approval, make the predicted weight match the current weight
			predictedWeightOnBagging = checkoutStation.baggingArea.getCurrentWeight();
			correctItemAccepted = true;
		} else {
			correctItemAccepted = false;
		}
	}
	
	/**
	 * Adds item it bagginga area, returns int depending on issue
	 * 
	 * @param item
	 * @return double: 0 (no issues), 1 (weight issue), 2 (item already in bagging area), 3 (failed to place)
	 * @throws Exception
	 */
	public int AddItem(Item item) throws Exception {
		if (item == null) {
			throw new SimulationException(new NullPointerException("Item is null!"));
		}
		try {
			Item indexedItem = IndexItem(item);
			double previousWeight = checkoutStation.baggingArea.getCurrentWeight();
			checkoutStation.baggingArea.add(indexedItem);
			
			// Checks if scanned barcoded item matches weight added to bagging area
			scannedWeight = item.getWeight();
			changeInWeight = listener.newWeight - previousWeight;
			// Make sure the change in weight is between acceptable weights +/- weightVariability
			if (listener.itemAcceptedorRemoved == true) { // weight on scale 
				checkWeightDiscrepancy(item, changeInWeight, scannedWeight);
				if (correctItemAccepted = true) { // no weight discrepancy
					predictedWeightOnBagging += indexedItem.getWeight();
					return 0;
				} else { // weight issue
					return 1;
				}
			} else { // failed to place item
				System.out.println("Item is not in bagging area yet.");
				return 3;
			}	
		} catch (SimulationException s) {
			System.out.println("Item already in bagging area.");
			return 2;
		}

	}
	
	
	/**
	 * Checks for a weight discrepancy. Notifies attendant if there is.
	 * @param item 
	 * @param changeInWeight
	 * @param scannedWeight
	 * @throws Exception
	 */
	public void checkWeightDiscrepancy(Item item, double changeInWeight, double scannedWeight) throws Exception {
		double weightVariability = item.getWeight() * 0.1; // sensitivity added/removed
		if (changeInWeight >= scannedWeight - weightVariability
				&& changeInWeight <= scannedWeight + weightVariability) {
			correctItemAccepted = true;
		} else {
			correctItemAccepted = false;
			attendantNotifiedWeight(attendantLogin); 
			if (correctItemAccepted == false) { // attendant did NOT approve
				System.out.println("Incorrect weight. Remove the item from the bagging area");
				RemoveItem(item);
			}
		}
	}

	/*
	 * RemoveItem: Removes item from scale. Prints current weight in console if item
	 * is removed.
	 */
	public double RemoveItem(Item item) throws Exception {
		if (item == null) {
			throw new SimulationException(new NullPointerException("Item is null!"));
		}
		try {
			Item indexedItem = IndexItem(item);
			checkoutStation.baggingArea.remove(indexedItem);
			// Make sure that current weight matches predicted weight
			if (listener.itemAcceptedorRemoved == true) {
				predictedWeightOnBagging -= indexedItem.getWeight();
				if (checkoutStation.baggingArea.getCurrentWeight() == predictedWeightOnBagging) {
					System.out.println("Item has been removed");
				} else {
					System.out.println("Item has not been removed");
					predictedWeightOnBagging += indexedItem.getWeight();
					return -1;
				}

			}
		} catch (SimulationException s) {
			System.out.println("Item not in bagging area, nothing to remove");
			return -1;
		}

		try {

			return checkoutStation.baggingArea.getCurrentWeight();
		} catch (OverloadException e) {
		}
		return -1;
	}

	// Methods for customer adding their own bag to the bagging area

	/**
	 * Setter for ownBag
	 * 
	 * @param ownBag True if the customer has their own bag(s)
	 */
	public void setOwnBag(Boolean ownBag) {
		this.ownBag = ownBag;
	}

	/**
	 * Getter for ownBag
	 * 
	 * @return ownBag Whether customer has their own bag(s)
	 */
	public boolean getOwnBag() {
		return ownBag;
	}

	/**
	 * Getter for bagPlacedOnScale
	 * 
	 * @return bagPlacedOnScale Whether the bag has been placed on the scale
	 */
	public boolean getBagPlacedOnScale() {
		return bagPlacedOnScale;
	}

	/**
	 * If customer has their own bag, place it on the scale and add the weight to
	 * the predictedWeight
	 * 
	 * @param item
	 * @throws OverloadException
	 */
	public void placeOwnBag(Item item) throws OverloadException {
		if (getOwnBag() == true) {
			System.out.println("Place your own bag(s) in the bagging area.");
			checkoutStation.baggingArea.add(item);
			if (listener.itemAcceptedorRemoved == true) {
				bagPlacedOnScale = true;
				// Add weight of bags to predictedWeightOnBagging so that the currentWeight will
				// match
				predictedWeightOnBagging += item.getWeight();
			} else {
				bagPlacedOnScale = false;
			}
		}
	}
	
	// Setter
	public void setChangeInWeight(double changeInWeight) {
		this.changeInWeight = changeInWeight;
	}
	
	// Getter
	public boolean getCorrectItemAccepted() {
		return correctItemAccepted;
	}

	/**
	 * The following method represents the use case: "customer does not want to bag
	 * a scanned item" 
	 * This method just adds the scanned item's weight to a variable that holds the weight of  
	 * all scanned items not placed on the bagging scale and adds it to the items not bagged list.
	 * This method can add items that are under the sensitivity limit and 
	 * items over the scale weight limit. 
	 * @throws OverloadException 
	 */
	public void AddItemNoBag(Item item) throws OverloadException {
		if (item == null) {
			throw new SimulationException(new NullPointerException("Item is null!"));
		}
		else {
			Item indexedItem = IndexItem(item);
			attendantNotifiedWeight(true); // let the attendant know about the possible difference in checkout weight 
			weightNotInBag += indexedItem.getWeight(); //add to the variable holding the weight of all items not in bagging area
			itemsNotBagged.add(indexedItem);
			return;
		}
	}

	/*
	 * RemoveItem: Removes scanned item that is not on the scale. 
	 */
	public void RemoveItemNotInBag(Item item) throws Exception {
		if (item == null) {
			throw new SimulationException(new NullPointerException("Item is null!"));
		}
		else {
			Item indexedItem = IndexItem(item);
			weightNotInBag -= indexedItem.getWeight();
			itemsNotBagged.remove(indexedItem);
		}
		return;
	}
	
	//checks if item is in the itemsNotBagged list 
	//useful to decide which remove method to call on the item to be removed 
	//if returns true, item is in the not bagged list (itemsNotBagged ArrayList)
	//if returns false, item is in the bagged list (items ArrayList)
	public boolean isInNotBaggedList (Item item) {
		Item indexedItem = IndexItem(item);
		return itemsNotBagged.contains(indexedItem);
	}
	
	
	/**
	 * The following method represents the use case: "customer removes purchased
	 * items from bagging area" This is similar to remove item, but works on all
	 * purchased items at the same time This method should only be used once the
	 * customer is finished purchasing everything at the end of the checkout
	 * process.
	 */
	public void removePurchasedItems() {
		int i = 0; // iterator to process all the items in the list
		int itemsSize = items.size(); // get the size of the all items purchased
		while (i < itemsSize) { // until all items are processed (removed)
			try {
				Item indexedItem = IndexItem(items.get((items.size() - 1))); // get the last item on list
				checkoutStation.baggingArea.remove(indexedItem); // remove last item from the list (from listener
																	// perspective)

				if (listener.itemAcceptedorRemoved == true) { // make sure listener detects it
					predictedWeightOnBagging -= indexedItem.getWeight(); // decrement the total weight of all items
					System.out.println("Item has been removed"); // print message
					items.remove((items.size() - 1)); // remove the last item from the items list in this class
				}
				i++; // increment to process the next item
			} catch (SimulationException s) {
				System.out.println("Nothing left on scale to remove"); // print message
			}
		}
		return;
	}


	/**
	 * The following 2 methods are for the number of plastic bags used (setting and
	 * getting, respectively) which represents the use case: "customer enters number
	 * of plastic bags used"
	 */
	// setter for the number of plastic bags used
	public void addNumberPlasticBagsUsed(int numBags) {
		if (numBags >= 0) { // make sure the number of bags used is a non-negative number
			numberOfPlasticBags = numBags; // number will change based on the plastics bags requested
			plasticBagsCost = numberOfPlasticBags * 0.10; 
			if (plasticBagsCostAdded == true) { //if the plastic bag cost has been previously added
				cart.removePlasticBagFromCart(new BigDecimal(plasticBagsCost));
			}
			cart.addPlasticBagToCart(new BigDecimal(plasticBagsCost));
			plasticBagsCostAdded = true;
			previousPlasticBagsCost = plasticBagsCost; 
			
		} else // invalid number entered ( < 0)
			System.out.println("Invalid number of bags entered"); // print message
	}

	// getter for the number of plastic bags used
	public int getNumberPlasticBagsUsed() {
		return numberOfPlasticBags;
	}
	

    public double getPlasticBagsPrice() {
        return plasticBagsCost; 
    }

}

//Listener interface is implemented to receive information from scanner
class ElectronicScaleListenerInterface implements ElectronicScaleListener {

	boolean itemAcceptedorRemoved = false;
	boolean itemOverload;
	double newWeight;

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		itemAcceptedorRemoved = true;
		newWeight = weightInGrams;
	}

	@Override
	public void overload(ElectronicScale scale) {
		// TODO Auto-generated method stub
		itemAcceptedorRemoved = false;
		itemOverload = true;
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		// TODO Auto-generated method stub
		itemAcceptedorRemoved = true;

	}

}
