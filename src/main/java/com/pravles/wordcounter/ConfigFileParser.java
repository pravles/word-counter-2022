package com.pravles.wordcounter;

import java.io.File;

public class ConfigFileParser {
    Outcome<WordCounterConfiguration> parseConfigFile(final File configFile) {
        return new Outcome<WordCounterConfiguration>()
                .success(false)
                .message("Not implemented");
    }
}
