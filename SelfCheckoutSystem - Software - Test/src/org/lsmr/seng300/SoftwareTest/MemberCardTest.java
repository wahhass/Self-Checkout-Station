package org.lsmr.seng300.SoftwareTest;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;

import CODE.MemberCard;

public class MemberCardTest {

	Card debitCard;
	Card memberCard;
	Card memberCard2;
	Card memberCard3;
	Card memberCard4;
	
	MemberCard member;
	MemberCard member2;
	MemberCard member3;
	MemberCard member4;
	
	CardIssuer Store; 
	
	@Before
	public void setup() {
		
		debitCard = new Card("debit", "6750094156347755", "Billy", "056", "1234", true, true);
		memberCard = new Card("member", "0019284527102937", "Joe", null, null, false, false);
		memberCard2 = new Card("member", "0019284527102937", "Joe", null, null, false, false);
		memberCard3 = new Card("member", "0019284527102937", "Joe", null, null, false, false);
		memberCard4 = new Card("member", "0123345678901234", "Tim", null, null, false, false);
		
		member = new MemberCard("member", "0019284527102937", "Joe", new BigDecimal(30));
		member2 = new MemberCard("member", "0019284527102937", "Joe", new BigDecimal(0));
		member3 = new MemberCard("member", "0019284527102937", "Joe", null);
		member4 = new MemberCard("member", "0123345678901234", "Tim", new BigDecimal(20));
		
		member.addToDatabase("0019284527102937", "Joe");
		
	}
	
