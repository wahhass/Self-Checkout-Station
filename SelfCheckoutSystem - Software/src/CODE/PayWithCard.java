package CODE;

import java.awt.image.BufferedImage;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystemNotFoundException;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;
import org.lsmr.selfcheckout.external.CardIssuer;


/**
 * 
 * @author Andrew Galbraith
 *A class which stimulates the software of paying for a card at the self checkout station
 *Can pay with debit card, credit card or gift cards
 */
public class PayWithCard {
	
	public final CardReader cardReader;
	public CardReaderListenerInterface cardReaderListener;
	
	
	
	public PayWithCard() {
		cardReader = new CardReader();
		cardReaderListener = new CardReaderListenerInterface();
		
		cardReader.register(cardReaderListener);
		
		
	}

	/**
	 * Pay With Debit Card is a software stimulation of paying with a Debit card
	 * 
	 * @param debitCard - has to be of type debit card and hold all other valid card criteria
	 * @param paymentOption - needs to be int between 0-2 (0 = tap, 1 = insert, 2 = swipe_
	 * @param totalBalence - the amount due at checkout cannot be greater than card limit
	 * @param pin - the inputed pin by customer
	 * @param bank - the bank the card is issued by
	 * @return boolean if false payment not successful if true payment was successful
	 */
	
	public boolean PayWithDebitCard(Card debitCard, int paymentOption, BigDecimal totalBalence, String pin, CardIssuer bank){
		
		CardData cardData = null;
		boolean cardRead;
		cardRead = false;
		
		//Check if debit card is null
		if (debitCard == null) {
			throw new SimulationException(new NullPointerException("Debit Card is null."));
		}
		else {
			//Check if payment type is invalid
			if (paymentOption > 2 || paymentOption < 0) {
				throw new SimulationException("Invalid Payment Option");
			}
			switch (paymentOption) {
				
				//Whether the card is tapped, inserted or swiped the listener is checked to ensure card was read
				case 0: 
					cardData = TapCard(debitCard);
					if (cardData != null && cardData == cardReaderListener.getCardData() && cardReaderListener.isCardTapped()) {
						cardRead = true;
					}
					break;
				case 1:
					cardData = InsertCard(debitCard, pin);
					if (cardData != null && cardData == cardReaderListener.getCardData() && cardReaderListener.isCardInserted()) {
						cardRead = true;
					}
					break;
				case 2:
					
					cardData = SwipeCard(debitCard);
					if (cardData != null && cardData == cardReaderListener.getCardData() && cardReaderListener.isCardSwipped()) {
						cardRead = true;
					}
					break;
					
			}
			//Check if card was read
			if (cardRead == true) {
				
				//Ensures debit card was used
				if (cardData.getType() == "debit") {
					//Hold is placed on amount due - if transaction is successful, hold is released
					int holdNumber = bank.authorizeHold(cardData.getNumber(), totalBalence);
					if (bank.postTransaction(cardData.getNumber(), holdNumber, totalBalence)) {
						if (bank.releaseHold(cardData.getNumber(), holdNumber)) {
							return true;
						}
					}
					throw new SimulationException("Bank Rejected Card");
					
				}
				else {
					throw new SimulationException("Not a debit card");
				}
				
			}
			else {

				throw new SimulationException("Card not read");
			}
		}
	}
	
	/**
	 * Pay With Credit Card is a software stimulation of paying with a credit card
	 * 
	 * @param creditCard - has to be of type credit card and hold all other valid card criteria
	 * @param paymentOption - needs to be int between 0-2 (0 = tap, 1 = insert, 2 = swipe_
	 * @param totalBalence - the amount due at checkout cannot be greater than card limit
	 * @param pin - the inputed pin by customer
	 * @param bank - the bank the card is issued by
	 * @return boolean if false payment not successful if true payment was successful
	 * 
	 * Note code is mostly redundant to pay with debit however, two seperate functions were created since these are two unique use cases
	 */
	
