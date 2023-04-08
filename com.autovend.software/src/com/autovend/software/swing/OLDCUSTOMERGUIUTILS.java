package com.autovend.software.swing;
//package com.autovend.software.swing;
//
//import java.awt.Container;
//import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JButton;
//import javax.swing.JPanel;
//import javax.swing.border.EmptyBorder;
//
//import com.autovend.software.controllers.CustomerIOController;
//
///**
// * CustomerGUI utility class. Provides methods that return different customer screens.
// */
//public class CustomerGUIUtils {
//	
//	/**
//	 * Creates a customer start screen pane
//	 * 
//	 * @param cioc
//	 * 			CustomerIOController to signal events to.
//	 * @return
//	 * 			Start screen pane.
//	 */
//	public static Container getStartPane(CustomerIOController cioc) {
//		// Create start screen pane.
//        JPanel startContentPane = new JPanel();
//        startContentPane.setLayout(null);
//        startContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//
//        // Create login button.
//        JButton startButton = new JButton("START");
//        startButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
//        startButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                // Start button pressed.
//
//                // Notify controller that start was pressed.
//            	cioc.startPressed();
//            }
//        });
//
//        startButton.setBounds(290, 200, 200, 200);
//        startContentPane.add(startButton);
//        
//        /* Language has temporarily been removed */
//
////        JButton languageSelectButton = new JButton(Language.translate(language, "Select Language"));
////        languageSelectButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
////        if (language == "English"){
////            languageSelectButton.setText("Select a language:");
////        }
////        else if (language == "French"){
////            languageSelectButton.setText("Choisir la langue");
////        }
////        languageSelectButton.addActionListener(new ActionListener() {
////            public void actionPerformed(ActionEvent e) {
////                //create a panel to hold the language select popup
////                JPanel panel = new JPanel();
////                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
////                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
////                //create a label for the language selection
////                JLabel label = new JLabel("Select a language:");
////                label.setAlignmentX(Component.CENTER_ALIGNMENT);
////                panel.add(label);
////                //create a group of radio buttons for the available languages
////                ButtonGroup group = new ButtonGroup();
////                String[] languages = {"English", "French"};
////                for (String language : languages) {
////                    JRadioButton radioButton = new JRadioButton(language);
////                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
////                    group.add(radioButton);
////                    panel.add(radioButton);
////                }
////
////                //show the language selection dialog and get the selected language
////                int result = JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
////                if (result == JOptionPane.OK_OPTION) {
////                    String newLanguage = null;
////                    for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
////                        AbstractButton button = buttons.nextElement();
////                        if (button.isSelected()) {
////                            newLanguage = button.getText();
////                            break;
////                        }
////                    }
////
////                    if (newLanguage != null) {
////                        // Update the language variable
////                        language = newLanguage;
////
////                        // Update the text on the buttons
////                        if (language == languages[0]){
////                            languageSelectButton.setText(Language.translate(language, "Select Language"));
////                            startButton.setText("START");
////
////                        }
////                        else if (language == languages[1]){
////                            languageSelectButton.setText(Language.translate(language, "Choisir la langue"));
////                            startButton.setText("COMMENCER");
////                        }
////
////                    }
////                }
////            }
////        });
////        languageSelectButton.setBounds(this.getWidth()/2 - 75, 500, 150, 50);
////
////        startContentPane.add(languageSelectButton);
//
//        // return start pane
//        return startContentPane;
//
//	}
//	
//	/**
//	 * Creates a customer operation screen pane
//	 * 
//	 * @param cioc
//	 * 			CustomerIOController to signal events to.
//	 * @return
//	 * 			Start screen pane.
//	 */
//	public static Container getOperationPane(CustomerIOController cioc) {
//		// Create login screen pane.
//        JPanel operationContentPane = new JPanel();
//        operationContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//        operationContentPane.setLayout(null);
//
//        // Create exit button. TODO: Replace with cart functionalities
//        JButton logoutButton = new JButton("Exit");
//        logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
//        logoutButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                // Logout button pressed.
//
//                // Notify controller that logout is requested.
//                cioc.logoutPressed();
//            }
//        });
//        logoutButton.setBounds(324, 455, 120, 63);
//        operationContentPane.add(logoutButton);
//
//        // Change frame pane to regular operation.
//        return operationContentPane;
//	}
//}
