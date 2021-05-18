package CODE;

import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;
import org.lsmr.selfcheckout.devices.*;

import java.util.ArrayList;
import java.util.List;

import org.lsmr.selfcheckout.Coin;



public class EmptyCoin {
	
	/**
	 * @Param CSU: The CoinStorageUnit to be emptied, which is taken as a parameter
	 * @Param listener: A CoinStorageUnit listener to be registered
	 * @Param amountUnloaded: A counter to track the amount of coins unloaded
	 * @Param unloadedCoins: An array list of all the coin objects that were unloaded
	 * @Param initialCoins: The amount of coins that were present in the coin storage unit initially
	 */

	public CoinStorageUnit CSU;
	public CoinStorageUnitListenerInterface listener;
	public boolean coinUnloaded;
	public List<Coin> unloadedCoins;
	public int initialCoins;
	
	/**
	 * Constructor that takes in a CoinStorageUnit as a parameter
	 * @param CSU
	 */

	public EmptyCoin(CoinStorageUnit CSU) {
		this.CSU = CSU;
		listener = new CoinStorageUnitListenerInterface();
		this.CSU.register(listener);
		this.coinUnloaded = false;
		this.initialCoins = 0;
		this.unloadedCoins = new ArrayList<Coin>();
	}

	public List<Coin> EmptyCoinStorage() {
		initialCoins = CSU.getCoinCount();
		unloadedCoins.addAll(CSU.unload());
		while(unloadedCoins.remove(null)) {
			}
			if (listener.getFlag() == true) {
				coinUnloaded = true;
			}
		if (coinUnloaded == true && unloadedCoins.size() == initialCoins)
		System.out.println("Coins have been emptied");
		return unloadedCoins;
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
			// TODO Auto-generated method stub

		}

		@Override
		public void coinAdded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinsLoaded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		@Override
		public void coinsUnloaded(CoinStorageUnit unit) {
			// TODO Auto-generated method stub
			this.storageFlag = true;
		}

		public boolean getFlag() {
			return this.storageFlag;
		}

	}
}
