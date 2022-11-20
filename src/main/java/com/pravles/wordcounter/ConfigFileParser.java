package com.pravles.wordcounter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.io.Charsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;
import static us.bpsm.edn.parser.Parsers.defaultConfiguration;

public class ConfigFileParser {
    private final static Logger logger = getLogger(ConfigFileParser.class);

    Outcome<WordCounterConfiguration> parseConfigFile(final File configFile) {

        final String edn;
        try {
            edn = FileUtils.readFileToString(configFile, UTF_8);
        } catch (final IOException exception) {
            logger.error(format("Error while parsing configuration file '%s'", configFile.getAbsolutePath()), exception);
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message(exception.getMessage());
        }
        Parseable pbr = Parsers.newParseable(edn);
        Parser p = Parsers.newParser(defaultConfiguration());
        final Map<?, ?> data = (Map<?, ?>) p.nextValue(pbr);


        final Map<?, ?> whereToCountWordsIn = (Map<?, ?>)data.get(Keyword.newKeyword("where-to-count-words-in"));
        final String path = (String) whereToCountWordsIn.get(Keyword.newKeyword("directory"));
        final String pattern = (String) whereToCountWordsIn.get(Keyword.newKeyword("pattern"));

        final File directory = new File(path);

        if (!directory.exists() || !directory.canRead() || !directory.isDirectory()) {
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message(format("Directory '%s' does not exist, is not readable or is not a directory at all", directory.getAbsolutePath()));
        }

        final FileFilter fileFilter = new WildcardFileFilter(pattern);
        final File[] filesInDirectory = directory.listFiles(fileFilter);

        if (filesInDirectory == null) {
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message(format("Failed to read a list of " +
                            "files in directory '%s'",
                            directory.getAbsolutePath()));
        }

        if (filesInDirectory.length < 1) {
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message(format("Directory '%s' does not contain any " +
                                    "files matching the pattern '%s'",
                            directory.getAbsolutePath(), pattern));
        }

        final Long defaultTarget = (Long) data.get(Keyword.newKeyword("default-target"));

        if (defaultTarget == null) {
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message("Default daily target not specified");
        }

        if (defaultTarget <= 0) {
            return new Outcome<WordCounterConfiguration>()
                    .success(false)
                    .message(format("Default daily target %d is " +
                                    "invalid (zero or less)", defaultTarget));
        }

        return new Outcome<WordCounterConfiguration>()
                .success(true)
                .value(new WordCounterConfiguration()
                        .defaultTarget(defaultTarget)
                        .directory(directory)
                        .pattern(pattern));
    }
}
