package dev.undefinedteam.gensh1n.music.objs.api.music.lyric;

import com.google.gson.annotations.SerializedName;

public class WLyricObj {
    @SerializedName("lrc")
    private lrc lrc;
    @SerializedName("tlyric")
    private tlyric tlyric;
    @SerializedName("klyric")
    private klyric klyric;
    @SerializedName("yrc")
    private yrc yrc;
    @SerializedName("nolyric")
    private boolean nolyric;
    @SerializedName("uncollected")
    private boolean uncollected;

    public boolean isOk() {
        return lrc != null || isNone();
    }

    public String getLyric() {
        return lrc.getLyric();
    }

    public String getTlyric() {
        if (tlyric == null)
            return null;
        return tlyric.getLyric();
    }

    public String getKlyric() {
        if (klyric == null)
            return null;
        return klyric.getLyric();
    }

    public String getYrc() {
        if (yrc == null)
            return null;
        return yrc.getLyric();
    }

    public boolean getVersion() {
        return yrc != null;
    }

    public boolean isNone() {
        return nolyric || uncollected;
    }
}

class lrc {
    @SerializedName("lyric")
    private String lyric;

    public String getLyric() {
        return lyric == null ? "" : lyric;
    }
}

class tlyric {
    @SerializedName("lyric")
    private String lyric;

    public String getLyric() {
        return lyric;
    }
}

class klyric {
    @SerializedName("lyric")
    private String lyric;
    @SerializedName("version")
    private int version;

    public int getVersion() {
        return version;
    }

    public String getLyric() {
        return lyric == null ? "" : lyric;
    }
}

class yrc {
    @SerializedName("version")
    private int version;
    @SerializedName("lyric")
    private String lyric;

    public int getVersion() {
        return version;
    }

    public String getLyric() {
        return lyric == null ? "" : lyric;
    }
}
