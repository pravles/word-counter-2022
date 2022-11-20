package com.pravles.wordcounter;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Outcome<C> {
    private boolean success;
    private C value;
}
