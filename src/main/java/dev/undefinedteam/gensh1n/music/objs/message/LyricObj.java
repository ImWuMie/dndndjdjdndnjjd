package dev.undefinedteam.gensh1n.music.objs.message;

public class LyricObj {
    public String data;
    public String tdata;
    public String empty1;
    public String empty2;
    public String empty3;

    public boolean check() {
        if (data == null)
            return true;
        if (tdata == null)
            return true;
        if (empty1 == null)
            return true;
        if (empty2 == null)
            return true;
        return empty3 == null;
    }

    public void init() {
        data = "%Lyric%";
        tdata = "%Lyric%(%TLyric%)";
        empty1 = "没有歌词";
        empty2 = "没有翻译歌词";
        empty3 = "没有KTV歌词";
    }

    public static LyricObj make() {
        LyricObj obj = new LyricObj();
        obj.init();

        return obj;
    }
}
