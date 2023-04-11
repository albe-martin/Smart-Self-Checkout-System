package com.autovend.software.swing;

import com.autovend.software.controllers.CustomerIOController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ShutdownPane extends JPanel {
    private static final long serialVersionUID = 1L;
    private CustomerIOController cioc;

    public ShutdownPane(CustomerIOController cioc) {
        super();
        this.cioc = cioc;

        initializeShutdownPane();

    }

    private void initializeShutdownPane() {
        this.setLayout(null);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        //this.setForeground(new Color(0,0,0));
        //this.setOpaque(true);
        this.setBackground(new Color(0,0,0));


    }

}
