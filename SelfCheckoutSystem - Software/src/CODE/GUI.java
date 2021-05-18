package CODE;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.TouchScreen;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;

/** 
 * The GUI class implements the workflow of the GUI by executing actions based off of button presses. 
 * The beginning of the class has all relevant GUI assets declared and initialized.
 * This is followed by the constructor and constructor methods for each panel. 
 * The main initializes all relevant parameters and launches the GUI. 
 * 
 * @author Alexander Vanc
 *
 */
public class GUI {
	
	// Parameters for SelfCheckoutStation
	public static final Currency CAD = Currency.getInstance(Locale.CANADA);
	public static final int[] banknoteDenominations = {5, 10, 20, 50, 100, 500};
	public static final BigDecimal[] coinDenominations = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
	private static final int MAXIMUM_INK = 1 << 20;
	private static final int MAXIMUM_PAPER = 1 << 10;
	
	// JPanel for containing other machine workspaces
	JPanel container = new JPanel();
	CardLayout cardLayout = new CardLayout();
	
	// JPanel for start up
	JPanel startUp = new JPanel();
	public static volatile JButton startUpButton = new JButton("Start Up");
	
	// JPanel for machine welcome
	JPanel welcome = new JPanel();
	public static volatile JButton startButton = new JButton("Start");
	public static volatile JButton ownBagsButton = new JButton("I brought my own bag");
	
	// JPanel for membership entry 
	JPanel enterMemberNum = new JPanel();
	JLabel memberLabel = new JLabel("Please enter your member information or scan your card.");
	JLabel memberNumLabel = new JLabel("Number:");
	JLabel memberNameLabel = new JLabel("Name:");
	public static volatile JFormattedTextField memberNumField = new JFormattedTextField("");
	public static volatile JFormattedTextField memberNameField = new JFormattedTextField("");
	public static volatile JButton memberNumEnterButton = new JButton("Enter");
	public static volatile JButton nonMemberButton = new JButton("Non-Member");
	public static volatile JButton memberCancelButton = new JButton("Cancel");
	public static volatile JButton memberScanButton = new JButton("Scan");
	
	// JPanel for scanning and bagging items
	JPanel scanAndBag = new JPanel();
	JLabel runningTotalLabel = new JLabel("Running total: $" + cart.getCumPrice());
	JList<CartProduct> itemsScanned = new JList(dlm);
	public static volatile JButton lookUpItemButton = new JButton("Look Up Item");
	public static volatile JButton keyInCodeButton = new JButton("Key In Code");
	public static volatile JButton scanButton = new JButton("Scan Item");
	public static volatile JButton finishAndPayButton = new JButton("Finish & Pay");
	public static volatile JButton scanCancelButton = new JButton("Cancel Items");
	public static volatile JButton scanHelpButton = new JButton("Help");
	
	// Buttons for product library
	public static volatile JButton apples = new JButton("Apples");
	public static volatile JButton apricots = new JButton("Apricots");
	public static volatile JButton mushrooms = new JButton("Mushrooms");
	public static volatile JButton bananas = new JButton("Bananas");
	public static volatile JButton lemons = new JButton("Lemons");
	public static volatile JButton broccoli = new JButton("Broccoli");
	
	// JPanel for looking up product 
	JPanel lookUpProduct = new JPanel();
	public static volatile JButton lookUpGoBackButton = new JButton("Go Back");
	public static volatile JButton lookUpHelpButton = new JButton("Help");
	
	// JPanel for keying in item code
	JPanel keyInCode = new JPanel();
	JLabel keyInCodeLabel = new JLabel("Please enter the PLU of your product.");
	public static volatile JFormattedTextField itemCodeField = new JFormattedTextField();
	public static volatile JButton itemCodeEnterButton = new JButton("Enter");
	public static volatile JButton keyInGoBackButton = new JButton("Go Back");
	public static volatile JButton keyInHelpButton = new JButton("Help");
	
	// JPanel for selecting payment type
	JPanel selectPaymentType = new JPanel();
	JLabel paymentTypeLabel = new JLabel("Please select your payment type.");
	public static volatile JButton cashButton = new JButton("Cash");
	public static volatile JButton creditButton = new JButton("Credit");
	public static volatile JButton debitButton = new JButton("Debit");
	public static volatile JButton giftCardButton = new JButton("Gift Card");
	public static volatile JButton paymentGoBackButton = new JButton("Go Back");
	
	//JPanel for pay with credit
	JPanel payWithCredit = new JPanel();
	public static volatile JButton swipeCredit = new JButton("Swipe");
	public static volatile JButton tapCredit = new JButton("Tap");
	public static volatile JButton insertCredit = new JButton("Insert");
	
