package dev.undefinedteam.gensh1n.system.hud.elements;

import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.render._new.NTextRenderer;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.hud.Alignment;
import dev.undefinedteam.gensh1n.system.hud.ElementInfo;
import dev.undefinedteam.gensh1n.system.hud.HudElement;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ActiveMods extends HudElement {
    public static final ElementInfo INFO = new ElementInfo("ActiveMods", "Show active mods", ActiveMods.class);

    public ActiveMods() {
        super(INFO);
    }

    private static final Color WHITE = new Color();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Sort> sort = choice(sgGeneral, "sort", "How to sort active modules.", Sort.Biggest);

    private final Setting<Boolean> activeInfo = bool(sgGeneral, "additional-info", "Shows additional info from the module next to the name in the active modules list.", false);

    private final Setting<SettingColor> moduleInfoColor = color(sgGeneral, "module-info-color", "Color of module info text.", new SettingColor(175, 175, 175), activeInfo::get);

    private final Setting<ColorMode> colorMode = choice(sgGeneral, "color-mode", "What color to use for active modules.", ColorMode.Rainbow);

    private final Setting<SettingColor> flatColor = color(sgGeneral, "flat-color", "Color for flat color mode.", new SettingColor(225, 25, 25), () -> colorMode.get() == ColorMode.Flat);

    private final Setting<Boolean> shadow = bool(sgGeneral, "shadow", "Renders shadow behind text.", true);

    private final Setting<Alignment> alignment = choice(sgGeneral, "alignment", "Horizontal alignment.", Alignment.Auto);

    private final Setting<Double> rainbowSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("Rainbow speed of rainbow color mode.")
        .defaultValue(0.05)
        .min(0.01)
        .max(0.2)
        .decimalPlaces(4)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    private final Setting<Double> rainbowSpread = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-spread")
        .description("Rainbow spread of rainbow color mode.")
        .defaultValue(0.01)
        .min(0.001)
        .max(0.05)
        .decimalPlaces(4)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    private final Setting<Double> rainbowSaturation = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-saturation")
        .defaultValue(1.0d)
        .range(0.0d, 1.0d)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    private final Setting<Double> rainbowBrightness = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-brightness")
        .defaultValue(1.0d)
        .range(0.0d, 1.0d)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    private final List<Module> modules = new ArrayList<>();

    private final Color rainbow = new Color(255, 255, 255);
    private double rainbowHue1;
    private double rainbowHue2;


    @Override
    public void tick() {
        var font = NText.regular;

        modules.clear();
        modules.addAll(Modules.get().getActive());

        if (modules.isEmpty()) {
            if (inEdit) {
                var str = "Active Modules";
                setElementSize((int) (font.getWidth(str,this.shadow.get())), (int) (font.getHeight(str,this.shadow.get())));
            }
            return;
        }

        modules.sort((e1, e2) -> switch (sort.get()) {
            case Alphabetical -> e1.title.compareTo(e2.title);
            case Biggest -> Double.compare(getModuleWidth(font, e2), getModuleWidth(font, e1));
            case Smallest -> Double.compare(getModuleWidth(font, e1), getModuleWidth(font, e2));
        });

        double width = 0;
        double height = 0;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            width = Math.max(width, getModuleWidth(font, module));
            height += font.getHeight(module.title,this.shadow.get());
            if (i > 0) height += 2;
        }

        setElementSize((int) width, (int) height);
    }

    @Override
    public void render(DrawContext context,float delta) {
        double x = this.x;
        double y = this.y;

        var font = NText.regular;
        var renderer = Renderer.MAIN;

        if (modules.isEmpty()) {
            if (inEdit) {
                font.draw("Active Modules", x, y, WHITE.getPacked(), shadow.get());
            }
            return;
        }

        rainbowHue1 += rainbowSpeed.get() * delta;
        if (rainbowHue1 > 1) rainbowHue1 -= 1;
        else if (rainbowHue1 < -1) rainbowHue1 += 1;

        rainbowHue2 = rainbowHue1;
        for (int i = 0; i < modules.size(); i++) {
            double offset = alignX(getModuleWidth(font, modules.get(i)), alignment.get());
            renderModule(font,renderer, modules, i, x + offset, y);

            y += 2 + font.getHeight(modules.get(i).title,this.shadow.get());
        }
    }

    private void renderModule(NTextRenderer font, Renderer renderer, List<Module> modules, int index, double x, double y) {
        Module module = modules.get(index);
        Color color = flatColor.get();

        switch (colorMode.get()) {
            case Random -> color = module.color;
            case Rainbow -> {
                rainbowHue2 += rainbowSpread.get();
                int c = java.awt.Color.HSBtoRGB((float) rainbowHue2, rainbowSaturation.get().floatValue(), rainbowBrightness.get().floatValue());
                rainbow.r = Color.toRGBAR(c);
                rainbow.g = Color.toRGBAG(c);
                rainbow.b = Color.toRGBAB(c);
                color = rainbow;
            }
        }

        font.draw(module.title, x, y, color.getPacked(),this.shadow.get());

        double emptySpace = font.getWidth(" ");
        double textLength = font.getWidth(module.title,this.shadow.get());

        if (activeInfo.get()) {
            String info = module.getInfoString();
            if (info != null) {
                font.draw(info, x + emptySpace + textLength, y, moduleInfoColor.get().getPacked(), shadow.get());
                textLength += emptySpace + font.getWidth(info,this.shadow.get());
            }
        }
    }

    private double getModuleWidth(NTextRenderer renderer, Module module) {
        double width = renderer.getWidth(module.title,this.shadow.get());

        if (activeInfo.get()) {
            String info = module.getInfoString();
            if (info != null) width += renderer.getWidth(" ") + renderer.getWidth(info,this.shadow.get());
        }

        return width;
    }


    public enum Sort {
        Alphabetical,
        Biggest,
        Smallest
    }

    public enum ColorMode {
        Flat,
        Random,
        Rainbow
    }
}
