package com.pravles.wordcounter;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class Controller {
    private final static Logger logger = getLogger(Controller.class);
    void start(final WordCounterConfiguration config) {
        final File dir = config.directory();
        final File[] files = dir.listFiles((FileFilter) new WildcardFileFilter(config.pattern()));

        // Calculate word count at start
        int wordCount = 0;

        for (int i=0; i < files.length; i++) {
            try {
                wordCount += calculateWordCount(files[i]);
            } catch (final IOException exception) {
                logger.error(String.format("An error occurred while trying to count words in file '%s'", files[i].getAbsolutePath()), exception);
            }
        }


    }

    private int calculateWordCount(final File file) throws IOException {
        // Command to count words (except lines starting with #)
        // cat draft.org | sed '/^#/d' | wc -w
        final String command = String.format("cat %s | sed '/^#/d' | wc -w", file.getAbsolutePath());
        final CommandLine cmdLine = CommandLine.parse(command);
        final DefaultExecutor executor = new DefaultExecutor();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        executor.execute(cmdLine);
        final String x = outputStream.toString();
        return 0;
    }
}