	//JPanel for pay with debit
	JPanel payWithDebit = new JPanel();
	public static volatile JButton swipeDebit = new JButton("Swipe");
	public static volatile JButton tapDebit = new JButton("Tap");
	public static volatile JButton insertDebit = new JButton("Insert");
	
	
	// JPanel for pay with cash
	JPanel payWithCash = new JPanel();
	JLabel outstandingBalance = new JLabel("Outstanding Balance: $" + cart.getCumPrice());
	public static volatile JButton nickel = new JButton("Insert $0.05");
	public static volatile JButton dime = new JButton("Insert $0.10");
	public static volatile JButton quarter = new JButton("Insert $0.25");
	public static volatile JButton loonie = new JButton("Insert $1.00");
	public static volatile JButton toonie = new JButton("Insert $2.00");
	public static volatile JButton five = new JButton("Insert $5.00");
	public static volatile JButton ten = new JButton("Insert $10.00");
	public static volatile JButton twenty = new JButton("Insert $20.00");
	public static volatile JButton fifty = new JButton("Insert $50.00");
	public static volatile JButton hundred = new JButton("Insert $100.00");
	
	
	// JPanel for selecting number of bags used
	JPanel selectNumberOfBags = new JPanel();
	JLabel numberOfBagsLabel = new JLabel("Enter the number of bags used \n or 0 if non were used.");
	public static volatile JFormattedTextField numberOfBagsField = new JFormattedTextField();
	public static volatile JButton bagEnterButton = new JButton("Enter");
	public static volatile JButton bagGoBackButton = new JButton("Go Back");
	
	// Bagging pending screen
	JPanel baggingPending = new JPanel();
	public static volatile JButton bagItemButton = new JButton("Bag Item");
	public static volatile JButton dontBagItemButton = new JButton("Don't Bag Item");
	
	// Bagging issue screen
	JPanel baggingIssue = new JPanel();
	JLabel baggingIssuePrompt = new JLabel("Enter attendant password to override scale.");
	public static volatile JFormattedTextField baggingIssueField = new JFormattedTextField();
	public static volatile JButton baggingIssueOverrideButton = new JButton("Override");
	public static volatile JButton baggingIssueRejectButton = new JButton("Reject");
	
	// JPanel for checkout complete screen
	JPanel takeReceipt = new JPanel();
	JLabel change = new JLabel("Change due: ");
	public static volatile JButton takeReceiptButton = new JButton("Take Receipt");
	JList<CartProduct> receipt = new JList(dlm);
	
	// JPanel for attendant login 
	JPanel attendantLogin = new JPanel();
	JLabel attendantLoginPrompt = new JLabel("Scan your card and enter your password.");
	public static volatile JFormattedTextField attendantLoginField = new JFormattedTextField();
	public static volatile JButton attendantLoginButton = new JButton("Login");
	public static volatile JButton attendantGoBackButton = new JButton("Go Back");
	
	// JPanel for attendant functions 
	JPanel attendantFunctions = new JPanel();
	public static volatile JButton attendantLogoutButton = new JButton("Logout");
	public static volatile JButton attendantControlGoBackButton = new JButton("Go Back");
	public static volatile JButton refillPrinterInk = new JButton("Refill Ink");
	public static volatile JButton refillPrinterPaper = new JButton("Refill Paper");
	public static volatile JButton emptyCoin = new JButton("Empty Coin Storage");
	public static volatile JButton emptyBanknote = new JButton("Empty Banknote Storage");
	public static volatile JButton refillCoin = new JButton("Refill Coins");
	public static volatile JButton refillBanknote = new JButton("Refill Banknotes");
	public static volatile JButton removeItem = new JButton("Remove item");
	public static volatile JButton blockStation = new JButton("Block Station");
	public static volatile JButton shutdownStation = new JButton("Shut Down Station");
	
	// JPanel for station blocked
	JPanel stationBlocked = new JPanel();
	JLabel stationBlockedPrompt = new JLabel("Enter attendant password to unblock.");
	public static volatile JFormattedTextField stationBlockedField = new JFormattedTextField();
	public static volatile JButton stationBlockedButton = new JButton("Unblock");
	
	// Declaring software classes
	public static itemInBaggingAreaV2 baggingArea;
	public static productScan scanner;
	public static EnterPLUCode plu;
	public static Cart cart = new Cart();
	public static DefaultListModel<CartProduct> dlm = new DefaultListModel();
	public static PayWithCard cardPayment;
	public static PayWithCoin coinPayment;
	public static PayWithNote notePayment;
	public static PrintReceipt receiptPrinter;
	public static LoginToControl staffLogin;
	public static EmptyBanknote emptyNotes;
	public static EmptyCoin emptyCoins;
	public static GiveChange giveChange;
	public static MemberCard memberCard;
	
	// Declaring other relevant parameters
	public static Calendar expiry = Calendar.getInstance();
	public static Item itemForBag;
	public static CartProduct tempProduct;
	
