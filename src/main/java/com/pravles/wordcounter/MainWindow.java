package com.pravles.wordcounter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import java.awt.GridLayout;

import static com.pravles.wordcounter.Utils.calculateCenterOnScreen;

public class MainWindow extends JFrame {

    private final JLabel initialWordCount;
    private final JLabel currentWordCount;
    private final JLabel wordsWrittenToday;
    private final JTextField dailyTarget;

    public MainWindow() {
        super("Word Counter 2022");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JTabbedPane tabbedPane = new JTabbedPane();

        final JPanel detailsPane = new JPanel(false);

        final GridLayout gridLayout = new GridLayout(4, 2);
        detailsPane.setLayout(gridLayout);
        initialWordCount = new JLabel("?");
        detailsPane.add(new JLabel("Initial word count:"));
        detailsPane.add(initialWordCount);

        currentWordCount = new JLabel("?");

        detailsPane.add(new JLabel("Current word count:"));
        detailsPane.add(currentWordCount);

        wordsWrittenToday = new JLabel("?");

        detailsPane.add(new JLabel("Words written today:"));
        detailsPane.add(wordsWrittenToday);

        dailyTarget = new JTextField("?");

        detailsPane.add(new JLabel("Daily target:"));
        detailsPane.add(dailyTarget);

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
        this.wordsWrittenToday.setText("0");
    }

    public void setDailyTarget(final long dailyTarget) {
        this.dailyTarget.setText(Long.toString(dailyTarget));
    }
}
