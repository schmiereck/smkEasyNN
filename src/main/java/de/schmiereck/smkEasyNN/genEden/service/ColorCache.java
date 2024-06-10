package de.schmiereck.smkEasyNN.genEden.service;

import javafx.scene.paint.Color;

import java.util.Objects;

public class ColorCache {
    /**
     * 16_777_216 = 256 * 256 * 256
     */
    private final Color[][][] colorCachceArr = new Color[256][256][256];

    public Color retrieveColor(final double red, final double green, final double blue) {
        final Color retColor;
        final int r = (int) (red * 255);
        final int g = (int) (green * 255);
        final int b = (int) (blue * 255);
        final Color cacheColor = this.colorCachceArr[r][g][b];

        if (Objects.nonNull(cacheColor)) {
            retColor = cacheColor;
        } else {
            retColor = Color.color(red, green, blue);
            this.colorCachceArr[r][g][b] = retColor;
        }
        return retColor;
    }
}
