package CODE;

import org.lsmr.selfcheckout.devices.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteValidatorListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteStorageUnitListener;
import org.lsmr.selfcheckout.Banknote;

/** Control software which codes for customers paying with a banknote **/

public class PayWithNote {
	/**
	 * noteObj : the coin being inserted 
	 * slot: The banknote slot being used  
	 * slotListener: The banknote slot's listener
	 * validator: The banknote validator being used 
	 * validatorListener : the banknote validator's listener 
	 * storage: The banknote storage unit being used
	 * storageListener: The banknote storage unit's listener
	 * dispenser: The banknote dispenser being used 
	 * denominations: The list of banknote denominations being used by the self checkout station 
	 * orderPrice: The total price of all items in the cart 
	 * moneyInserted: The total value of the all the banknotes being inserted
	 */
	
	public BanknoteSlot slot;
	public BanknoteSlotListenerInterface slotListener;
	public BanknoteValidator validator;
	public BanknoteValidatorListenerInterface validatorListener;
	public BanknoteStorageUnit storage;
	public BanknoteStorageUnitListenerInterface storageListener;
	public BanknoteDispenser dispenser;
	public int[] denominations;
	public int totalCashInserted;
	private BidirectionalChannel<Banknote> validatorSource;

	public PayWithNote(Currency currency, int capacity, int[] denominations,
			boolean invert) {

		slot = new BanknoteSlot(invert);
		slotListener = new BanknoteSlotListenerInterface();
		validator = new BanknoteValidator(currency, denominations);
		validatorListener = new BanknoteValidatorListenerInterface();
		storage = new BanknoteStorageUnit(capacity);
		storageListener = new BanknoteStorageUnitListenerInterface();
		dispenser = new BanknoteDispenser(capacity);
		totalCashInserted = 0;
		slot.register(slotListener);
		validator.register(validatorListener);
		storage.register(storageListener);
		
		
		validatorSource = new BidirectionalChannel<Banknote>(slot, validator);
		slot.connect(validatorSource);
		UnidirectionalChannel<Banknote> bc = new UnidirectionalChannel<Banknote>(storage);
		validator.connect(validatorSource, bc);
	}
	/**
	 * Constructor for GUI
	 * @param denominations: Denominations of possible coin values
	 * @param slot: Banknote slot to be used
	 * @param validator: Banknote validator to be used  
	 * @param storage: Banknote storage unit to be used
	 * @param Dispenser: Banknote dispenser to be used
	 */
	public PayWithNote(SelfCheckoutStation selfCheckout) {

		this.slot = selfCheckout.banknoteInput;
		slotListener = new BanknoteSlotListenerInterface();
		this.validator = selfCheckout.banknoteValidator;
		validatorListener = new BanknoteValidatorListenerInterface();
		this.storage = selfCheckout.banknoteStorage;
		storageListener = new BanknoteStorageUnitListenerInterface();
		totalCashInserted = 0;
		this.slot.register(slotListener);
		this.validator.register(validatorListener);
		this.storage.register(storageListener);
		
	}

	/**
	 * Trying to pay with a banknote.
	 * 
	 * @param noteObj, the banknote that is being inserted.
	 * @throws DisabledException
	 * @throws OverloadException
	 */

	public void payNote(Banknote noteObj) throws DisabledException, OverloadException {
		if (noteObj == null) {
			throw new SimulationException(new NullPointerException("Banknote is null."));
		}
		try {
			slot.accept(noteObj);
			if (slotListener.getFlag() == true) {
				validator.accept(noteObj);
				if (validatorListener.getFlag() == true) {
					storage.accept(noteObj);
					if (storageListener.getFlag() == true) {
						int noteValue = noteObj.getValue();
						totalCashInserted += noteValue;
					}

				}

			}
			else {
				slot.removeDanglingBanknote();
			}

		} catch (SimulationException s) {
			System.out.println("Banknote was rejected.");
			s.printStackTrace();
		}
	}



	/**
	 * Getter for the total cart price.
	 * 
	 * @return
	 */

	public int getTotalCashInserted() {
		return totalCashInserted;
	}

	class BanknoteSlotListenerInterface implements BanknoteSlotListener {
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
		public void banknoteInserted(BanknoteSlot slot) {
			// TODO Auto-generated method stub
			this.slotFlag = true;
		}

		@Override
		public void banknoteEjected(BanknoteSlot slot) {
			// TODO Auto-generated method stub
			this.slotFlag = false;
		}

		@Override
		public void banknoteRemoved(BanknoteSlot slot) {
			// TODO Auto-generated method stub
			this.slotFlag = false;
		}

		public boolean getFlag() {
			return this.slotFlag;
		}

	}

	class BanknoteValidatorListenerInterface implements BanknoteValidatorListener {
		Banknote note = null;
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
		public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
			this.validFlag = true;
			this.note = note;

		}

		@Override
		public void invalidBanknoteDetected(BanknoteValidator validator) {
			// TODO Auto-generated method stub
			this.validFlag = false;

		}

		public Banknote getNote() {
			return this.note;
		}

		public boolean getFlag() {
			return this.validFlag;
		}
	}

	class BanknoteStorageUnitListenerInterface implements BanknoteStorageUnitListener {
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
		public void banknotesFull(BanknoteStorageUnit unit) {
			// TODO Auto-generated method stub
			this.storageFlag = false;
		}

		@Override
		public void banknoteAdded(BanknoteStorageUnit unit) {
			// TODO Auto-generated method stub
			this.storageFlag = true;
		}

		@Override
		public void banknotesLoaded(BanknoteStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknotesUnloaded(BanknoteStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		public boolean getFlag() {
			return this.storageFlag;
		}
	}
}