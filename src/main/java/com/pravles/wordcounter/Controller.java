package com.pravles.wordcounter;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static org.slf4j.LoggerFactory.getLogger;

public class Controller {
    private final static Logger logger = getLogger(Controller.class);

    private WatchService watcher;
    private Map<WatchKey, Path> keys;

    void start(final WordCounterConfiguration config) throws IOException {
        final File dir = config.directory();
        final int wordCount = countWords(config, dir);
        logger.info(format("Initial word count: %d", wordCount));

        startObservingDirectory(config.directory());
    }

    private void startObservingDirectory(final File directory) throws IOException {

        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        register(directory);

    }

    private void register(File directory) throws IOException {
       final Path path = Paths.get(directory.toURI());
       final WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
       keys.put(key, path);
    }
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    private void processEvents() {
        for (;;) {
            // Wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException exception) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                logger.error("Watch key not recognized");
                continue;
            }

            for (final WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                logger.info(String.format("%s: %s", event.kind().name(), child));
            }
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private int countWords(WordCounterConfiguration config, File dir) {
        final File[] files = dir.listFiles((FileFilter) new WildcardFileFilter(config.pattern()));

        // Calculate word count at start
        int wordCount = 0;

        for (int i=0; i < files.length; i++) {
            try {
                wordCount += calculateWordCount(files[i]);
            } catch (final IOException exception) {
                logger.error(format("An error occurred while trying to count words in file '%s'", files[i].getAbsolutePath()), exception);
            }
        }
        return wordCount;
    }

    private int calculateWordCount(final File file) throws IOException {
        final String command = format("cat %s | sed '/^#/d' | wc -w", file.getAbsolutePath());
        final CommandLine cmdLine = new CommandLine("/bin/sh");
        cmdLine.addArguments(new String[]{
                "-c",
                command
        }, false);
        final DefaultExecutor executor = new DefaultExecutor();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        executor.execute(cmdLine);
        final String resultTxt = outputStream.toString();
        return Integer.parseInt(StringUtils.trim(resultTxt));
    }
}
