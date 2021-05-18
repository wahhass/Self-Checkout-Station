package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;

import CODE.EmptyBanknote;

public class EmptyBanknoteTest {
		EmptyBanknote EmptyBanknoteSoftware1;
		EmptyBanknote EmptyBanknoteSoftware2;
		EmptyBanknote EmptyBanknoteSoftware3;
		Currency CAD;
		Banknote note5;
		Banknote note10;
		Banknote note100;
		ArrayList<Banknote> testUnloaded;

		@Before
		public void setup() throws SimulationException, OverloadException {
			CAD = Currency.getInstance(Locale.CANADA);
			BanknoteStorageUnit BSU1 = new BanknoteStorageUnit(1000);
			note5 = new Banknote(5, CAD);
			note10 = new Banknote(10, CAD);
			note100 = new Banknote(100, CAD);
			BSU1.load(note5, note5, note5);
			EmptyBanknoteSoftware1 = new EmptyBanknote(BSU1);
			BanknoteStorageUnit BSU2 = new BanknoteStorageUnit(1000);
			BSU2.load(note5, note10, note100);
			EmptyBanknoteSoftware2 = new EmptyBanknote(BSU2);
			BanknoteStorageUnit BSU3 = new BanknoteStorageUnit(1000);
			EmptyBanknoteSoftware3 = new EmptyBanknote(BSU3);
			
		}

		// Tests that when the banknotes to be unloaded are all the same, the correct list of banknotes is unloaded
		@Test
		public void testSameNote() throws DisabledException, OverloadException, EmptyException {
			testUnloaded = new ArrayList<Banknote>();
			testUnloaded.add(note5); 				
			testUnloaded.add(note5);
			testUnloaded.add(note5);
			Assert.assertEquals(testUnloaded, EmptyBanknoteSoftware1.EmptyBanknoteStorage());
		}

		// Tests that when the banknotes to be unloaded are different, the correct list of banknotes is unloaded
		@Test
		public void testDifferentNote() throws DisabledException, OverloadException, EmptyException {
			testUnloaded = new ArrayList<Banknote>();
			testUnloaded.add(note5);
			testUnloaded.add(note10);
			testUnloaded.add(note100);
			Assert.assertEquals(testUnloaded, EmptyBanknoteSoftware2.EmptyBanknoteStorage());
		}
		
		// Tests that when the banknote storage unit is empty, the list of banknotes unloaded is empty
		@Test
		public void testEmptyStorage() throws DisabledException, OverloadException, EmptyException {
			testUnloaded = new ArrayList<Banknote>();
			Assert.assertEquals(testUnloaded, EmptyBanknoteSoftware3.EmptyBanknoteStorage());
		}
}
