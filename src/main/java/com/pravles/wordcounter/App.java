package com.pravles.wordcounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.pravles.wordcounter.Utils.calculateCenterOnScreen;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);
    private final ConfigFileParser configFileParser;

    App(final ConfigFileParser configFileParser) {
        this.configFileParser = configFileParser;
    }

    public App() {
        this(new ConfigFileParser());
    }


    void run(final String[] args) {
        final Outcome<File> argsParsingResult = extractConfigFile(args);
        if (!argsParsingResult.success()) {
            logger.error(format("No configuration file specified or "+
                            "configuration is invalid ('%s')",
                    defaultString(argsParsingResult.message(), "")));
            System.exit(1);
        }
        final Outcome<WordCounterConfiguration> configParsingOutcome = configFileParser.parseConfigFile(argsParsingResult.value());

        if (!configParsingOutcome.success()) {
            logger.error(format("Error parsing configuration file ('%s')",
                   configParsingOutcome.message()));
            System.exit(2);
        }

        final Controller controller = new Controller();
        try {
            controller.start(configParsingOutcome.value());
        } catch (final IOException exception) {
            logger.error("Could not start the controller", exception);
            System.exit(3);
        }

        final MainWindow window = new MainWindow();
        window.setVisible(true);
        window.setLocation(calculateCenterOnScreen(window));
    }

    Outcome<File> extractConfigFile(final String[] args) {
        if (args == null) {
            return new Outcome<File>().success(false).message("args is null");
        }
        if (args.length != 1) {
            return new Outcome<File>().success(false)
                    .message(format("Incorrect number of arguments, " +
                            "got %d instead of 1", args.length));
        }
        final String configFilePath = args[0];
        final File configFile = new File(configFilePath);
        if (!configFile.exists() || !configFile.isFile() ||
                !configFile.canRead()) {
            return new Outcome<File>().success(false)
                    .message(format("Configuration file '%s' either does " +
                            "not exist, is not a file or not readable",
                            configFile.getAbsolutePath()));
        }
        return new Outcome<File>().success(true).value(configFile);
    }

    public static void main(final String[] args) {
        final App app = new App();
        app.run(args);
    }
}
