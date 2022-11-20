package com.pravles.wordcounter;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainWindow extends JFrame {
    public MainWindow() {
        super("Word Counter 2022");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JLabel label = new JLabel("Hello, world");
        getContentPane().add(label);

        pack();
    }
}
