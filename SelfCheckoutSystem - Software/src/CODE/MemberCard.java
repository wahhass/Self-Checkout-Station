package CODE;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

/**
 * Class which allows the customer to
 * scan their membership card and utilize
 * their points
 */
public class MemberCard {

	public final CardReader reader;
	public CardReaderListenerInterface cardListener;
	String number, type, cardHolder;
	public BigDecimal points, pointsToAdd;
	MathContext m = new MathContext(2); // 2 precision
	
	//membership database which will hold "number, cardHolder" to search
	public static final Map<String, String> MEMBERSHIP_DATABASE = new HashMap<>(); 

	public MemberCard(String type, String number, String cardHolder, BigDecimal points) {
		reader = new CardReader();
		cardListener = new CardReaderListenerInterface();
		reader.register(cardListener);
		
		this.type = type;
		this.number = number;
		this.cardHolder = cardHolder;
		this.points = points;
		pointsToAdd = BigDecimal.ZERO;
	}
	
	/**
	 * Adds a membership card to the database
	 * @param number
	 * @param cardHolder
	 */
	public void addToDatabase(String number, String cardHolder) {
		
		MEMBERSHIP_DATABASE.put(number, cardHolder);
	
	}
	
	/**
	 * Allows the customer to tap swipe
	 * membership card
	 * @param memberCard
	 */
	public boolean useMemberCard(Card memberCard, BigDecimal total) throws IOException{
			
		CardData memberData = null;
		boolean cardRead = false;
			
		if (memberCard == null) {
			throw new SimulationException("Membership Card is null");
		}
		else {
			memberData = SwipeCard(memberCard);
			if (memberData != null && memberData == cardListener.getCardData() && cardListener.isCardSwiped()) {
				cardRead = true;
			}	
			
		}
		if (cardRead == true) {
			if (memberData.getType() == "member") {
				if(MEMBERSHIP_DATABASE.containsKey(memberData.getNumber())){ //checks for membership number in database
					addPoints(total);
					return true;
				} else {
					throw new SimulationException("Membership number does not exist");
				}
				
			} else {
				throw new SimulationException("Not a member card");
			} 
		} else {
			throw new SimulationException("Card not read");
		}
	}
	
	/**
	 * Allows member to manually put in their card information
	 * Returns true if membership info is in database
	 */
	
	public boolean inputMembershipInfo(String number, String cardholder, BigDecimal total) {
		
		if (number == null) {
			throw new SimulationException("Membership Number is null");
		}
		if (cardholder == null) {
			throw new SimulationException("Card Holder is null");
		}
		
		if(MEMBERSHIP_DATABASE.containsKey(number)){
			String realCardHolder = MEMBERSHIP_DATABASE.get(number);
			if (realCardHolder.equals(cardholder)) {
				addPoints(total);
				return true;
			}
			throw new SimulationException("Card holder does not match card number");
		}
		throw new SimulationException("Card number not in database");
	}

	/**
	 * Allows the customer to swipe their
	 * membership card
	 * @param memberCard
	 * @return card data
	 */
	private CardData SwipeCard(Card memberCard) {
		BufferedImage signature = new BufferedImage(1, 1, 1);
		try {
			return reader.swipe(memberCard, signature);
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	/**
	 * Adds points to a customers 
	 * membership card
	 * @param total
	 */
	private void addPoints(BigDecimal total) {
		if (total == null) {
			throw new SimulationException(new NullPointerException("Total is null"));
		} else if (total.compareTo(BigDecimal.ZERO) == 0){
			throw new SimulationException("Total is zero");
		} else if (total.compareTo(BigDecimal.ZERO) > 0) {
			pointsToAdd = pointsToAdd.ZERO;				//zeroing
			pointsToAdd = pointsToAdd.add(total);		//make equal to total
			
			pointsToAdd = pointsToAdd.multiply(new BigDecimal(0.05));
			pointsToAdd = pointsToAdd.round(m);			//round to 2 decimal places
			this.points = points.add(pointsToAdd); 		//adding points equivalent to 5% of purchase cost
		}
	}

	
	/**
	 * Customer may request to check their points
	 * @return points
	 */
	public BigDecimal checkPoints() {
		if (points == null){
			throw new SimulationException(new NullPointerException("Card has null points!"));
		}
			return this.points;
	}
	
	/**
	 * Customer may use their points toward their purchase
	 * @return new total
	 */
	public BigDecimal redeemPoints(Card memberCard, BigDecimal total, BigDecimal pointsToUse) {
//catch total errors
		if (total == null){
			throw new SimulationException(new NullPointerException("Total is null"));
		} else if (total.compareTo(new BigDecimal(0)) == 0) {
			throw new SimulationException("Total is zero"); 
		}
//catch pointsToUse errors	
		if (pointsToUse == null){
			throw new SimulationException(new NullPointerException("Trying to use null points"));
		} else if (pointsToUse.compareTo(new BigDecimal(0)) == 0) {
			throw new SimulationException("Trying to use no points"); 
		}
//catch points errors
		if (points == null){
			throw new SimulationException(new NullPointerException("Card has null points!"));
		} else if (points.compareTo(new BigDecimal(0)) == 0) {
			throw new SimulationException("Card has no points!"); 
		} 
		
		else if (points.compareTo(pointsToUse) < 0){			//card has less points than wanting to redeem
			
			total = total.subtract(points);						//subtract all points
			this.points = points.ZERO;							//zeroing the points (all used)		
			
		}else if (points.compareTo(pointsToUse) >= 0){			//card has at least the amount of points to redeem 
			if(pointsToUse.compareTo(total) < 0) {				//pointsToUse is less than the total
				
				total = total.subtract(pointsToUse);			//takes off equivalent cost of points indicated 
				this.points = points.subtract(pointsToUse);		//removes points from total points
				
			} else {
				if(points.compareTo(total) >= 0) {				//pointsToUse is greater than or equal to the total
					
					this.points = points.subtract(total);		//subtract all points
					total = total.ZERO;							//make total zero; all covered
				}
			}
		}
		return total;
	}

	/**
	 * Stubbing the CardReaderListener class
	 */
	class CardReaderListenerInterface implements CardReaderListener{
	
		private CardData data; 
		private boolean cardSwiped = false;
		public CardData getCardData() {
			return data;
		}
		
		public boolean isCardSwiped() {
			return cardSwiped;
		}
		
		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void cardInserted(CardReader reader) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void cardRemoved(CardReader reader) {
			// TODO Auto-generated method stub\
			
		}
	
		@Override
		public void cardTapped(CardReader reader) {
			
		}
	
		@Override
		public void cardSwiped(CardReader reader) {
			// TODO Auto-generated method stub
			cardSwiped = true;
			
		}
	
		@Override
		public void cardDataRead(CardReader reader, CardData data) {
			// TODO Auto-generated method stub
			
			this.data = data;
			
		}
		
		
		
	}
		
	
}
