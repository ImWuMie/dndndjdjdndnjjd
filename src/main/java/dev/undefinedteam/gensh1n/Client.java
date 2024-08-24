package dev.undefinedteam.gensh1n;

import com.google.gson.Gson;
import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.render.Fonts;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.utils.json.GsonUtils;
import dev.undefinedteam.gensh1n.utils.predict.ExtrapolationUtils;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class Client {
    public static final String NAME = "Genshin-Light";
    public static final String LC_NAME = NAME.toLowerCase();
    public static final String VERSION = "1.0.0";
    public static final int VERSION_ID = 10;
    public static final String DEV = "wumie yurnu";
    public static final String ASSETS_LOCATION = "gensh1n";

    public static final Gson GSON = GsonUtils.newBuilder().create();

    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final File FOLDER = new File("_" + NAME + "_");

    public static final IEventBus EVENT_BUS = new EventBus();
    public static final String NAME_F = "Gensh1n Light";
    public static final String SINGLE_SPECIAL_NAME = "\uD835\uDD72";
    public static final String FULL_UPPER1_SPECIAL_NAME = "\uD835\uDD72";
    public static final String FULL_UPPER_SPECIAL_NAME = "\uD835\uDD72\uD835\uDD70\uD835\uDD79\uD835\uDD7E\uD835\uDD73\uD835\uDD74\uD835\uDD79";
    public static final String FULL_SPECIAL_NAME = "\uD835\uDD72\uD835\uDD8A\uD835\uDD93\uD835\uDD98\uD835\uDD8D\uD835\uDD8E\uD835\uDD93";

    public static boolean isOnMinecraftEnv() {
        return mc != null;
    }

    static {
        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
        }

        EVENT_BUS.registerLambdaFactory("dev.undefinedteam", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        EVENT_BUS.registerLambdaFactory("icyllis.modernui", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
    }

    public static void init() {
        EVENT_BUS.subscribe(ExtrapolationUtils.class);
        try {
            GChat.get().startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GL.init();
    }
}
