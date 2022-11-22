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
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static org.slf4j.LoggerFactory.getLogger;

public class Controller {
    private final static Logger logger = getLogger(Controller.class);

    private final Runtime runtime;
    private WatchService watcher;
    private Map<WatchKey, Path> keys;
    private WildcardFileFilter fileFilter;
    private ConcurrentHashMap<String, Integer> wordCountByFilename;
    private MainWindow window;

    public Controller() {
        this(Runtime.getRuntime());
    }

    Controller(final Runtime runtime) {
        this.runtime = runtime;
    }

    void start(final WordCounterConfiguration config, MainWindow window) throws IOException {
        this.window = window;

        final File dir = config.directory();
        final int initialWordCount = countWords(config, dir);

        fileFilter = new WildcardFileFilter(config.pattern());

        logger.info(format("Initial word count: %d", initialWordCount));

        logger.info("Setting up observation of the directory with text...");
        startObservingDirectory(config.directory());
        logger.info("Done");

        final Thread processEventsThread =
                new Thread(this::processEvents);
        final Thread stopProcessEventsThread =
                new Thread(processEventsThread::interrupt);

        runtime.addShutdownHook(stopProcessEventsThread);

        this.window.setInitialWordCount(initialWordCount);
        this.window.setDailyTarget(config.defaultTarget());

        this.window.centerOnScreen();
        this.window.pack();
        this.window.setVisible(true);

        processEventsThread.start();
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

            final Path dir = keys.get(key);
            if (dir == null) {
                logger.error("Watch key not recognized");
                continue;
            }

            for (final WatchEvent<?> event : key.pollEvents()) {
                final WatchEvent.Kind kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }
                final WatchEvent<Path> ev = cast(event);
                final Path name = ev.context();
                final Path child = dir.resolve(name);

                processEvent(event, child);
            }
            final boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void processEvent(final WatchEvent<?> event, final Path child) {
        final File file = child.toFile();
        final String path = file.getAbsolutePath();
        if (!fileFilter.accept(file)) {
            return;
        }

        final String eventName = event.kind().name();
        if ("ENTRY_DELETE".equals(eventName)) {
            if (wordCountByFilename.containsKey(path)) {
                wordCountByFilename.remove(path);

                final int currentWordCount = countWords();
                this.window.setCurrentWordCount(currentWordCount);
            }
        } else if ("ENTRY_CREATE".equals(eventName) || "ENTRY_MODIFY".equals(eventName)) {
            final int newFileWordCount;
            try {
                newFileWordCount = calculateWordCount(file);

                wordCountByFilename.put(path, newFileWordCount);

                final int currentWordCount = countWords();
                this.window.setCurrentWordCount(currentWordCount);
            } catch (final IOException exception) {
                logger.error(String.format("An error occurred while counting words in file '%s'", file.getAbsolutePath()), exception);
            }
        }

        // ENTRY_MODIFY

        logger.info(String.format("%s: %s", eventName, child));

    }


    private int countWords(WordCounterConfiguration config, File dir) {
        final File[] files = dir.listFiles((FileFilter) new WildcardFileFilter(config.pattern()));

        wordCountByFilename = new ConcurrentHashMap<>(files.length);

        // Calculate word count at start
        int wordCount = 0;

        for (File file : files) {
            try {
                final int currentWordcount =  calculateWordCount(file);
                wordCount += currentWordcount;
                wordCountByFilename.put(file.getAbsolutePath(), currentWordcount);
            } catch (final IOException exception) {
                logger.error(format("An error occurred while trying to count words in file '%s'", file.getAbsolutePath()), exception);
            }
        }
        return wordCount;
    }

    private int countWords() {
        return this.wordCountByFilename
                .values().stream().mapToInt(Integer::intValue).sum();
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
