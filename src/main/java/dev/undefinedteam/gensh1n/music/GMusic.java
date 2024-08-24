package dev.undefinedteam.gensh1n.music;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.gui.overlay.Notifications;
import dev.undefinedteam.gensh1n.music.objs.CookieObj;
import dev.undefinedteam.gensh1n.music.objs.LyricSave;
import dev.undefinedteam.gensh1n.music.objs.SearchMusicObj;
import dev.undefinedteam.gensh1n.music.objs.message.MessageObj;
import dev.undefinedteam.gensh1n.music.objs.music.SongInfoObj;
import dev.undefinedteam.gensh1n.utils.network.Http;
import icyllis.modernui.graphics.BitmapFactory;
import icyllis.modernui.graphics.Image;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.undefinedteam.gensh1n.Client.GSON;

@Log4j2
public class GMusic {
    public static GMusic INSTANCE;

    public static final File FOLDER = new File(Client.FOLDER, "music");
    public static final File COOKIE_FILE = new File(FOLDER, "cookies.json");
    public CookieObj cookieObj;
    public MessageObj messageObj;

    public MusicApi api;

    public SongInfoObj current;
    public LyricSave currentLyric;
    public Image currentMImage;

    public GMusic() {
        INSTANCE = this;

        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
        }
    }

    public void init() {
        cookieObj = new CookieObj();
        messageObj = new MessageObj();
        api = new MusicApi();

        messageObj.init();
        try {
            if (!COOKIE_FILE.exists()) {
                saveCookie();
            }
            this.cookieObj = GSON.fromJson(Files.readString(COOKIE_FILE.toPath(), StandardCharsets.UTF_8), CookieObj.class);
        } catch (IOException e) {
            log.warn("Failed to load cookies. (>_<)");
        }
    }

    public void saveCookie() {
        try {
            if (!COOKIE_FILE.exists()) {
                COOKIE_FILE.createNewFile();
            }
            String data = GSON.toJson(this.cookieObj);
            Files.writeString(COOKIE_FILE.toPath(), data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Failed to save cookies. (>_<)");
        }
    }

    public static void setCurrent(SearchMusicObj obj) {
        if (INSTANCE != null) {
            if (obj == null) {
                INSTANCE.currentMImage = null;
                INSTANCE.current = null;
                INSTANCE.currentLyric = null;
                return;
            }
            INSTANCE.current = INSTANCE.api.getMusic(obj.id);
            INSTANCE.currentLyric = INSTANCE.api.getLyric(obj.id);
            if (INSTANCE.current != null && !INSTANCE.current.getPicUrl().isEmpty()) {
                try {
                    byte[] stream = Http.get(INSTANCE.current.getPicUrl()).sendBytes();
                    INSTANCE.currentMImage = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(stream, 0, stream.length));

                    var noti = Notifications.INSTANCE;
                    if (noti != null) {
                        var name = INSTANCE.current.getName() + " - " + INSTANCE.current.getAuthor();
                        noti.info("Current playing: {}", Notifications.SHORT, name);
                    }
                } catch (IOException e) {
                    INSTANCE.currentMImage = null;
                }
            }
        }
    }
}
