package CODE;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.*;

import java.math.BigDecimal;
import java.util.Currency;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.lsmr.selfcheckout.devices.listeners.CoinTrayListener;
import org.lsmr.selfcheckout.devices.listeners.CoinDispenserListener;

public class GiveChange {
	/**
	 * @Param noteSlot: The banknote slot for ejecting change in banknotes.
	 * @Param noteSlotListener: The listener for the banknote slot.
	 * @Param noteDispenser: The dispenser for banknotes
	 * @Param noteDispenserListener: The listener for the banknote dispenser
	 * @Param totalChange: The total change to be dispensed
	 * @Param coinDispenser: The dispenser for coins
	 * @Param coinDispenserListener: The listener for the coin dispenser
	 * @Param coinTray: The tray for coins to be dispensed as change
	 * @Param coinTrayListener: The listener for the coin tray
	 * @Param changeFlag: True if change is all dispensed; false if otherwise
	 * @Param banknoteDispensers: Hashmap of each banknote dispenser
	 * @Param banknoteDispensersListeners: Hashmap of each banknote dispenser listener
	 * @Param banknote_dispenser_capacity: Capacity of each banknote dispenser
	 * @Param noteDenominations: Denominations of possible banknote values in CAD
	 * @Param coinDenominations: Denominations of possible coin values in CAD
	 * @Param coinDispensers: Hashmap of each coin dispenser
	 * @Param coinDispensersListeners: Hashmap of each coin dispenser listener
	 * @Param coin_dispenser_capacity: Capacity of each coin dispenser
	 * @Param coin_tray_capacity: Capcity of coin tray
	 * @Param CAD: Currency type, which in this case is Canadian Dollars.
	 * 
	 */
	public BanknoteSlot noteSlot;
	public BanknoteSlotListenerInterface noteSlotListener;
	public BanknoteDispenser noteDispenser;
	public BanknoteDispenserListenerInterface noteDispenserListener;
	public BigDecimal totalChange;
	public CoinDispenser coinDispenser;
	public CoinDispenserListenerInterface coinDispenserListener;
	public CoinTray coinTray;
	public CoinTrayListenerInterface coinTrayListener;
	public boolean changeFlag;
	public final Map<Integer, BanknoteDispenser> banknoteDispensers;
	public final Map<Integer, BanknoteDispenserListenerInterface> banknoteDispensersListeners;
	public final static int BANKNOTE_DISPENSER_CAPACITY = 100;
	public final int[] noteDenominations;
	public final List<BigDecimal> coinDenominations;
	public final Map<BigDecimal, CoinDispenser> coinDispensers;
	public final Map<BigDecimal, CoinDispenserListenerInterface> coinDispensersListeners;
	public static final int COIN_DISPENSER_CAPACITY = 200;
	public static final int COIN_TRAY_CAPACITY = 20;
	public Currency CAD;
	


