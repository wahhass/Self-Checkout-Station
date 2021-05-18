
package CODE;

import org.lsmr.selfcheckout.devices.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinSlotListener;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;
import org.lsmr.selfcheckout.devices.listeners.CoinValidatorListener;
import org.lsmr.selfcheckout.Coin;

/** Control software which codes for customers paying with a coin **/

public class PayWithCoin {
	/**
	 * coinObj : the coin being inserted 
	 * slot: The coin slot being used
	 * slotListener: the coin slot's listener
	 * validator: The coin validator being used
	 * validatorListener: the coin validator's listener 
	 * storage: The coin storage unit being used 
	 * storageListener: the coin storage unit's listener
	 * dispenser: The coin dispenser being used 
	 * denominations: The list of coin denominations being used by the self checkout station 
	 * orderPrice: The total price of all items in the cart 
	 * moneyInserted: The total value of all the coins inserted
	 */
	public Coin coinObj;
	public CoinSlot slot;
	public CoinSlotListenerInterface slotListener;
	public CoinValidator validator;
	public CoinValidatorListenerInterface validatorListener;
	public CoinStorageUnit storage;
	public CoinStorageUnitListenerInterface storageListener;
	public CoinDispenser dispenser;
	public List<BigDecimal> denominations;
	public BigDecimal totalCoinInserted;
	public final Map<BigDecimal, CoinDispenser> coinDispensers;
	public final List<BigDecimal> coinDenominations;

	public PayWithCoin(Currency currency, int storageCapacity, BigDecimal[] denominations) {

		slot = new CoinSlot();
		slotListener = new CoinSlotListenerInterface();
		coinDenominations = Arrays.asList(denominations);
		validator = new CoinValidator(currency, coinDenominations);
		validatorListener = new CoinValidatorListenerInterface();
		storage = new CoinStorageUnit(storageCapacity);
		storageListener = new CoinStorageUnitListenerInterface();
		dispenser = new CoinDispenser(200);
		totalCoinInserted = new BigDecimal("0");
		slot.register(slotListener);
		validator.register(validatorListener);
		storage.register(storageListener);
		CoinTray tray = new CoinTray(20);
		


		coinDispensers = new HashMap<>();

		for(int i = 0; i < denominations.length; i++)
			coinDispensers.put(denominations[i], new CoinDispenser(200));
		
		UnidirectionalChannel<Coin> cc = new UnidirectionalChannel<Coin>(validator);
		slot.connect(cc);
		UnidirectionalChannel<Coin> rejectChannel = new UnidirectionalChannel<Coin>(tray);
		UnidirectionalChannel<Coin> overflowChannel = new UnidirectionalChannel<Coin>(storage);

		Map<BigDecimal, UnidirectionalChannel<Coin>> dispenserChannels = new HashMap<BigDecimal, UnidirectionalChannel<Coin>>();

		for(BigDecimal denomination : coinDispensers.keySet())
		{
			CoinDispenser dispenser = coinDispensers.get(denomination);
			dispenserChannels.put(denomination, new UnidirectionalChannel<Coin>(dispenser));
		}
		

		validator.connect(rejectChannel, dispenserChannels, overflowChannel);
	}
	/**
	 * Constructor for GUI
	 * @param denominations: Denominations of possible coin values
	 * @param slot: Coin slot to be used 
	 * @param validator: Coin validator to be used  
	 * @param storage: Coin storage unit to be used
	 * @param Dispenser: Coin dispenser to be used
	 * @param tray: Coin tray to be used 
	 * @param coinDispensers: Hashmap of all coin dispensers
	 */
	public PayWithCoin(SelfCheckoutStation selfCheckout) {

		this.slot = selfCheckout.coinSlot;
		slotListener = new CoinSlotListenerInterface();
		coinDenominations = selfCheckout.coinDenominations;
		this.validator = selfCheckout.coinValidator;
		validatorListener = new CoinValidatorListenerInterface();
		this.storage = selfCheckout.coinStorage;
		storageListener = new CoinStorageUnitListenerInterface();

		totalCoinInserted = new BigDecimal("0");
		this.slot.register(slotListener);
		this.validator.register(validatorListener);
		this.storage.register(storageListener);
		this.coinDispensers = selfCheckout.coinDispensers;
	}

	/**
	 * Trying to pay with a coin.
	 * 
	 * @param coinObj, the coin that is being inserted
	 * @throws DisabledException
	 * @throws OverloadException
	 */

	public void payCoin(Coin coinObj) throws DisabledException, OverloadException {
		if (coinObj == null) {
			throw new SimulationException(new NullPointerException("Coin is null."));
		}
		try {
			slot.accept(coinObj);
			if (slotListener.getFlag() == true) {
				
				validator.accept(coinObj);
					
				if (validatorListener.getFlag() == true) {
					storage.accept(coinObj);
					
					if (storageListener.getFlag() == true) {
						
						totalCoinInserted = totalCoinInserted.add(coinObj.getValue());
					}
				}
			}
		} catch (SimulationException s) {
			System.out.println("Coin was rejected.");
			s.printStackTrace();
		}
	}
	


	
	/**
	 * Getter for the total cart price.
	 * 
	 * @return
	 */
	public BigDecimal getTotalCoinInserted() {
		return totalCoinInserted;
	}


	class CoinSlotListenerInterface implements CoinSlotListener {
		boolean slotFlag = false;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinInserted(CoinSlot slot) {
			// TODO Auto-generated method stub
			this.slotFlag = true;

		}

		public boolean getFlag() {
			return this.slotFlag;
		}

	}

	class CoinValidatorListenerInterface implements CoinValidatorListener {
		Coin coin = null;
		boolean validFlag = false;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void validCoinDetected(CoinValidator validator, BigDecimal value) {
			this.validFlag = true;
			this.coin = coin;

		}

		@Override
		public void invalidCoinDetected(CoinValidator validator) {
			this.validFlag = false;
			// TODO Auto-generated method stub

		}

		public Coin getCoin() {
			return this.coin;
		}

		public boolean getFlag() {
			return this.validFlag;
		}

	}

	class CoinStorageUnitListenerInterface implements CoinStorageUnitListener {
		boolean storageFlag = false;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinsFull(CoinStorageUnit unit) {
			this.storageFlag = false;
			// TODO Auto-generated method stub

		}

		@Override
		public void coinAdded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub
			this.storageFlag = true;
		}

		@Override
		public void coinsLoaded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinsUnloaded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		public boolean getFlag() {
			return this.storageFlag;
		}

	}
}