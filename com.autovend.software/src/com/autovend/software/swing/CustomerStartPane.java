package com.autovend.software.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.software.controllers.CustomerIOController;

/**
 * A class for  the customer start pane.
 */
public class CustomerStartPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomerIOController cioc;
	// TODO: Make languages parameters.
	private String language = "English";
	private String languages[] = new String[] {"English", "French"};
	public JButton startButton;
	public JButton languageSelectButton;
	
	/**
	 * TODO: Delete for final submission.
	 * 
	 * Quick GUI launcher, used to allow window builder to work.
	 */
	public static void main(String[] args) {
		// Add French language.
		HashMap<String, String> french = new HashMap<>();
		french.put("Username:", "Le username:");
		french.put("Password:", "Le password:");
		french.put("Log In", "Le log in");
		french.put("Change Language", "Le Change Language");
		french.put("START", "LE START");
		Language.addLanguage("French", french);
				
		// Create checkout station.
		SelfCheckoutStation customerStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), 
				new int[] {1}, new BigDecimal[] {new BigDecimal(0.25)}, 100, 1);
		
		// Get and set up screen
		JFrame customerScreen = customerStation.screen.getFrame();
		customerScreen.setExtendedState(0);
		customerScreen.setSize(800, 800);
		customerScreen.setUndecorated(false);
		customerScreen.setResizable(false);
		
		CustomerIOController cioc = new CustomerIOController(customerStation.screen);
		customerScreen.setContentPane(new CustomerStartPane(cioc));
		
		customerScreen.setVisible(true);
	}
	
	/**
	 * Basic constructor.
	 * 
	 * @param cioc
	 * 			Linked CustomerIOController.
	 */
	public CustomerStartPane(CustomerIOController cioc) {
		super();
		this.cioc = cioc;
		initializeStartPane();
	}
	
	/**
	 * Initialize customer start pane.
	 */
	private void initializeStartPane() {
		// Create start screen pane.
        this.setLayout(null);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Initialize start button.
        initializeStartButton();
        
        // Initialize language select button.
        initializeLanguageSelectButton();
	}
	
	/**
	 * Initialize the start button.
	 */
	private void initializeStartButton() {
		// Create start button.
        startButton = new JButton("START");
        startButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Start button pressed.

                // Notify controller that start was pressed.
            	cioc.startPressed();
            }
        });
        startButton.setBounds(290, 200, 200, 200);
        this.add(startButton);
	}

	/**
	 * Initialize the language select button.
	 */
	private void initializeLanguageSelectButton() {
		// Create language select button.
		languageSelectButton = new JButton(Language.translate(language, "Change Language"));
	    languageSelectButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
	    languageSelectButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            // Create a panel to hold the language select pop-up
	            JPanel panel = new JPanel();
	            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            
	            // Create a label for the language selection
	            JLabel label = new JLabel("Select a language:");
	            label.setAlignmentX(Component.CENTER_ALIGNMENT);
	            panel.add(label);
	            
	            // Create a group of radio buttons for the available languages
	            ButtonGroup group = new ButtonGroup();
	            for (String language : languages) {
	                JRadioButton radioButton = new JRadioButton(language);
	                radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	                group.add(radioButton);
	                panel.add(radioButton);
	            }
	
	            // Show the language selection dialog and get the selected language
	            int result = JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	            if (result == JOptionPane.OK_OPTION) {
	                String newLanguage = null;
	                // Determine selected button's text
	                for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
	                    AbstractButton button = buttons.nextElement();
	                    if (button.isSelected()) {
	                        newLanguage = button.getText();
	                        break;
	                    }
	                }
	                 if (newLanguage != null) {
	                    // Update the language variable
	                    language = newLanguage;
	                     // Update texts to new language
	                    startButton.setText(Language.translate(language, "START"));
	                    languageSelectButton.setText(Language.translate(language, "Change Language"));
	                }
	            }
	        }
	    });
	    languageSelectButton.setBounds(519, 647, 200, 50);

     	this.add(languageSelectButton);
	}
}
