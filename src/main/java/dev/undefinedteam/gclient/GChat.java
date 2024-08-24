package dev.undefinedteam.gclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.undefinedteam.gclient.assets.AssetsManager;
import dev.undefinedteam.gclient.data.GsonUtils;
import dev.undefinedteam.gclient.text.Style;
import dev.undefinedteam.gclient.text.Text;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GChat {
    public static final int VERSION_ID = 10;
    public static GChat INSTANCE;
    public final File FOLDER = new File("_Genshin_", "gchat");
    public final File CFG = new File(FOLDER, "user.json");
    public static final String SINGLE_SPECIAL_NAME = "\uD835\uDD72";
    public static final String SPECIAL_NAME = "GChat";
    public static final Text LOG_PREFIX = Text.of(SPECIAL_NAME).setStyle(Style.EMPTY.withFormatting(Formatting.GOLD)).append(Text.of(">>").setStyle(Style.EMPTY.withFormatting(Formatting.BOLD, Formatting.GRAY)));//Formatting.GOLD + SPECIAL_NAME + Formatting.BOLD + Formatting.GRAY + ">>";
    public static final Gson GSON = GsonUtils.newBuilder().create();

    public String username, token;

    public boolean success = true;
    public boolean isQuit = false;

    public GChat() {
        INSTANCE = this;
        if (!FOLDER.exists()) FOLDER.mkdirs();

        try {
            if (CFG.exists()) {
                JsonObject object = new JsonParser().parse(new BufferedReader(new FileReader(CFG))).getAsJsonObject();
                this.username = object.get("name").getAsString();
                this.token = new String(Base64.decodeBase64(object.get("d").getAsString()), StandardCharsets.UTF_8);
            }

            GCClient.get().init();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
    }

    public static GChat get() {
        if (INSTANCE == null) {
            INSTANCE = new GChat();
        }
        return INSTANCE;
    }

    public void startClient() throws IOException {
        if (success) {
            GCClient.get().connect();
        }
    }

    public void save() throws IOException {
        AssetsManager.INSTANCE.save();
        saveUser();
    }

    public void saveUser() throws IOException {
        JsonObject data = new JsonObject();
        data.addProperty("name", this.username);
        data.addProperty("d", Base64.encodeBase64String(this.token.getBytes(StandardCharsets.UTF_8)));
        if (!CFG.exists()) CFG.createNewFile();

        Files.writeString(CFG.toPath(), GSON.toJson(data), StandardCharsets.UTF_8);
    }
}
