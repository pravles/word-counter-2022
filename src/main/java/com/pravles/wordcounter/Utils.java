package com.pravles.wordcounter;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

public final class Utils {
    private Utils() {
    }

    public static Point calculateCenterOnScreen(final JFrame window) {
        final int windowWidth = window.getWidth();
        final int windowHeight = window.getHeight();

        final Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize();

        final int screenWidth = (int) screenSize.getWidth();
        final int screenHeight = (int) screenSize.getHeight();

        final int x = (screenWidth - windowWidth)/2;
        final int y = (screenHeight - windowHeight)/2;

        return new Point(x, y);
    }
}