	public GiveChange(Currency currency, boolean invert, int[] noteDenominations, BigDecimal[] coinDenominations) {

		noteSlot = new BanknoteSlot(invert);
		noteSlotListener = new BanknoteSlotListenerInterface();
		noteDispenserListener = new BanknoteDispenserListenerInterface();
		noteSlot.register(noteSlotListener);
		coinTray = new CoinTray(COIN_TRAY_CAPACITY);
		coinTrayListener = new CoinTrayListenerInterface();
		coinTray.register(coinTrayListener);
		CAD = currency;
		this.noteDenominations = noteDenominations;
		this.coinDenominations = Arrays.asList(coinDenominations);
		banknoteDispensers = new HashMap<>();
		banknoteDispensersListeners = new HashMap<>();
		changeFlag = false;

		for (int i = 0; i < noteDenominations.length; i++) {
			banknoteDispensers.put(noteDenominations[i], new BanknoteDispenser(BANKNOTE_DISPENSER_CAPACITY));
			banknoteDispensersListeners.put(noteDenominations[i], new BanknoteDispenserListenerInterface());
		}
		coinDispensers = new HashMap<>();
		coinDispensersListeners = new HashMap<>();

		for (int i = 0; i < coinDenominations.length; i++) {
			System.out.println(coinDenominations[i]);
			coinDispensers.put(coinDenominations[i], new CoinDispenser(COIN_DISPENSER_CAPACITY));
			coinDispensersListeners.put(coinDenominations[i], new CoinDispenserListenerInterface());
		}

		for (CoinDispenser coinDispenser : coinDispensers.values()) {
			interconnect(coinDispenser, coinTray);
		}

		coinDispensers.get(new BigDecimal("0.05")).register(coinDispensersListeners.get(new BigDecimal("0.05")));
		coinDispensers.get(new BigDecimal("0.10")).register(coinDispensersListeners.get(new BigDecimal("0.10")));
		coinDispensers.get(new BigDecimal("0.25")).register(coinDispensersListeners.get(new BigDecimal("0.25")));
		coinDispensers.get(new BigDecimal("1.00")).register(coinDispensersListeners.get(new BigDecimal("1.00")));
		coinDispensers.get(new BigDecimal("2.00")).register(coinDispensersListeners.get(new BigDecimal("2.00")));

		for (BanknoteDispenser noteDispenser : banknoteDispensers.values()) {
			interconnect(noteDispenser, noteSlot);
		}
		banknoteDispensers.get(5).register(banknoteDispensersListeners.get(5));
		banknoteDispensers.get(10).register(banknoteDispensersListeners.get(10));
		banknoteDispensers.get(20).register(banknoteDispensersListeners.get(20));
		banknoteDispensers.get(50).register(banknoteDispensersListeners.get(50));
		banknoteDispensers.get(100).register(banknoteDispensersListeners.get(100));
	}
	
	
	/**
	 * Constructor made to take in self checkout station devices as parameters
	 * @param noteSlot
	 * @param coinTray
	 * @param banknoteDispensers
	 * @param coinDispensers
	 * @param coinDenominations
	 * @param noteDenominations
	 */
	public GiveChange(BanknoteSlot noteSlot, CoinTray coinTray, Map<Integer, BanknoteDispenser> banknoteDispensers,  Map<BigDecimal, CoinDispenser> coinDispensers, BigDecimal[] coinDenominations, int[] noteDenominations) {
		this.noteSlot = noteSlot;
		noteSlotListener = new BanknoteSlotListenerInterface();
		this.noteSlot.register(noteSlotListener);
		this.coinDenominations = Arrays.asList(coinDenominations);
		this.coinDispensers = coinDispensers;
		this.coinDispensersListeners = new HashMap<>();
		this.noteDenominations = noteDenominations;
		this.coinTray = coinTray;
		coinTrayListener = new CoinTrayListenerInterface();
		this.coinTray.register(coinTrayListener);
		CAD = Currency.getInstance(Locale.CANADA);
		
		this.banknoteDispensers = banknoteDispensers;
		banknoteDispensersListeners = new HashMap<>();
		
		for (int i = 0; i < noteDenominations.length; i++) {
			banknoteDispensersListeners.put(noteDenominations[i], new BanknoteDispenserListenerInterface());
		}
		
		for (int i = 0; i < coinDenominations.length; i++) {
			coinDispensersListeners.put(coinDenominations[i], new CoinDispenserListenerInterface());
		}
		
		coinDispensers.get(new BigDecimal("0.05")).register(coinDispensersListeners.get(new BigDecimal("0.05")));
		coinDispensers.get(new BigDecimal("0.10")).register(coinDispensersListeners.get(new BigDecimal("0.10")));
		coinDispensers.get(new BigDecimal("0.25")).register(coinDispensersListeners.get(new BigDecimal("0.25")));
		coinDispensers.get(new BigDecimal("1.00")).register(coinDispensersListeners.get(new BigDecimal("1.00")));
		coinDispensers.get(new BigDecimal("2.00")).register(coinDispensersListeners.get(new BigDecimal("2.00")));

		
		
		banknoteDispensers.get(5).register(banknoteDispensersListeners.get(5));
		banknoteDispensers.get(10).register(banknoteDispensersListeners.get(10));
		banknoteDispensers.get(20).register(banknoteDispensersListeners.get(20));
		banknoteDispensers.get(50).register(banknoteDispensersListeners.get(50));
		banknoteDispensers.get(100).register(banknoteDispensersListeners.get(100));
		
		
		
	}

	private void interconnect(BanknoteDispenser dispenser, BanknoteSlot slot) {
		UnidirectionalChannel<Banknote> bc = new UnidirectionalChannel<Banknote>(slot);
		dispenser.connect(bc);
	}

	private void interconnect(CoinDispenser dispenser, CoinTray tray) {
		UnidirectionalChannel<Coin> cc = new UnidirectionalChannel<Coin>(tray);
		dispenser.connect(cc);
	}
	
	public Map<Integer, BanknoteDispenser> getBankNoteDispensers(){
		return banknoteDispensers;
	}
	
