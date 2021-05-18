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

import CODE.MemberCard.CardReaderListenerInterface;


/*
 * To log into control an attendant swipes their attendant card 
 * and has to input password which matches their login credentials
 */
public class LoginToControl {
	
	public final CardReader reader;
	public CardReaderListenerInterface cardListener;
	String number, type, cardHolder;
	
	private boolean attendantLoggedIn;
	
	
	
	//attendant database which will hold "cardHolder, and password" for attendant login validation
		public static final Map<String, String> ATTENDANT_DATABASE = new HashMap<>(); 
	

	public LoginToControl() {
		reader = new CardReader();
		cardListener = new CardReaderListenerInterface();
		reader.register(cardListener);
		
		
		attendantLoggedIn = false;

	}
	
	/**
	 * Adds a attendant information to a database
	 * @param number
	 * @param cardHolder
	 * Hashmap created with key (Card Holder) and value (Password)
	 */
	public void addToDatabase(String password, String cardHolder) {
		
		ATTENDANT_DATABASE.put(cardHolder, password);
	
	}
	
	/**
	 * Allows the customer to swipe their
	 * attendant card and input their password
	 * @param attendantCard
	 * @param inputPassword
	 */
	public void attendantLogIn(Card attendantCard, String inputPassword) throws IOException{
		
		
		//Cannot log in if already logged
		if (attendantLoggedIn == true) {
			throw new SimulationException("Attendant already logged in");
		}
		
		//Data is initialized which will be compared to ensure card is swiped
		CardData attendantData = null;
		boolean cardRead = false;
		
		//Password cannot be null
		if (inputPassword == null) {
			throw new SimulationException("Password is null");
		}
		
		//Attendant card cannot be null
		if (attendantCard == null) {
			throw new SimulationException("Attendant Card is null");
		}
		
		//Swipe card and check if read
		else {
			attendantData = SwipeCard(attendantCard);
			if (attendantData != null && attendantData == cardListener.getCardData() && cardListener.isCardSwiped()) {
				cardRead = true;
			}	
			
		}
		
		//If card is read, make sure it is of right type, is in the database and has the right password
		if (cardRead == true) {
			if (attendantData.getType() == "attendant") {
				if(ATTENDANT_DATABASE.containsKey(attendantData.getCardholder())){ 
					String validPassword = ATTENDANT_DATABASE.get(attendantData.getCardholder());
					
					if (inputPassword.equals(validPassword)) {
						attendantLoggedIn = true;
					}
					else {
						throw new SimulationException("Wrong Password Inputted");
					}
					return;
				} else {
					throw new SimulationException("Attendant does not exist");
				}
				
			} else {
				throw new SimulationException("Not an attendant card");
			} 
		} else {
			throw new SimulationException("Card not read");
		}
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
	
	public void attendantLogOut() {
		
		if (attendantLoggedIn == false) {
			throw new SimulationException(" No attendant is logged in");
		}
		else {
			attendantLoggedIn = false;
		}
	}
	
	public boolean isAttendantLoggedIn() {
		return attendantLoggedIn;
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