	/**
	 * Tests for a null membership card
	 */
	@Test
	public void NullMemberCard() {
		try {
			member.useMemberCard(null, new BigDecimal(20));
			Assert.fail("Expected exception: Membership Card is null");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Tests for a null card number input
	 */
	@Test
	public void NullMemberNumber() {
		try {
			member.inputMembershipInfo(null, "Joe", new BigDecimal(20));
			Assert.fail("Expected exception: Membership Number is null");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Tests for a null card holder input
	 */
	@Test
	public void NullMemberCardHolder() {
		try {
			member.inputMembershipInfo("0019284527102937", null , new BigDecimal(20));
			Assert.fail("Expected exception: Card Holder is null");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Tests for a non valid card number input
	 */
	@Test
	public void InvalidMemberNumber() {
		try {
			member.inputMembershipInfo("0019237", "Joe", new BigDecimal(20));
			Assert.fail("Expected exception: Card number not in database");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Tests for a non valid card number input
	 */
	@Test
	public void MemberNumberDoesNotMatchHolder() {
		try {
			member.inputMembershipInfo("0019284527102937", "Bill", new BigDecimal(20));
			Assert.fail("Card holder does not match card number");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Tests for a valid membership input
	 */
	
	@Test
	public void ValidMemberInput() {

		try {
			Assert.assertTrue(member.inputMembershipInfo("0019284527102937", "Joe", new BigDecimal(200)));
			Assert.assertEquals(new BigDecimal(40), member.checkPoints());	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	/**
	 * Tests if swiping a valid member card
	 * with a null total works
	 */
	@Test
	public void SwipeMemberCardNullTotal() {
		
		try {
			member.useMemberCard(memberCard, null);
			Assert.fail("Expected exception: Total is null");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	
	}
	
	/**
	 * Tests if swiping a valid member card
	 * with a total of 0 works
	 */
	@Test
	public void SwipeMemberCardZeroTotal() {
		
		try {
			member.useMemberCard(memberCard, BigDecimal.ZERO);
			Assert.fail("Expected exception: Total is zero");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	
	}
	
	/**
	 * Tests that a card which is not in the system 
	 * can't be used
	 */
	@Test
	public void SwipeMemberCardNotInSystem() {
		
		try {
			member4.useMemberCard(memberCard4, new BigDecimal(20));
			Assert.fail("Expected exception: Membership number does not exist");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if swiping a valid member card works
	 * and points are properly added
	 */
	@Test
	public void SwipeValidMemberCard() {

		try {
			Assert.assertTrue(member.useMemberCard(memberCard, new BigDecimal(200)));
			Assert.assertEquals(new BigDecimal(40), member.checkPoints());	
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * Testing random swipe failures
	 * Swipe error rate = 0.11
	 */
	@Test
	public void randomFail() {
		
		int randomSwipeFailure = 0;
		
		for(int i = 0; i < 10000; i ++) {	//testing over 10,000 swipes how many fails

			try {
				member.useMemberCard(memberCard, new BigDecimal(10));
			} catch (Exception e){
				randomSwipeFailure++;		//increment number of fails
			}
		}
		
		Assert.assertTrue(randomSwipeFailure > 1000 && randomSwipeFailure < 1200);		//expected range of fails
		
	}
	
	/**
	 * Tests if card input is not a member card
	 */
	@Test
	public void NotMemberCard() {
		try {
			member.useMemberCard(debitCard, new BigDecimal(20));
			Assert.fail("Expected exception: Not a member card");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
	}
	
	/**
	 * Tests if invalid card 
	 * with null points throws error
	 */
	@Test
	public void CheckNullPoints() {
		
		try {
			member3.checkPoints();
			Assert.fail("Expected exception: Card has null points!");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if valid card with 
	 * points returns correct amount
	 */
	@Test
	public void checkPoints() {
		
	Assert.assertEquals(new BigDecimal(30), member.checkPoints());
		
	}
	
	/**
	 * Tests if valid card with zero
	 * points returns correct amount
	 */
	@Test
	public void checkZeroPoints() {
		
	Assert.assertEquals(new BigDecimal(0), member2.checkPoints());
		
	}
	
	/**
	 * Tests if a member card with null
	 * total can be used
	 */
	@Test
	public void redeemNullTotal() {
		
		try {
			member.redeemPoints(memberCard, null, new BigDecimal(30));
			Assert.fail("Expected exception: Total is null");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if a member card with a
	 * total of zero can be used
	 */
	@Test
	public void redeemNoTotal() {
		
		try {
			member.redeemPoints(memberCard, new BigDecimal(0), new BigDecimal(30));
			Assert.fail("Expected exception: Total is zero");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if a member card with null
	 * points for redemption can be used
	 */
	@Test
	public void redemptionOfNull() {
		
		try {
			member.redeemPoints(memberCard, new BigDecimal(30), null);
			Assert.fail("Expected exception: Trying to use null points");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if a member card with no
	 * points for redemption can be used
	 */
	@Test
	public void redemptionOfZero() {
		
		try {
			member.redeemPoints(memberCard, new BigDecimal(30), new BigDecimal(0));
			Assert.fail("Expected exception: Trying to use no points");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if a member card with no
	 * points can be used
	 */
	@Test
	public void redeemNoPoints() {
		
		try {
			member2.redeemPoints(memberCard2, new BigDecimal(30), new BigDecimal(30));
			Assert.fail("Expected exception: Card has no points!");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if a member card with null
	 * points can be used
	 */
	@Test
	public void redeemNullPoints() {
		
		try {
			member3.redeemPoints(memberCard3, new BigDecimal(30), new BigDecimal(30));
			Assert.fail("Expected exception: Card has null points!");
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof SimulationException);
		}
		
	}
	
	/**
	 * Tests if a valid member card with 
	 * enough points can be used to cover entire total
	 */
	@Test
	public void redeemAllPointsAllTotal() {
		
		Assert.assertEquals(new BigDecimal(0), member.redeemPoints(memberCard, new BigDecimal(30), new BigDecimal(30)));
		
	}
	
	/**
	 * Tests if a valid member card with 
	 * enough points can be used to reduce total
	 */
	@Test
	public void redeemSomePointsSomeTotal() {
		
		Assert.assertEquals(new BigDecimal(30), member.redeemPoints(memberCard, new BigDecimal(50), new BigDecimal(20)));
		Assert.assertEquals(new BigDecimal(10), member.checkPoints());
		
	}
	
	/**
	 * Tests if a valid member card can
	 * use all points to cover some total
	 */
	@Test
	public void redeemAllPointsSomeTotal() {
		
		Assert.assertEquals(new BigDecimal(20), member.redeemPoints(memberCard, new BigDecimal(50), new BigDecimal(50)));
		Assert.assertEquals(new BigDecimal(0), member.checkPoints());
		
	}
	
	/**
	 * Tests if a valid member card can
	 * use all points to cover some total
	 */
	@Test
	public void redeemSomePointsAllTotal() {
		
		Assert.assertEquals(new BigDecimal(0), member.redeemPoints(memberCard, new BigDecimal(10), new BigDecimal(20)));
		Assert.assertEquals(new BigDecimal(20), member.checkPoints());
		
	}


}
