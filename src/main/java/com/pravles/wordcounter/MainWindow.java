package com.pravles.wordcounter;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import static com.pravles.wordcounter.Utils.calculateCenterOnScreen;
import static java.lang.Integer.parseInt;

public class MainWindow extends JFrame {

    private final JLabel initialWordCount;
    private final JLabel currentWordCount;
    private final JLabel wordsWrittenToday;
    private final JTextField dailyTarget;
    private final JLabel progress;
    private final JLabel overviewProgress;

    public MainWindow() {
        super("Word Counter 2022");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JTabbedPane tabbedPane = new JTabbedPane();

        final JPanel chartPane = new JPanel(false);
        chartPane.setLayout(new BoxLayout(chartPane, BoxLayout.PAGE_AXIS));

        overviewProgress = new JLabel("?");
        chartPane.add(overviewProgress);

        tabbedPane.add("Overview", chartPane);

        final JPanel detailsPane = new JPanel(false);

        final GridLayout gridLayout = new GridLayout(5, 2);
        detailsPane.setLayout(gridLayout);
        initialWordCount = new JLabel("?");
        detailsPane.add(new JLabel("Initial word count:"));
        detailsPane.add(initialWordCount);

        currentWordCount = new JLabel("?");

        detailsPane.add(new JLabel("Current word count:"));
        detailsPane.add(currentWordCount);

        wordsWrittenToday = new JLabel("0%");

        detailsPane.add(new JLabel("Words written in current session:"));
        detailsPane.add(wordsWrittenToday);

        dailyTarget = new JTextField("?");

        detailsPane.add(new JLabel("Daily target:"));
        detailsPane.add(dailyTarget);

        progress = new JLabel("0%");

        detailsPane.add(new JLabel("Progress:"));
        detailsPane.add(progress);


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

    public void setCurrentWordCount(final int wordCount) {
        this.currentWordCount.setText(Integer.toString(wordCount));
        final int initialWordCount = parseInt(this.initialWordCount.getText());
        final int wordsWritten = Math.max(0, wordCount - initialWordCount);
        this.wordsWrittenToday.setText(Integer.toString(wordsWritten));
        final int dailyTargetAmount = parseInt(this.dailyTarget.getText());
        final int progress = wordsWritten*100 / dailyTargetAmount;
        this.progress.setText(String.format("%d %%", progress));

        this.overviewProgress.setText(String.format("%d %% (%d out of %d)", progress, wordsWritten, dailyTargetAmount));
    }
}
