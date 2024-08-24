package dev.undefinedteam.gensh1n.render._new;

import icyllis.modernui.annotation.RenderThread;
import net.minecraft.client.gui.DrawContext;

public class NText {
    public static NText INSTANCE;

    public NText() {
        INSTANCE = this;
    }

    public static final int TEXT_BUFFER_CAPACITY = 2097152;

    public static NTextRenderer regular;
    public static NTextRenderer regular13;
    public static NTextRenderer regular16;

    @RenderThread
    public void init() {
        regular = register(18);
        regular13 = register(13);
        regular16 = register(16);
    }

    private NTextRenderer register(float size) {
        return new NTextRenderer(TEXT_BUFFER_CAPACITY, size);
    }

    public static void begin(DrawContext context) {
        regular.begin(context.getMatrices());
        regular13.begin(context.getMatrices());
        regular16.begin(context.getMatrices());
    }

    public static void draw() {
        regular.end();
        regular13.end();
        regular16.end();
    }
}
