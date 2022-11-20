package com.pravles.wordcounter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import static com.pravles.wordcounter.Utils.calculateCenterOnScreen;

public class MainWindow extends JFrame {

    private final JLabel initialWordCount;
    private final JLabel currentWordCount;

    public MainWindow() {
        super("Word Counter 2022");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JTabbedPane tabbedPane = new JTabbedPane();

        final JPanel detailsPane = new JPanel(false);

        initialWordCount = new JLabel("?");
        detailsPane.add(new JLabel("Initial word count:"));
        detailsPane.add(initialWordCount);

        currentWordCount = new JLabel("?");

        detailsPane.add(new JLabel("Current word count:"));
        detailsPane.add(currentWordCount);

        tabbedPane.add("Details", detailsPane);

        getContentPane().add(tabbedPane);

        pack();
    }

    void centerOnScreen() {
        setLocation(calculateCenterOnScreen(this));
    }

    public void setInitialWordCount(int initialWordCount) {
        this.initialWordCount.setText(Integer.toString(initialWordCount));
        this.currentWordCount.setText(Integer.toString(initialWordCount));
    }
}
