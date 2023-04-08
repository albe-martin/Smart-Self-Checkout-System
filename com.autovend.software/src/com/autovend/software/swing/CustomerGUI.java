package com.autovend.software.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CustomerGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private String language;

    /**
     * TODO: Delete this method for final product.
     *
     * Used to launch GUI when run
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CustomerGUI frame = new CustomerGUI("English");
                    // Center it
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the customer GUI frame.
     */
    public CustomerGUI(String language) {
        super("Customer GUI");

        // Set language.
        this.language = language;

        // Set frame properties.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 800, 800);

        // Start on the login screen.
        showStartScreen();
    }

    /**
     * Switches to the start screen.
     */
    public void showStartScreen() {
        // TODO: Language selector.

        // Create start screen pane.
        JPanel startContentPane = new JPanel();
        startContentPane.setLayout(null);
        startContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        ImageIcon startImage = new ImageIcon(getClass().getResource("resources/start.png"));

        // Create login button.
        JButton startButton = new JButton(startImage);
        startButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Start button pressed.

                // Switch to regular operation screen.
                showOperationScreen();
            }
        });

        startButton.setBounds(200, 200, startImage.getIconWidth(), startImage.getIconHeight());
//		startButton.setIcon(new ImageIcon("~/Desktop/start.png"));
        startContentPane.add(startButton);

        JButton languageSelectButton = new JButton(Language.translate(language, "Select Language"));
        languageSelectButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        languageSelectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //create a panel to hold the language select popup
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                //create a label for the language selection
                JLabel label = new JLabel("Select a language:");
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(label);
                //create a group of radio buttons for the available languages
                ButtonGroup group = new ButtonGroup();
                String[] languages = {"English", "French"};
                for (String language : languages) {
                    JRadioButton radioButton = new JRadioButton(language);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                //show the language selection dialog and get the selected language
                int result = JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (result == JOptionPane.OK_OPTION) {
                    String newLanguage = null;
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

                        // Update the text on the buttons
                        if (language == languages[0]){
                        languageSelectButton.setText(Language.translate(language, "Select Language"));}
                        else if (language == languages[1]){
                            languageSelectButton.setText(Language.translate(language, "Choisir la langue"));
                        }

                    }
                }
            }
        });
        languageSelectButton.setBounds(this.getWidth()/2 - 75, 500, 150, 50);

        startContentPane.add(languageSelectButton);

        // Change frame pane to login.
        setContentPane(startContentPane);

        // Refresh frame.
        revalidate();
        repaint();
    }

    /**
     * Switches to the regular operation screen.
     */
    public void showOperationScreen() {
        // Create login screen pane.
        JPanel operationContentPane = new JPanel();
        operationContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        operationContentPane.setLayout(null);

        // Create exit button. TODO: Replace with cart functionalities
        JButton logoutButton = new JButton(Language.translate(language, "Exit"));
        logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logout button pressed.

                // Switch to login screen.
                showStartScreen();
            }
        });
        logoutButton.setBounds(324, 455, 120, 63);
        operationContentPane.add(logoutButton);

        // Change frame pane to regular operation.
        setContentPane(operationContentPane);

        // Refresh frame.
        revalidate();
        repaint();
    }
}