	public boolean PayWithCreditCard(Card creditCard, int paymentOption, BigDecimal totalBalence, String pin, CardIssuer bank){
		CardData cardData = null;
		boolean cardRead = false;
		
		//Check if credit card is null
		if (creditCard == null) {
			throw new SimulationException(new NullPointerException("Debit Card is null."));
		}
		//Check if payment type is invalid
		else {
			if (paymentOption > 2 || paymentOption < 0) {
				throw new SimulationException("Invalid Payment Option");
			}
			switch (paymentOption) {
			//Whether the card is tapped, inserted or swiped the listener is checked to ensure card was read
				case 0: 
					cardData = TapCard(creditCard);
					if (cardData != null && cardData == cardReaderListener.getCardData() && cardReaderListener.isCardTapped()) {
						cardRead = true;
						
					}
					break;
				case 1:
					cardData = InsertCard(creditCard, pin);
					if (cardData != null && cardData == cardReaderListener.getCardData() && cardReaderListener.isCardInserted()) {
						
						cardRead = true;
						
					}
					break;
				case 2:
					
					cardData = SwipeCard(creditCard);
					if (cardData != null && cardData == cardReaderListener.getCardData() && cardReaderListener.isCardSwipped()) {
						cardRead = true;
					}
					break;
					
			}
			//Check if card was read
			if (cardRead == true) {
				//Ensures credit card was used
				if (cardData.getType() == "credit") {
					//Hold is placed on amount due - if transaction is successful, hold is released
					int holdNumber = bank.authorizeHold(cardData.getNumber(), totalBalence);
					if (bank.postTransaction(cardData.getNumber(), holdNumber, totalBalence)) {
						if (bank.releaseHold(cardData.getNumber(), holdNumber)) {
							return true;
						}
					}
					
					throw new SimulationException("Bank Rejected Card");
					
				}
				else {
					
					throw new SimulationException("Not a debit card");
				}
				
			}
			else {

				throw new SimulationException("Card not read");
			}
		}
	}
	
	/**
	 * Pay With Gift Card is a software stimulation of paying with a gift card
	 * 
	 * @param giftCard - has to be of type gift card and needs a card number
	 * @param totalBalence - the amount due at checkout cannot be greater than gift card limit
	 * @param coop - coop is the assumed issuer of the card
	 * @return boolean if false payment not successful if true payment was successful
	 * 
	 */
	
	public boolean PayWithGiftCard(Card giftCard, BigDecimal totalBalence, CardIssuer coop){
		CardData cardData = null;
		boolean cardRead = false;
		
		//Check if credit card is null
		if (giftCard == null) {
			throw new SimulationException(new NullPointerException("Gift Card is null."));
		}
		//Check if payment type is invalid
		else {
					
			cardData = SwipeCard(giftCard);
			if (cardData != null && cardData == cardReaderListener.getCardData() && cardReaderListener.isCardSwipped()) {
				cardRead = true;
					
			}
			//Check if card was read
			if (cardRead == true) {
				//Ensures credit card was used
				if (cardData.getType() == "gift card") {
					//Hold is placed on amount due - if transaction is successful, hold is released
					int holdNumber = coop.authorizeHold(cardData.getNumber(), totalBalence);
					if (coop.postTransaction(cardData.getNumber(), holdNumber, totalBalence)) {
						if (coop.releaseHold(cardData.getNumber(), holdNumber)) {
							return true;
						}
					}
					
					throw new SimulationException("Coop Rejected Gift Card");
					
				}
				else {
					
					throw new SimulationException("Not a gift card");
				}
				
			}
			else {

				throw new SimulationException("Gift not read");
			}
		}
	}
	
	
	
	
	
	/**
	 * 
	 * @param card
	 * @return the data of tapped card
	 */
	private CardData TapCard(Card card) {
		try {
			return cardReader.tap(card);
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param card
	 * @param pin - must match the pin of card else error is thrown
	 * @return the data of inserted card
	 */
	private CardData InsertCard(Card card, String pin) {
		try {
			return cardReader.insert(card, pin);
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param card
	 * @return the data of swiped card
	 */
	private CardData SwipeCard(Card card) {
		BufferedImage signature = new BufferedImage(1, 1, 1);
		try {
			return cardReader.swipe(card, signature);
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	//Removes card from slot
	public void removeCard() {
		cardReader.remove();
	}
	
	/**
	 * 
	 * @author Andrew Galbraith
	 *
	 *A Listener which is called upon to ensure hardware processing of tap, swipe and insert was succesful
	 *
	 *
	 */
	class CardReaderListenerInterface implements CardReaderListener{
		
		private CardData data; 
		private boolean cardInserted = false;
		private boolean cardSwipped = false;
		private boolean cardTapped = false;
		
		public CardData getCardData() {
			return data;
		}
		public boolean isCardInserted() {
			return cardInserted;
		}
		public boolean isCardSwipped() {
			return cardSwipped;
		}
		public boolean isCardTapped() {
			return cardTapped;
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
			cardInserted = true;
		}

		@Override
		public void cardRemoved(CardReader reader) {
			// TODO Auto-generated method stub\
			cardInserted = false;
			
		}

		@Override
		public void cardTapped(CardReader reader) {
			// TODO Auto-generated method stub
			cardTapped = true;
			
		}

		@Override
		public void cardSwiped(CardReader reader) {
			// TODO Auto-generated method stub
			cardSwipped = true;
			
		}

		@Override
		public void cardDataRead(CardReader reader, CardData data) {
			// TODO Auto-generated method stub
			
			this.data = data;
			
		}
		
		
		
	}
}