	public Map<BigDecimal, CoinDispenser> getCoinDispensers(){
		return coinDispensers;
	}
	
	
	/**
	 * refillBanknoteDispensers is a method restricted to only attendant use
	 * This method checks if any of the dispensers are empty and then fills them if they are
	 * 
	 * @param BanknotDispenserCapacity - the amount of banknotes to add to each dispenser if empty
	 * if higher than capacity - overload exception is thrown
	 */
	public void refillBanknoteDispensers(int BanknoteDispenserCapacity) {
		
		//Is 5 empty, if so fill to capacity
		if(banknoteDispensers.get(5).size() == 0) {
			Banknote note5 = new Banknote(5, CAD);
			for (int i = 1; i <= BanknoteDispenserCapacity; i++) {
				try {
					banknoteDispensers.get(5).load(note5);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Banknote five Dispenser overloaded");
					
				}
			}
		}
		//Is 10 empty, if so fill to capacity
		if(banknoteDispensers.get(10).size() == 0) {
			Banknote note10 = new Banknote(10, CAD);
			for (int i = 1; i <= BanknoteDispenserCapacity; i++) {
				try {
					banknoteDispensers.get(10).load(note10);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Banknote 10 Dispenser overloaded");
					
				}
			}
		}
		//Is 20 empty, if so fill to capacity
		if(banknoteDispensers.get(20).size() == 0) {
			Banknote note20 = new Banknote(20, CAD);
			for (int i = 1; i <= BanknoteDispenserCapacity; i++) {
				try {
					banknoteDispensers.get(20).load(note20);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Banknote 20 Dispenser overloaded");
					
				}
			}
		}
		
		//Is 50 empty, if so fill to capacity
		if(banknoteDispensers.get(50).size() == 0) {
			Banknote note50 = new Banknote(50, CAD);
			for (int i = 1; i <= BanknoteDispenserCapacity; i++) {
				try {
					banknoteDispensers.get(50).load(note50);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Banknote 50 Dispenser overloaded");
					
				}
			}
		}
		
		//Is 100 empty, if so fill to capacity
		if(banknoteDispensers.get(100).size() == 0) {
			Banknote note100 = new Banknote(100, CAD);
			for (int i = 1; i <= BanknoteDispenserCapacity; i++) {
				try {
					banknoteDispensers.get(100).load(note100);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Banknote 100 Dispenser overloaded");
					
				}
			}
		}
		
	}
	
	
	/**
	 * refillCoinDispensers is a method restricted to only attendant use
	 * This method checks if any of the dispensers are empty and then fills them if they are
	 * 
	 * @param BanknotDispenserCapacity - the amount of coins to add to each dispenser if empty
	 * if higher than capacity - overload exception is thrown
	 */
	public void refillCoinDispenser(int CoinDispenserCapacity) {
		BigDecimal nickel = new BigDecimal("0.05");
		BigDecimal dime = new BigDecimal("0.10");
		BigDecimal quarter = new BigDecimal("0.25");
		BigDecimal loonie = new BigDecimal("1.00");
		BigDecimal toonie = new BigDecimal("2.00");
		Coin coin5 = new Coin(nickel, CAD);
		Coin coin10 = new Coin(dime, CAD);
		Coin coin25 = new Coin(quarter, CAD);
		Coin coin100 = new Coin(loonie, CAD);
		Coin coin200 = new Coin(toonie, CAD);
		
		if(coinDispensers.get(nickel).size() == 0) {
			for (int i = 1; i <= CoinDispenserCapacity; i++) {
				try {
					coinDispensers.get(nickel).load(coin5);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Nickel coin Dispenser overloaded");
					
				}
			}
		}
		
		if(coinDispensers.get(dime).size() == 0) {
			//Coin coin10 = new Coin(dime, CAD);
			for (int i = 1; i <= CoinDispenserCapacity; i++) {
				try {
					coinDispensers.get(dime).load(coin10);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Dime coin Dispenser overloaded");
					
				}
			}
		}
		
		if(coinDispensers.get(quarter).size() == 0) {
			//Coin coin25 = new Coin(quarter, CAD);
			for (int i = 1; i <= CoinDispenserCapacity; i++) {
				try {
					coinDispensers.get(quarter).load(coin25);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Quarter coin Dispenser overloaded");
					
				}
			}
		}
		
		if(coinDispensers.get(loonie).size() == 0) {
			//Coin coin100 = new Coin(loonie, CAD);
			for (int i = 1; i <= CoinDispenserCapacity; i++) {
				try {
					coinDispensers.get(loonie).load(coin100);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException(" $1 coin Dispenser overloaded");
					
				}
			}
		}
		
		if(coinDispensers.get(toonie).size() == 0) {
			//Coin coin200 = new Coin(toonie, CAD);
			for (int i = 1; i <= CoinDispenserCapacity; i++) {
				try {
					coinDispensers.get(toonie).load(coin200);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException(" $2 coin Dispenser overloaded");
					
				}
			}
		}
		

		
	}
	
		
	/**
	 * Trying to return change.
	 * 
	 * @param totalChange: change to be returned to the customer.
	 * @throws EmptyException
	 * @throws DisabledException
	 * @throws OverloadException
	 */
	public void returnChange(BigDecimal totalChange) throws EmptyException, DisabledException, OverloadException {
		if (totalChange == null) {
			throw new SimulationException(new NullPointerException("Amount of change is null."));
		}
		
		try {

			List<BigDecimal> changeList = new ArrayList<BigDecimal>();
			BigDecimal zero = new BigDecimal("0.00");
			for (int i = (noteDenominations.length - 1); i >= 0; i--) {
				BigDecimal num = new BigDecimal(noteDenominations[i]);
				while (totalChange.subtract(num).compareTo(zero) == 1
						|| totalChange.subtract(num).compareTo(zero) == 0) {
					if (totalChange.compareTo(num) == 1 || totalChange.compareTo(num) == 0) {
						totalChange = totalChange.subtract(num);
						changeList.add(num);
					}
				}
			}
			if (changeList.isEmpty() == false) {
				for (int i = 0; i < changeList.size(); i++) {
					banknoteDispensers.get(changeList.get(i).intValue()).emit();
					noteSlot.removeDanglingBanknote();
					if (banknoteDispensersListeners.get(changeList.get(i).intValue()).getFlag() == true
							&& noteSlotListener.getFlag() == true) {
						System.out.println("Banknote has been dispensed.");
					}
				}
			}
				changeList.clear();
				for (int i = (coinDenominations.size() - 1); i >= 0; i--) {
					BigDecimal num = coinDenominations.get(i);
					while (totalChange.subtract(num).compareTo(zero) == 1
							|| totalChange.subtract(num).compareTo(zero) == 0) {
						if (totalChange.compareTo(num) == 1 || totalChange.compareTo(num) == 0) {
							totalChange = totalChange.subtract(num);
							changeList.add(num);
						}
					}
				}
				for (int i = 0; i < changeList.size(); i++) {
					coinDispensers.get(changeList.get(i)).emit();
					if (coinDispensersListeners.get(changeList.get(i)).getFlag() == true
							&& coinTrayListener.getFlag() == true) {
						System.out.println("Coin has been dispensed into tray.");
					}

					
				}
				
			
			if (totalChange.compareTo(zero) == 0) {
				this.changeFlag = true;
				System.out.println("Change has been dispensed.");
			}
		} catch (

		SimulationException s) {
			System.out.println("Change was not ejected.");
			s.printStackTrace();
		}

	}

