package com.pravles.wordcounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.pravles.wordcounter.Utils.calculateCenterOnScreen;

public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    void run(final String[] args) {
        final Outcome<File> argsParsingResult = extractConfigFile(args);
        if (argsParsingResult.success()) {
            final MainWindow window = new MainWindow();
            window.setVisible(true);
            window.setLocation(calculateCenterOnScreen(window));
        } else {
            logger.error("No configuration file specified");
        }
    }

    Outcome<File> extractConfigFile(final String[] args) {
        return new Outcome<File>().success(false);
    }

    public static void main(final String[] args) {
        final App app = new App();
        app.run(args);
    }
}
