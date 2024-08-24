package dev.undefinedteam.gensh1n.music.objs.music;

import dev.undefinedteam.gensh1n.music.GMusic;
import lombok.ToString;

public class LyricItemObj {
    public String lyric;
    public String tlyric;

    public LyricItemObj(String lyric, String tlyric) {
        this.lyric = lyric;
        this.tlyric = tlyric;
    }

    public String getString() {
        if (lyric == null || lyric.isEmpty())
            return "";
        String data;
        if (tlyric != null && !tlyric.isEmpty()) {
            data = GMusic.INSTANCE.messageObj.lyric.tdata;
            return data.replace("%Lyric%", lyric)
                    .replace("%TLyric%", tlyric);
        }

        data = GMusic.INSTANCE.messageObj.lyric.data;
        return data.replace("%Lyric%", lyric);
    }
}
