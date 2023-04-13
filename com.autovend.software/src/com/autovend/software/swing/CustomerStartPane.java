/*
 * SENG 300 Project Iteration 3 - Group P3-2
 * Braedon Haensel -         UCID: 30144363
 * Umar Ahmed -             UCID: 30145076
 * Bartu Okan -             UCID: 30150180
 * Arie Goud -                 UCID: 30163410
 * Abdul Biderkab -         UCID: 30156693
 * Hamza Khan -             UCID: 30157097
 * James Hayward -             UCID: 30149513
 * Christian Salvador -     UCID: 30089672
 * Fatema Chowdhury -         UCID: 30141268
 * Sankalp Bartwal -         UCID: 30132025
 * Avani Sharma -             UCID: 30125040
 * Albe Martin -             UCID: 30161964
 * Omar Khan -                 UCID: 30143707
 * Samantha Liu -             UCID: 30123255
 * Alex Chen -                 UCID: 30140184
 * Auric Adubofour-Poku -     UCID: 30143774
 * Grant Tkachyk -             UCID: 30077137
 * Amandeep Kaur -             UCID: 30153923
 * Tashi Labowka-Poulin -     UCID: 30140749
 * Daniel Chang -             UCID: 30110252
 * Jacob Braun -             UCID: 30124507
 * Omar Ragab -             UCID: 30148549
 * Artemy Gavrilov -         UCID: 30143698
 * Colton Gowans -             UCID: 30143979
 * Hada Rahadhi Hafiyyan -     UCID: 30186484
 *
 */
package com.autovend.software.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.software.controllers.CustomerIOController;

/**
 * A class for the customer start pane.
 */
public class CustomerStartPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomerIOController cioc;
	private final ArrayList<String> languages = Language.languages;
	public String language = Language.defaultLanguage;
	public JButton startButton;
	public JButton languageSelectButton;
	public ButtonGroup group;
	public JLabel disabledMessage;

	private JPanel glassPane;

	/**
	 * Basic constructor.
	 *
	 * @param cioc Linked CustomerIOController.
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

		initializeTransparentPane();

		this.add(glassPane);

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
			public void actionPerformed
					(ActionEvent e) {
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
				group = new ButtonGroup();
				for (String language : languages) {
					JRadioButton radioButton = new JRadioButton(language);
					radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
					group.add(radioButton);
					panel.add(radioButton);
				}

				// Show the language selection dialog and get the selected language
				//int result = JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (optionDialogPopup(panel) == JOptionPane.OK_OPTION) {
					String newLanguage = null;
					// Determine selected button's text
					for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
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

	public int optionDialogPopup(JPanel panel) {
		return JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}

	public void initializeTransparentPane() {
		glassPane = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(128, 128, 128, 128)); // Semi-transparent gray
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		glassPane.setOpaque(false);
		glassPane.setBounds(0, 0, 800, 800); // Set the bounds to match the size of the CustomerStartPane
		glassPane.setVisible(false);

		disabledMessage = new JLabel("Station disabled: waiting for attendant to enable");
		disabledMessage.setFont(new Font("Tahoma", Font.BOLD, 20));
		glassPane.add(disabledMessage);

		// Make the glass pane "absorb" the mouse events, so that nothing behind it (the buttons) can be clicked while it is displayed
		glassPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				e.consume();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				e.consume();
			}
		});

	}

	public void enableStation() {
		glassPane.setVisible(false);
	}
	public void disableStation() {
		glassPane.setVisible(true);
	}

}

