package com.pravles.wordcounter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import static com.pravles.wordcounter.Utils.calculateCenterOnScreen;

public class MainWindow extends JFrame {

    private final JLabel initialWordCount;

    public MainWindow() {
        super("Word Counter 2022");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JTabbedPane tabbedPane = new JTabbedPane();

        final JPanel detailsPane = new JPanel(false);

        final JLabel label = new JLabel("Initial word count:");
        initialWordCount = new JLabel("?");
        detailsPane.add(label);
        detailsPane.add(initialWordCount);

        tabbedPane.add("Details", detailsPane);

        getContentPane().add(tabbedPane);

        pack();
    }

    void centerOnScreen() {
        setLocation(calculateCenterOnScreen(this));
    }

    public void setInitialWordCount(int initialWordCount) {
        this.initialWordCount.setText(Integer.toString(initialWordCount));
    }
}
