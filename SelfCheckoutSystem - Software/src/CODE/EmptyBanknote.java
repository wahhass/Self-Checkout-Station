package CODE;

import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteStorageUnitListener;

import org.lsmr.selfcheckout.devices.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lsmr.selfcheckout.Banknote;

public class EmptyBanknote {
	/**
	 * @Param BSU: The BanknoteStorageUnit to be emptied, which is taken as a
	 *        parameter
	 * @Param listener: A BanknoteStorageUnit listener to be registered
	 * @Param amountUnloaded: A counter to track the amount of banknotes unloaded
	 * @Param unloadedNotes: An array list of all the banknote objects that were
	 *        unloaded
	 * @Param initialNotes: The amount of bankenotes that were present in the
	 *        banknote storage unit initially
	 */

	public BanknoteStorageUnit BSU;
	public BanknoteStorageUnitListenerInterface listener;
	public boolean noteUnloaded;
	public List<Banknote> unloadedNotes;
	public int initialNotes;

	public EmptyBanknote(BanknoteStorageUnit BSU) {
		this.BSU = BSU;
		listener = new BanknoteStorageUnitListenerInterface();
		this.BSU.register(listener);
		this.noteUnloaded = false;
		this.initialNotes = 0;
		this.unloadedNotes = new ArrayList<Banknote>();
	}

	/**
	 * Unloads a banknote storage unit and returns a list of unloaded banknotes
	 * 
	 * @return
	 */
	public List<Banknote> EmptyBanknoteStorage() {
		initialNotes = BSU.getBanknoteCount();
		unloadedNotes.addAll(BSU.unload());
		while(unloadedNotes.remove(null)) {
			}
			if (listener.getFlag() == true) {
				noteUnloaded = true;
			}
		
		if (noteUnloaded == true && unloadedNotes.size() == initialNotes)
			System.out.println("Banknotes have been emptied");
		return unloadedNotes;
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

		}

		@Override
		public void banknoteAdded(BanknoteStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknotesLoaded(BanknoteStorageUnit unit) {
			// TODO Auto-generated method stub

		}

		@Override
		public void banknotesUnloaded(BanknoteStorageUnit unit) {
			// TODO Auto-generated method stub
			this.storageFlag = true;
		}

		public boolean getFlag() {
			return this.storageFlag;
		}

	}
}
