package org.lsmr.seng300.SoftwareTest;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;

import CODE.LoginToControl;




public class LoginToControlTest {
	
	Card debitCard;
	Card attendantCard;
	Card attendantCard2;
	Card attendantCard3;
	Card attendantCard4;
	
	LoginToControl loginToControl;
	
	
	CardIssuer Store; 
	
	@Before
	public void setup() {
		
		debitCard = new Card("debit", "6750094156347755", "Bill", "056", "1234", true, true);
		attendantCard = new Card("attendant", "0019284527102937", "Bill", null, null, false, false);
		attendantCard2 = new Card("attendant", "0019284527102937", "Ryan", null, null, false, false);
		attendantCard3 = new Card("attendant", "0019284527102937", "Lee", null, null, false, false);
		attendantCard4 = new Card("attendant", "0123345678901234", "Tim", null, null, false, false);
		
		loginToControl = new LoginToControl();
		
		
		loginToControl.addToDatabase("12345", "Bill");
		loginToControl.addToDatabase("12367", "Ryan");
		loginToControl.addToDatabase("12398", "Lee");
	}
	
	/**
	 * Tests for a null attendant card
	 */
	@Test
	public void NullAttendantCard() {
		try {
			loginToControl.attendantLogIn(null, "12345");
			Assert.fail("Expected exception: Attendant Card is null");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Tests if swiping a valid attendant card
	 * with a null password works
	 */
	@Test
	public void SwipeMemberCardNullTotal() {
		
		try {
			loginToControl.attendantLogIn(attendantCard, null);
			Assert.fail("Expected exception: Password is null");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	
	}
	
	/**
	 * Tests if swiping a valid attendant card with incorrect password works
	 */
	@Test
	public void SwipeMemberCardZeroTotal() {
		
		try {
			loginToControl.attendantLogIn(attendantCard, "111");
			Assert.fail("Expected exception: Wrong Password Inputted");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	
	}
	
	/**
	 * Tests that a card which is not in the attendant database 
	 * cannot be used
	 */
	@Test
	public void SwipeMemberCardNotInSystem() {
		
		try {
			loginToControl.attendantLogIn(attendantCard4, "12398");
			Assert.fail("Expected exception: Attendant does not exist");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if swiping a valid attendant card works
	 * and password is correct
	 */
	@Test
	public void SwipeValidMemberCard() {

		try {
			
			loginToControl.attendantLogIn(attendantCard, "12345");
			Assert.assertEquals(true, loginToControl.isAttendantLoggedIn());
			loginToControl.attendantLogOut();
			
			loginToControl.attendantLogIn(attendantCard2, "12367");
			Assert.assertEquals(true, loginToControl.isAttendantLoggedIn());
			loginToControl.attendantLogOut();
			
			loginToControl.attendantLogIn(attendantCard3, "12398");
			Assert.assertEquals(true, loginToControl.isAttendantLoggedIn());
			loginToControl.attendantLogOut();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * Testing random swipe failures
	 * Swipe error rate = 0.11
	 */
	@Test
	public void randomSwipeFail() {
		
		int randomSwipeFailure = 0;
		
		for(int i = 0; i < 10000; i ++) {	//testing over 10,000 swipes how many fails

			try {
				loginToControl.attendantLogIn(attendantCard, "12345");
				loginToControl.attendantLogOut();
			} catch (Exception e){
				randomSwipeFailure++;		//increment number of fails
			}
		}
		
		Assert.assertTrue(randomSwipeFailure > 1000 && randomSwipeFailure < 1200);		//expected range of fails
		
	}
	
	/**
	 * Tests if card input is not a attendant card
	 */
	@Test
	public void NotAttendantCard() {
		try {
			loginToControl.attendantLogIn(debitCard, "12345");
			Assert.fail("Expected exception: Not an attendant card");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Cannot log in when already logged in
	 */
	
	@Test
	public void AlreadyLoggedIn() {
		try {
			loginToControl.attendantLogIn(attendantCard, "12345");
			loginToControl.attendantLogIn(attendantCard2, "12367");
			Assert.fail("Expected exception: Attendant already logged in");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Cannot log out when already logged out
	 */
	@Test
	public void AlreadyLoggedOut() {
		try {
			loginToControl.attendantLogOut();
			Assert.fail("Expected exception: No attendant is logged in");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}


}