	// Constructor 
	public GUI(SelfCheckoutStation selfCheckout) {
		
		container.setLayout(cardLayout);
		
		container.add(startUp, "startup");
		container.add(welcome, "welcome");
		container.add(scanAndBag, "scan");
		container.add(lookUpProduct, "lookup");
		container.add(keyInCode, "keyin");
		container.add(selectNumberOfBags, "selectbags");
		container.add(enterMemberNum, "member");
		container.add(selectPaymentType, "paymenttype");
		container.add(payWithCredit, "credit");
		container.add(payWithDebit, "debit");
		container.add(payWithCash, "cash");
		container.add(takeReceipt, "receipt");
		container.add(baggingPending, "bagitem");
		container.add(attendantLogin, "attendant");
		container.add(attendantFunctions, "control");
		container.add(stationBlocked, "blocked");
		container.add(baggingIssue, "issue");
		cardLayout.show(container, "startup");
		
		constructStartUpPanel();
		constructWelcomePanel();
		constructEnterMemberNumPanel();
		constructScanPanel();
		constructLookUpProductPanel();
		constructKeyInCodePanel();
		constructSelectNumberOfBagsPanel();
		constructSelectPaymentPanel();
		constructPayWithCreditPanel();
		constructPayWithDebitPanel();
		constructPayWithCashPanel();
		constructGiveChangeAndReceiptPanel();
		constructBagginPendingPanel();
		constructAttendantLoginPanel();
		constructAttendantFunctionsPanel();
		constructStationBlockedPanel();
		constructBaggingIssuePanel();
		
		selfCheckout.screen.getFrame().add(container);
		selfCheckout.screen.getFrame().setVisible(true);
		
	}
	
