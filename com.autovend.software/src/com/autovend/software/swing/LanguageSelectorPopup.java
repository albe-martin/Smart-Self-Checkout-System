package com.autovend.software.swing;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * Creates a pop-up that enables language selection.
 */
class LanguageSelectorPopup extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private ActionListener parent;
	
	private String language;
	private String[] languages;

	
	private ButtonGroup bg1;
	
	// Action command
	public static final String LANGUAGE_CHANGED = "language_changed";

	/**
	 * Create the Language Selector pop-up.
	 * 
	 * @param parent
	 * 			Parent to notify about action events.
	 * @param language
	 * 			Currently selected language.
	 * @param languages
	 * 			List of languages that it can change to.
	 */
    public LanguageSelectorPopup(ActionListener parent, String language, String[] languages) {
        super("Change Language");
        this.parent = parent;
        this.language = language;
        this.languages = languages;
        
        // Set properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 300);
        
        // Create content pane.
        Container contentPane = getContentPane();
        contentPane.add(createLanguageSelections());
    }
    
    /**
     * Create box with language radio button selectors.
     * 
     * @return box
     * 			Box with language radio button selectors.
     */
    private JComponent createLanguageSelections() {
    	// Container for elements.
        Box box = Box.createVerticalBox();
        
    	// Create choose language label.
        JLabel chooseLanguageLabel = new JLabel("Select language:");
        box.add(chooseLanguageLabel);
        box.add(Box.createVerticalStrut(5)); // spacer
        
        // Create radio button group.
        bg1 = new ButtonGroup();

        // Add radio button for each language
        for (String language : languages) {
        	// Create the radio button
        	JRadioButton rb = new JRadioButton();
        	rb.setText(language);
        	rb.setActionCommand(language);
        	if (this.language.equals(language)) {
        		// Select the starting language
        		rb.setSelected(true);
        	}
        	bg1.add(rb);
        	box.add(rb);
        	
        }
        
        // Create save button.
        JButton saveButton = new JButton("Save");
        saveButton.setActionCommand(LANGUAGE_CHANGED);
        saveButton.addActionListener(parent);
        box.add(saveButton);
        
        // Add some breathing room.
        box.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        return box;
    }
    
    /**
     * Get the new language.
     */
    public String getLanguage() {
    	return bg1.getSelection().getActionCommand();
    }
}