package com.pravles.wordcounter;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

@Data
@Accessors(fluent = true)
public class WordCounterConfiguration {
    private long defaultTarget;
    private File directory;
    private String pattern;

}
