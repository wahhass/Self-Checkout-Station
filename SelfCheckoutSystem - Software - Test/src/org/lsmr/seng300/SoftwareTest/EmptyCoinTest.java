package org.lsmr.seng300.SoftwareTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;

import CODE.EmptyCoin;

public class EmptyCoinTest {

			EmptyCoin EmptyCoinSoftware1;
			EmptyCoin EmptyCoinSoftware2;
			EmptyCoin EmptyCoinSoftware3;
			Currency CAD;
			Coin coin5;
			Coin coin10;
			Coin coin25;
			ArrayList<Coin> testUnloaded;

			@Before
			public void setup() throws SimulationException, OverloadException {
				CAD = Currency.getInstance(Locale.CANADA);
				coin5 = new Coin(new BigDecimal("0.05"), CAD);
				coin10 = new Coin(new BigDecimal("0.10"), CAD);
				coin25 = new Coin(new BigDecimal("0.25"), CAD);
				CoinStorageUnit CSU1 = new CoinStorageUnit(1000);
				CSU1.load(coin5, coin5, coin5);
				EmptyCoinSoftware1 = new EmptyCoin(CSU1);
				CoinStorageUnit CSU2 = new CoinStorageUnit(1000);
				CSU2.load(coin5, coin10, coin25);
				EmptyCoinSoftware2 = new EmptyCoin(CSU2);
				CoinStorageUnit CSU3 = new CoinStorageUnit(1000);
				EmptyCoinSoftware3 = new EmptyCoin(CSU3);
				
			}

			// Tests that when the coins to be unloaded are all the same, the correct list of coins is unloaded
			@Test
			public void testSameNote() throws DisabledException, OverloadException, EmptyException {
				testUnloaded = new ArrayList<Coin>();
				testUnloaded.add(coin5); 				
				testUnloaded.add(coin5);
				testUnloaded.add(coin5);
				Assert.assertEquals(testUnloaded, EmptyCoinSoftware1.EmptyCoinStorage());
			}

			// Tests that when the coins to be unloaded are different, the correct list of coins is unloaded
			@Test
			public void testDifferentNote() throws DisabledException, OverloadException, EmptyException {
				testUnloaded = new ArrayList<Coin>();
				testUnloaded.add(coin5);
				testUnloaded.add(coin10);
				testUnloaded.add(coin25);
				Assert.assertEquals(testUnloaded, EmptyCoinSoftware2.EmptyCoinStorage());
			}
			
			// Tests that when the coin storage unit is empty, the list of coins unloaded is empty
			@Test
			public void testEmptyStorage() throws DisabledException, OverloadException, EmptyException {
				testUnloaded = new ArrayList<Coin>();
				Assert.assertEquals(testUnloaded, EmptyCoinSoftware3.EmptyCoinStorage());
			}
	}

