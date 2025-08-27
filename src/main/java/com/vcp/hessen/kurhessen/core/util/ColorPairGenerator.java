package com.vcp.hessen.kurhessen.core.util;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

public class ColorPairGenerator {

    public static class ColorPair {
        public final Color background;
        public final Color text;

        public ColorPair(@Nullable Color background, @Nullable Color text) {

            if (background == null && text == null) {
                this.background = randomColor();
                this.text = getReadableColor(this.background);
                return;
            }
            if (background == null) {
                this.text = text;
                this.background = getReadableColor(text);
                return;
            }
            if (text == null) {
                this.background = background;
                this.text = getReadableColor(background);
                return;
            }



            this.background = background;
            this.text = text;
        }

        @Override
        public String toString() {
            return "Background: " + toHex(background) + ", Text: " + toHex(text);
        }
    }

    public static ColorPair generateColorPair() {
        Color baseColor = randomColor();
        Color textColor = getReadableColor(baseColor);
        return new ColorPair(baseColor, textColor);
    }

    private static Color randomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    private static double luminance(Color color) {
        // Convert RGB to relative luminance
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        r = (r <= 0.03928) ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
        g = (g <= 0.03928) ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
        b = (b <= 0.03928) ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    public static Color getReadableColor(Color otherColor) {
        Color white = Color.WHITE;
        Color black = Color.BLACK;

        double lumBg = luminance(otherColor);
        double lumWhite = luminance(white);
        double lumBlack = luminance(black);

        double contrastWhite = (Math.max(lumBg, lumWhite) + 0.05) / (Math.min(lumBg, lumWhite) + 0.05);
        double contrastBlack = (Math.max(lumBg, lumBlack) + 0.05) / (Math.min(lumBg, lumBlack) + 0.05);

        return (contrastWhite >= contrastBlack) ? white : black;
    }

    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    // Example usage
    public static void main(String[] args) {
        ColorPair pair = generateColorPair();
        System.out.println(pair);
    }
}