	// Constructor for panels which are called in the class constructor above
	public void constructStartUpPanel() {
		
		// Format the panel
		startUp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		startUp.add(startUpButton, gbc);
		startUpButton.setPreferredSize(new Dimension(200, 120));
		
		// Action listener for startUpButton
		startUpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "welcome");
			}
		});
		
	}
	
	public void constructWelcomePanel() {
		
		// Format the panel
		welcome.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		
		welcome.add(startButton, gbc);
		startButton.setPreferredSize(new Dimension(200, 120));
		welcome.add(ownBagsButton, gbc);
		ownBagsButton.setPreferredSize(new Dimension(200, 120));
		
		// Action listener for startButton
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "scan");
			}
		});
		
		// Action listener for ownBagsButton
		ownBagsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Bag bags = new Bag(1.0);
				try {
					baggingArea.placeOwnBag(bags);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void constructScanPanel() {
		
		// Format the panel
		scanAndBag.setLayout(new GridLayout(2,2));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		JPanel runningTotal, scanFunctions, groceryList, utilities;
		runningTotal = new JPanel();
		scanFunctions = new JPanel();
		groceryList = new JPanel();
		utilities = new JPanel();
		runningTotal.setLayout(new GridBagLayout());
		scanFunctions.setLayout(new GridBagLayout());
		groceryList.setLayout(new GridBagLayout());
		utilities.setLayout(new GridBagLayout());
		
		scanAndBag.add(runningTotal);
		scanAndBag.add(scanFunctions);
		scanAndBag.add(groceryList);
		scanAndBag.add(utilities);
		
		runningTotal.add(runningTotalLabel, gbc);
		runningTotalLabel.setFont(new Font(null, 1, 40));
		
		gbc.gridx = 0; gbc.gridy = 0;
		scanFunctions.add(lookUpItemButton, gbc);
		lookUpItemButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 0;
		scanFunctions.add(keyInCodeButton, gbc);
		keyInCodeButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 2;
		scanFunctions.add(scanButton, gbc);
		scanButton.setPreferredSize(new Dimension(400, 120));
		
		groceryList.add(itemsScanned);
		itemsScanned.setFont(new Font(null, 1, 30));
		
		gbc.gridx = 0; gbc.gridy = 0;
		utilities.add(finishAndPayButton, gbc);
		finishAndPayButton.setPreferredSize(new Dimension(400, 120));
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 1;
		utilities.add(scanCancelButton, gbc);
		scanCancelButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 1;
		utilities.add(scanHelpButton, gbc);
		scanHelpButton.setPreferredSize(new Dimension(200, 120));
		
		// Action Listener for lookUpItemButton
		lookUpItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "lookup");
			}
		});
		
		// Action Listener for keyInCodeButton
		keyInCodeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "keyin");
			}
		});
		
		// Action Listener for scanButton
		scanButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BarcodedItem item = new BarcodedItem(new Barcode("1111"), 5.00);
				scanner.scanItem(item);
				itemForBag = item;
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
				
			}
		});
		
		// Action Listener for finishAndPayButton
		finishAndPayButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "selectbags");
			}
		});
		
		// Action Listener for scanCancelButton
		scanCancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (staffLogin.isAttendantLoggedIn()) {
					reset();
					staffLogin.attendantLogOut();
				} else {
					cardLayout.show(container, "attendant");
				}
			}
		});
		
		// Action Listener for scanHelpButton
		scanHelpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (staffLogin.isAttendantLoggedIn()) {
					cardLayout.show(container, "control");
				} else {
					cardLayout.show(container, "attendant");
				}
			}
		});
		
		itemsScanned.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				tempProduct = itemsScanned.getSelectedValue();
			}
		});
	}
	
	public void constructKeyInCodePanel() {
		
		// Format the panel
		keyInCode.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		keyInCode.add(keyInCodeLabel, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		keyInCode.add(itemCodeField, gbc);
		itemCodeField.setPreferredSize(new Dimension(400, 30));
		gbc.gridx = 0; gbc.gridy = 2;
		keyInCode.add(itemCodeEnterButton, gbc);
		itemCodeEnterButton.setPreferredSize(new Dimension(400, 120));
		gbc.gridx = 0; gbc.gridy = 3;
		gbc.gridwidth = 1;
		keyInCode.add(keyInGoBackButton, gbc);
		keyInGoBackButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 3;
		keyInCode.add(keyInHelpButton, gbc);
		keyInHelpButton.setPreferredSize(new Dimension(200, 120));
		
		// Action Listener for itemCodeEnterButton
		itemCodeEnterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String pluCode = itemCodeField.getText().trim();
				try {
					plu.enterPLUItem(new PriceLookupCode(pluCode));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				itemForBag = new PLUCodedItem(new PriceLookupCode("4205"), plu.getPLUItemWeight());
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
				itemCodeField.setText(null);
			}
		});
		
		// Action Listener for keyInGoBackButton
		keyInGoBackButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "scan");
			}
		});	
		
		// Action Listener for keyInHelpButton
		keyInHelpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (staffLogin.isAttendantLoggedIn()) {
					cardLayout.show(container, "control");
				} else {
					cardLayout.show(container, "attendant");
				}
			}
		});	
	}
	
	public void constructLookUpProductPanel() {
		
		// Format the panel
		lookUpProduct.setLayout(new GridLayout(2,1));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		JPanel productLibrary, options;
		productLibrary = new JPanel();
		options = new JPanel();
		
		productLibrary.setLayout(new GridBagLayout());
		options.setLayout(new GridBagLayout());
		
		lookUpProduct.add(productLibrary);
		lookUpProduct.add(options);
		
		gbc.gridx = 0; gbc.gridy = 0;
		productLibrary.add(apples, gbc);
		apples.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 0;
		productLibrary.add(apricots, gbc);
		apricots.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 0;
		productLibrary.add(mushrooms, gbc);
		mushrooms.setPreferredSize(new Dimension(200, 120));
		// Row 2
		gbc.gridx = 0; gbc.gridy = 1;
		productLibrary.add(bananas, gbc);
		bananas.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 1;
		productLibrary.add(lemons, gbc);
		lemons.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 1;
		productLibrary.add(broccoli, gbc);
		broccoli.setPreferredSize(new Dimension(200, 120));
		
		gbc.gridx = 0; gbc.gridy = 0;
		options.add(lookUpGoBackButton, gbc);
		lookUpGoBackButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 0;
		options.add(lookUpHelpButton, gbc);
		lookUpHelpButton.setPreferredSize(new Dimension(200, 120));
		
		// Action Listener for lookUpGoBackButton
		lookUpGoBackButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "scan");
			}
		});
		
		// Action Listener for lookUpHelpButton
		lookUpHelpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (staffLogin.isAttendantLoggedIn()) {
					cardLayout.show(container, "control");
				} else {
					cardLayout.show(container, "attendant");
				}
			}
		});
		
		// Action listeners for products 
		// Action Listener for apples
		apples.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					plu.enterPLUItem(new PriceLookupCode("4205"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				itemForBag = new PLUCodedItem(new PriceLookupCode("4205"), plu.getPLUItemWeight());
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
			}
		});
		
		// Action Listener for apricots
		apricots.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					plu.enterPLUItem(new PriceLookupCode("4219"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				itemForBag = new PLUCodedItem(new PriceLookupCode("4219"), plu.getPLUItemWeight());
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
			}
		});
		
		// Action Listener for mushrooms
		mushrooms.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					plu.enterPLUItem(new PriceLookupCode("4556"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				itemForBag = new PLUCodedItem(new PriceLookupCode("4219"), plu.getPLUItemWeight());
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
			}
		});
		
		// Action Listener for bananas
		bananas.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					plu.enterPLUItem(new PriceLookupCode("4014"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				itemForBag = new PLUCodedItem(new PriceLookupCode("4219"), plu.getPLUItemWeight());
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
			}
		});
		
		// Action Listener for lemons
		lemons.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					plu.enterPLUItem(new PriceLookupCode("4053"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				itemForBag = new PLUCodedItem(new PriceLookupCode("4219"), plu.getPLUItemWeight());
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
			}
		});
		
		// Action Listener for broccoli
		broccoli.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					plu.enterPLUItem(new PriceLookupCode("3082"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				itemForBag = new PLUCodedItem(new PriceLookupCode("4219"), plu.getPLUItemWeight());
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				dlm.addElement(cart.cart.get(cart.cart.size() - 1));
				itemsScanned.setModel(dlm);
				cardLayout.show(container, "bagitem");
			}
		});
	}
	
	public void constructSelectNumberOfBagsPanel() {
		
		// Format the panel
		selectNumberOfBags.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		selectNumberOfBags.add(numberOfBagsLabel, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		selectNumberOfBags.add(numberOfBagsField, gbc);
		numberOfBagsField.setPreferredSize(new Dimension(400, 30));
		gbc.gridx = 0; gbc.gridy = 2;
		selectNumberOfBags.add(bagEnterButton, gbc);
		bagEnterButton.setPreferredSize(new Dimension(400, 120));
		gbc.gridx = 0; gbc.gridy = 3;
		selectNumberOfBags.add(bagGoBackButton, gbc);
		bagGoBackButton.setPreferredSize(new Dimension(400, 120));
		
		// Action Listener for bagEnterButton
		bagEnterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String bagsUsed = numberOfBagsField.getText().trim();
				if (Integer.valueOf(bagsUsed) > 0) {
					baggingArea.addNumberPlasticBagsUsed(Integer.valueOf(bagsUsed));
				}
				cardLayout.show(container, "member");
			}
		});
		
		// Action Listener for bagGoBackButton
		bagGoBackButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "scan");
			}
		});
	}
	
	public void constructBagginPendingPanel() {
		
		// Format the panel
		baggingPending.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		baggingPending.add(bagItemButton, gbc);
		bagItemButton.setPreferredSize(new Dimension(400, 120));
		gbc.gridx = 0; gbc.gridy = 1;
		baggingPending.add(dontBagItemButton, gbc);
		dontBagItemButton.setPreferredSize(new Dimension(400, 120));
		
		// Action Listener for bagItemButton
		bagItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					int itemAdded = baggingArea.AddItem(itemForBag);
					if (itemAdded == 1) {
						cardLayout.show(container, "issue");
					} else {
						cardLayout.show(container, "scan");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// Action Listener for dontBagItemButton
		dontBagItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					baggingArea.AddItemNoBag(itemForBag);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cardLayout.show(container, "scan");
			}
		});
		
	}
	
	public void constructEnterMemberNumPanel() {
		
		// Format the panel
		enterMemberNum.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		enterMemberNum.add(memberLabel, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 1;
		enterMemberNum.add(memberNumLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 1;
		enterMemberNum.add(memberNumField, gbc);
		gbc.gridx = 0; gbc.gridy = 2;
		enterMemberNum.add(memberNameLabel, gbc);
		gbc.gridx = 1; gbc.gridy = 2;
		enterMemberNum.add(memberNameField, gbc);
		gbc.gridx = 0; gbc.gridy = 3;
		gbc.gridwidth = 2;
		enterMemberNum.add(memberNumEnterButton, gbc);
		memberNumEnterButton.setPreferredSize(new Dimension(400, 120));
		gbc.gridx = 0; gbc.gridy = 4;
		gbc.gridwidth = 1;
		enterMemberNum.add(nonMemberButton, gbc);
		nonMemberButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 4;
		enterMemberNum.add(memberCancelButton, gbc);
		memberCancelButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 0; gbc.gridy = 5;
		gbc.gridwidth = 2;
		enterMemberNum.add(memberScanButton, gbc);
		memberScanButton.setPreferredSize(new Dimension(400, 120));
		
		memberCard.addToDatabase("000123", "Joe");
		Card card = new Card("member", "000123", "Joe", null, null, false, false);
		
		// Action listener for memberNumEnterButton
		memberNumEnterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String memberNumber = memberNumField.getText().trim();
				String memberName = memberNameField.getText().trim();
				memberCard.inputMembershipInfo(memberNumber, memberName, cart.getCumPrice());
				
				cardLayout.show(container, "paymenttype");
			}
		});
		
		// Action listener for nonMemberButton
		nonMemberButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "paymenttype");
			}
		});
		
		// Action listener for memberCancelButton
		memberCancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "selectbags");
				cart.removeFromCart(tempProduct);
			}
		});
		
		// Action listener for memberScanButton
		memberScanButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					memberCard.useMemberCard(card, cart.getCumPrice());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cardLayout.show(container, "paymenttype");
				
			}
		});
	}
	
	public void constructSelectPaymentPanel() {
		
		// Format the panel
		selectPaymentType.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		selectPaymentType.add(paymentTypeLabel, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 1;
		selectPaymentType.add(cashButton, gbc);
		cashButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 1;
		selectPaymentType.add(creditButton, gbc);
		creditButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 0; gbc.gridy = 2;
		selectPaymentType.add(debitButton, gbc);
		debitButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 2;
		selectPaymentType.add(giftCardButton, gbc);
		giftCardButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 0; gbc.gridy = 3;
		gbc.gridwidth = 2;
		selectPaymentType.add(paymentGoBackButton, gbc);
		paymentGoBackButton.setPreferredSize(new Dimension(400, 120));
		
		
		// Action Listener for cashButton
		cashButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "cash");
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
			}
		});
		
		// Action Listener for creditButton
		creditButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "credit");
			}
		});
		
		// Action Listener for debitButton
		debitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "debit");
			}
		});
		
		// Action Listener for giftCardButton
		giftCardButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Card giftCard = new Card("gift card", "6750056347755", "Andrew", null, null, false, false);
				CardIssuer coop = new CardIssuer("Co-op");
				coop.addCardData("6750056347755", "Andrew", expiry, "000", new BigDecimal(100));
				cardPayment.PayWithGiftCard(giftCard, cart.getCumPrice(), coop);
				finishPayment();
			}
		});
		
		// Action Listener for paymentGoBackButton
		paymentGoBackButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "member");
			}
		});
		
	}
	
	public void constructPayWithCreditPanel() {
		// Format the panel
		payWithCredit.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		payWithCredit.add(swipeCredit, gbc);
		swipeCredit.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 0;
		payWithCredit.add(tapCredit, gbc);
		tapCredit.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 0;
		payWithCredit.add(insertCredit, gbc);
		insertCredit.setPreferredSize(new Dimension(200, 120));
		
		Card creditCard = new Card("credit", "6750094154567755", "Billy", "043", "9876", true, true);
		CardIssuer Bank = new CardIssuer("TD Canada");
		Bank.addCardData("6750094154567755", "Billy", expiry, "043", new BigDecimal(500));
		
		// Action Listener for swipeCredit
		swipeCredit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardPayment.PayWithCreditCard(creditCard, 2, cart.getCumPrice(), "9876", Bank);
				finishPayment();
			}
		});
		
		// Action Listener for tapCredit
		tapCredit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardPayment.PayWithCreditCard(creditCard, 0, cart.getCumPrice(), "9876", Bank);
				finishPayment();
			}
		});
		
		// Action Listener for insertCredit
		insertCredit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardPayment.PayWithCreditCard(creditCard, 1, cart.getCumPrice(), "9876", Bank);
				finishPayment();
			}
		});
	}
	
	public void constructPayWithDebitPanel() {
		// Format the panel
		payWithDebit.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		payWithDebit.add(swipeDebit, gbc);
		swipeDebit.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 0;
		payWithDebit.add(tapDebit, gbc);
		tapDebit.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 0;
		payWithDebit.add(insertDebit, gbc);
		insertDebit.setPreferredSize(new Dimension(200, 120));
		
		Card debitCard = new Card("debit", "6750094154567755", "Billy", "043", "9876", true, true);
		CardIssuer Bank = new CardIssuer("TD Canada");
		Bank.addCardData("6750094154567755", "Billy", expiry, "043", new BigDecimal(500));
		
		// Action Listener for swipeCredit
		swipeDebit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardPayment.PayWithDebitCard(debitCard, 2, cart.getCumPrice(), "9876", Bank);
				finishPayment();
			}
		});
		
		// Action Listener for tapCredit
		tapDebit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardPayment.PayWithDebitCard(debitCard, 0, cart.getCumPrice(), "9876", Bank);
				finishPayment();
			}
		});
		
		// Action Listener for insertCredit
		insertDebit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardPayment.PayWithDebitCard(debitCard, 1, cart.getCumPrice(), "9876", Bank);
				finishPayment();
				
			}
		});
	}
	
	public void checkForDonePayment() {
		if(cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).doubleValue() <= 0.0) {
			BigDecimal endTotal = cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted()));
			change.setText("Change due: $" + endTotal.abs());
			receiptPrinter.print(cart);
			cardLayout.show(container, "receipt");
			receipt.setModel(dlm);
		
			if(endTotal.doubleValue() < 0.0) {
				try {
					giveChange.returnChange(endTotal.abs());
				} catch (EmptyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void finishPayment() {
		BigDecimal endTotal = BigDecimal.ZERO;
		change.setText("Change due: $" + endTotal.abs());
		receiptPrinter.print(cart);
		cardLayout.show(container, "receipt");
		receipt.setModel(dlm);
	}
	
	public void constructPayWithCashPanel() {
		
		// Format the panel
		payWithCash.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 2; gbc.gridy = 0;
		payWithCash.add(outstandingBalance, gbc);
		outstandingBalance.setFont(new Font(null, 1, 15));
		
		gbc.gridx = 0; gbc.gridy = 1;
		payWithCash.add(nickel, gbc);
		nickel.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 1;
		payWithCash.add(dime, gbc);
		dime.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 1;
		payWithCash.add(quarter, gbc);
		quarter.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 3; gbc.gridy = 1;
		payWithCash.add(loonie, gbc);
		loonie.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 4; gbc.gridy = 1;
		payWithCash.add(toonie, gbc);
		toonie.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 0; gbc.gridy = 2;
		payWithCash.add(five, gbc);
		five.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 2;
		payWithCash.add(ten, gbc);
		ten.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 2;
		payWithCash.add(twenty, gbc);
		twenty.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 3; gbc.gridy = 2;
		payWithCash.add(fifty, gbc);
		fifty.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 4; gbc.gridy = 2;
		payWithCash.add(hundred, gbc);
		hundred.setPreferredSize(new Dimension(200, 120));
		
		// Action Listener for nickel
		nickel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					coinPayment.payCoin(new Coin(new BigDecimal("0.05"), CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for tapCredimedit
		dime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					coinPayment.payCoin(new Coin(new BigDecimal("0.10"), CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for quarter
		quarter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					coinPayment.payCoin(new Coin(new BigDecimal("0.25"), CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for loonie
		loonie.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					coinPayment.payCoin(new Coin(new BigDecimal("1.00"), CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for toonie
		toonie.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					coinPayment.payCoin(new Coin(new BigDecimal("2.00"), CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for five
		five.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					notePayment.payNote(new Banknote(5, CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for ten
		ten.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					notePayment.payNote(new Banknote(10, CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for twenty
		twenty.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					notePayment.payNote(new Banknote(20, CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for fifty
		fifty.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					notePayment.payNote(new Banknote(50, CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
		
		// Action Listener for hundred
		hundred.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					notePayment.payNote(new Banknote(100, CAD));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outstandingBalance.setText("Outstanding Balance: $" + cart.getCumPrice().subtract(new BigDecimal(notePayment.getTotalCashInserted()).add(coinPayment.getTotalCoinInserted())).setScale(2, RoundingMode.HALF_UP));
				checkForDonePayment();
			}
		});
	}
	
	public void constructGiveChangeAndReceiptPanel() {
		
		// Format the panel
		takeReceipt.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		takeReceipt.add(change, gbc);
		change.setFont(new Font(null, 1, 40));
		gbc.gridx = 0; gbc.gridy = 1;
		takeReceipt.add(receipt, gbc);
		receipt.setFont(new Font(null, 1, 30));
		gbc.gridx = 0; gbc.gridy = 3;
		takeReceipt.add(takeReceiptButton, gbc);
		takeReceiptButton.setPreferredSize(new Dimension(400, 120));
		
		// Action Listener for hundred
		takeReceiptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(receiptPrinter.removePrintedreceipt());
				reset();
			}
		});
	}
	
	public void reset() {
		runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
		dlm.clear();
		itemsScanned.setModel(dlm);
		receipt.setModel(dlm);
		cart.clearCart();
		cardLayout.show(container, "welcome");
	}
	
	public void constructAttendantLoginPanel() {
		
		// Format the panel
		attendantLogin.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		attendantLogin.add(attendantLoginPrompt, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		attendantLogin.add(attendantLoginField, gbc);
		attendantLoginField.setPreferredSize(new Dimension(400, 30));
		gbc.gridx = 0; gbc.gridy = 2;
		gbc.gridwidth = 1;
		attendantLogin.add(attendantLoginButton, gbc);
		attendantLoginButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 2;
		attendantLogin.add(attendantGoBackButton, gbc);
		attendantGoBackButton.setPreferredSize(new Dimension(200, 120));
		
		// Action Listener for attendantLoginButton
		attendantLoginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String pw = attendantLoginField.getText().trim();
				Card attendantCard = new Card("attendant", "4382093267", "Sammy", null, null, false, false);
				try {
					staffLogin.attendantLogIn(attendantCard, pw);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (staffLogin.isAttendantLoggedIn()) {
					cardLayout.show(container, "control");
					attendantLoginField.setText(null);
				}
			}
		});
		
		// Action Listener for attendantGoBackButton
		attendantGoBackButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "scan");
			}
		});
	}
	
	public void constructBaggingIssuePanel() {
		
		// Format the panel
		baggingIssue.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		baggingIssue.add(baggingIssuePrompt, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		baggingIssue.add(baggingIssueField, gbc);
		gbc.gridx = 0; gbc.gridy = 2;
		gbc.gridwidth = 2;
		baggingIssue.add(baggingIssueOverrideButton, gbc);
		baggingIssueOverrideButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 2;
		baggingIssue.add(baggingIssueRejectButton, gbc);
		baggingIssueRejectButton.setPreferredSize(new Dimension(200, 120));
		
		// Action Listener for baggingIssueOverrideButton
		baggingIssueOverrideButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String pw = baggingIssueField.getText().trim();
				if (pw.equals("1234")) {
					try {
						baggingArea.attendantNotifiedWeight(true);
					} catch (OverloadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cardLayout.show(container, "scan");
					baggingIssueField.setText(null);
				}
			}
		});
		
		// Action Listener for baggingIssueRejectButton
		baggingIssueRejectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String pw = baggingIssueField.getText().trim();
				if (pw.equals("1234")) {
					try {
						baggingArea.attendantNotifiedWeight(true);
					} catch (OverloadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cardLayout.show(container, "scan");
					baggingIssueField.setText(null);
				}
			}
		});
	}
	
	public void constructAttendantFunctionsPanel() {
		
		// Format the panel
		attendantFunctions.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		attendantFunctions.add(attendantLogoutButton, gbc);
		attendantLogoutButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 0; gbc.gridy = 1;
		attendantFunctions.add(attendantControlGoBackButton, gbc);
		attendantControlGoBackButton.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 0;
		attendantFunctions.add(refillPrinterInk, gbc);
		refillPrinterInk.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 1; gbc.gridy = 1;
		attendantFunctions.add(refillPrinterPaper, gbc);
		refillPrinterPaper.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 0;
		attendantFunctions.add(emptyBanknote, gbc);
		emptyBanknote.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 2; gbc.gridy = 1;
		attendantFunctions.add(emptyCoin, gbc);
		emptyCoin.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 3; gbc.gridy = 0;
		attendantFunctions.add(refillBanknote, gbc);
		refillBanknote.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 3; gbc.gridy = 1;
		attendantFunctions.add(refillCoin, gbc);
		refillCoin.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 4; gbc.gridy = 0;
		attendantFunctions.add(shutdownStation, gbc);
		shutdownStation.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 4; gbc.gridy = 1;
		attendantFunctions.add(blockStation, gbc);
		blockStation.setPreferredSize(new Dimension(200, 120));
		gbc.gridx = 5; gbc.gridy = 0;
		attendantFunctions.add(removeItem, gbc);
		removeItem.setPreferredSize(new Dimension(200, 120));
		
		// Action Listener for attendantLogoutButton
		attendantLogoutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				staffLogin.attendantLogOut();
				cardLayout.show(container, "scan");
			}
		});
		
		// Action Listener for attendantControlGoBackButton
		attendantControlGoBackButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "scan");
			}
		});
		
		// Action Listener for refillPrinterInk
		refillPrinterInk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				receiptPrinter.addingInk(MAXIMUM_INK);
			}
		});
		
		// Action Listener for refillPrinterPaper
		refillPrinterPaper.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				receiptPrinter.addingPaper(MAXIMUM_PAPER);
			}
		});
		
		// Action Listener for refillBanknote
		refillBanknote.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				giveChange.refillBanknoteDispensers(100);
			}
		});
		
		// Action Listener for refillCoin
		refillCoin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				giveChange.refillCoinDispenser(200);
			}
		});
		
		// Action Listener for emptyBanknote
		emptyBanknote.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<Banknote> cashout = emptyNotes.EmptyBanknoteStorage();
			}
		});
		
		// Action Listener for emptyCoin
		emptyCoin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<Coin> cashout = emptyCoins.EmptyCoinStorage();
			}
		});
		
		// Action Listener for removeItem
		removeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cart.removeFromCart(tempProduct);
				dlm.removeElement(tempProduct);
				staffLogin.attendantLogOut();
				runningTotalLabel.setText("Running total: $" + cart.getCumPrice());
				cardLayout.show(container, "scan");
				
				
			}
		});
		
		// Action Listener for blockStation
		blockStation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cardLayout.show(container, "blocked");
			}
		});
		
		// Action Listener for shutdownStation
		shutdownStation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
				staffLogin.attendantLogOut();
				cardLayout.show(container, "startup");
			}
		});
	}
	
	public void constructStationBlockedPanel() {
		
		// Format the panel
		stationBlocked.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		stationBlocked.add(stationBlockedPrompt, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		stationBlocked.add(stationBlockedField, gbc);
		stationBlockedField.setPreferredSize(new Dimension(400, 30));
		gbc.gridx = 0; gbc.gridy = 2;
		stationBlocked.add(stationBlockedButton, gbc);
		stationBlockedButton.setPreferredSize(new Dimension(400, 120));
		
		// Action Listener for stationBlockedButton
		stationBlockedButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String pw = stationBlockedField.getText().trim();
				if (pw.equals("1234")) {
					cardLayout.show(container, "control");
					stationBlockedField.setText(null);
				}
			}
		});
	}
	
	// Populates the database for the barcoded products
	public static void populateBarcodeDatabase(productScan scanner) {
		BarcodedProduct cheese = new BarcodedProduct(new Barcode("1111"), "Cheese", new BigDecimal(5.00));
		BarcodedProduct milk = new BarcodedProduct(new Barcode("2222"), "Milk", new BigDecimal(4.50));
		BarcodedProduct meat = new BarcodedProduct(new Barcode("3333"), "Meat", new BigDecimal(10.00));
		BarcodedProduct egg = new BarcodedProduct(new Barcode("4444"), "", new BigDecimal(2.00));

		Barcode barcode1 = new Barcode("1111");
		Barcode barcode2 = new Barcode("2222");
		Barcode barcode3 = new Barcode("3333");
		Barcode barcode4 = new Barcode("4444");
		
		scanner.addToDatabase(barcode1, cheese);
		scanner.addToDatabase(barcode2, milk);
		scanner.addToDatabase(barcode3, meat);
		scanner.addToDatabase(barcode4, egg);	
	}

	// Executes the GUI startup
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Initialization of the self checkout station and software classes
		SelfCheckoutStation selfCheckout = new SelfCheckoutStation(CAD, banknoteDenominations, coinDenominations, 1000, 1);
		baggingArea = new itemInBaggingAreaV2(selfCheckout, cart, 100000000, 1);
		scanner = new productScan(cart);
		plu = new EnterPLUCode(selfCheckout, cart, baggingArea);
		cardPayment = new PayWithCard();
		coinPayment = new PayWithCoin(selfCheckout);
		notePayment = new PayWithNote(selfCheckout);
		receiptPrinter = new PrintReceipt(selfCheckout.printer);
		staffLogin = new LoginToControl();
		emptyNotes = new EmptyBanknote(selfCheckout.banknoteStorage);
		emptyCoins = new EmptyCoin(selfCheckout.coinStorage);
		giveChange = new GiveChange(selfCheckout.banknoteOutput, selfCheckout.coinTray, selfCheckout.banknoteDispensers, selfCheckout.coinDispensers, coinDenominations, banknoteDenominations);
		memberCard = new MemberCard("member", "0019284527102937", "Joe", new BigDecimal(100));
		
		// Initialization of relavent parameters
		receiptPrinter.addingPaper(MAXIMUM_PAPER);
		receiptPrinter.addingInk(MAXIMUM_INK);
		giveChange.refillBanknoteDispensers(100);
		giveChange.refillCoinDispenser(200);
		
		expiry.add(Calendar.MONTH, 2);
		expiry.add(Calendar.YEAR, 2);
		staffLogin.addToDatabase("1234", "Sammy");
		populateBarcodeDatabase(scanner);
		plu.addToPLUDatabase();
		
		// Calling GUI constructor
		new GUI(selfCheckout);

	}

}
