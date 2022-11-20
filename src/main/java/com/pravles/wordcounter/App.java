package com.pravles.wordcounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pravles.wordcounter.Utils.calculateCenterOnScreen;

public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(final String[] args) {
        logger.info("Hello");
        final MainWindow window = new MainWindow();
        window.setVisible(true);
        window.setLocation(calculateCenterOnScreen(window));
    }
}
