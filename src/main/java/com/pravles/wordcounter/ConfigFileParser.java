package com.pravles.wordcounter;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static us.bpsm.edn.parser.Parsers.defaultConfiguration;

public class ConfigFileParser {
    private final static Logger logger = getLogger(ConfigFileParser.class);

    Outcome<WordCounterConfiguration> parseConfigFile(final File configFile) {

        final String edn;
        try {
            edn = FileUtils.readFileToString(configFile, Charsets.UTF_8);
        } catch (final IOException exception) {
            logger.error(String.format("Error while parsing configuration file '%s'", configFile.getAbsolutePath()), exception);
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message(exception.getMessage());
        }
        Parseable pbr = Parsers.newParseable(edn);
        Parser p = Parsers.newParser(defaultConfiguration());
        final Map<?, ?> data = (Map<?, ?>) p.nextValue(pbr);

        final Long defaultTarget = (Long) data.get(Keyword.newKeyword("default-target"));

        final Map<?, ?> whereToCountWordsIn = (Map<?, ?>)data.get(Keyword.newKeyword("where-to-count-words-in"));
        final String path = (String) whereToCountWordsIn.get(Keyword.newKeyword("directory"));
        final String pattern = (String) whereToCountWordsIn.get(Keyword.newKeyword("pattern"));

        final File directory = new File(path);

        if (!directory.exists() || !directory.canRead() || !directory.isDirectory()) {
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message(String.format("Directory '%s' does not exist, is not readable or is not a directory at all", directory.getAbsolutePath()));
        }


        return new Outcome<WordCounterConfiguration>()
                .success(false)
                .message("Not implemented");
    }
}