	public boolean isChangeDispensed() {
		return changeFlag;
	}

	class BanknoteDispenserListenerInterface implements BanknoteDispenserListener {
		boolean dispenserFlag = false;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknotesFull(BanknoteDispenser dispenser) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknotesEmpty(BanknoteDispenser dispenser) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknoteAdded(BanknoteDispenser dispenser, Banknote banknote) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
			// TODO Auto-generated method stub
			dispenserFlag = true;

		}

		@Override
		public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
			// TODO Auto-generated method stub

		}

		public boolean getFlag() {
			return this.dispenserFlag;
		}
	}

	class BanknoteSlotListenerInterface implements BanknoteSlotListener {
		boolean noteSlotFlag = false;

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
		}

		@Override
		public void banknoteEjected(BanknoteSlot slot) {
			// TODO Auto-generated method stub
			this.noteSlotFlag = true;
		}

		@Override
		public void banknoteRemoved(BanknoteSlot slot) {
			// TODO Auto-generated method stub
			this.noteSlotFlag = true;
		}

		public boolean getFlag() {
			return this.noteSlotFlag;
		}

	}

	class CoinDispenserListenerInterface implements CoinDispenserListener {
		boolean coinDispenserFlag = false;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinsFull(CoinDispenser dispenser) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinsEmpty(CoinDispenser dispenser) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinAdded(CoinDispenser dispenser, Coin coin) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinRemoved(CoinDispenser dispenser, Coin coin) {
			// TODO Auto-generated method stub
			this.coinDispenserFlag = true;
		}

		@Override
		public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
			// TODO Auto-generated method stub

		}

		public boolean getFlag() {
			return this.coinDispenserFlag;
		}
	}

	class CoinTrayListenerInterface implements CoinTrayListener {
		boolean coinTrayFlag = false;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinAdded(CoinTray tray) {
			// TODO Auto-generated method stub
			this.coinTrayFlag = true;
		}

		public boolean getFlag() {
			return this.coinTrayFlag;
		}

	}
}
