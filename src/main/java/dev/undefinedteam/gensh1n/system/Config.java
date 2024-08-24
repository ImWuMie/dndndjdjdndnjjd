package dev.undefinedteam.gensh1n.system;

import com.google.gson.JsonObject;
import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gclient.packets.c2s.play.ChatMessageC2S;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.game.SendMessageEvent;
import dev.undefinedteam.gensh1n.jvm.service.decompiler.FernFlowerConfig;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.settings.Settings;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import meteordevelopment.orbit.EventHandler;

public class Config extends System<Config> implements SettingAdapter {
    public final Settings settings = new Settings();

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgCommand = settings.createGroup("Command");
    private final SettingGroup sgIRC = settings.createGroup("GIRC");
    public Setting<Double> rainbowSpeed = doubleN(sgDefault, "rainbow-speed", 10, 0, 10);
    public Setting<Boolean> msaa = bool(sgCommand, "msaa-framebuffer", "USE MSAA (FPS--)", true);

    public Setting<Boolean> calledByScreen = bool(sgCommand, "called-by-screen", "Should call commands' dispatch on overlay screen", false);
    public Setting<String> commandPrefix = text(sgCommand, "prefix", ".");

    public final Setting<Boolean> chatFeedback = bool(sgCommand, "chat-feedback", "Sends chat feedback when client performs certain actions.", true);
    public final Setting<Boolean> deleteChatFeedback = bool(sgCommand, "delete-chat-feedback", "Delete previous matching chat feedback to keep chat clear.", true, chatFeedback::get);

    public final Setting<String> prefix = text(sgIRC, "chat-irc-prefix", "@");

    public final FernFlowerConfig fernFlower = new FernFlowerConfig(this.settings);

    public Config() {
        super("config");
    }

    public static Config get() {
        return Systems.get(Config.class);
    }

    @EventHandler
    private void onChat(SendMessageEvent e) {
        var p = this.prefix.get();
        var msg = e.message;
        if (msg.startsWith(p)) {
            var text = msg.substring(p.length());
            if (GCClient.get().session() != null) {
                GCClient.get().session().send(new ChatMessageC2S(text));
            } else ChatUtils.error("GChat未连接");
            e.cancel();
        }
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        tag.addProperty("version", Client.VERSION);
        tag.add("settings", settings.toTag());

        return tag;
    }

    @Override
    public Config fromTag(JsonObject tag) {
        if (tag.has("settings")) settings.fromTag(tag.getAsJsonObject("settings"));

        return this;
    }
}
