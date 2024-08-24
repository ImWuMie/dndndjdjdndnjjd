package dev.undefinedteam.gensh1n.utils.render;

import dev.undefinedteam.gensh1n.utils.render.color.Color;

public class ColorUtils {
    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.awt().getColorComponents(rgb1);
        color2.awt().getColorComponents(rgb2);
        return new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir,1f);
    }

    public static Color blend(Color color1, Color color2) {
        return blend(color1, color2, 0.5);
    }
}
