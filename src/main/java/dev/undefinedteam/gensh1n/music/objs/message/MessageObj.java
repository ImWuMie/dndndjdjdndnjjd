package dev.undefinedteam.gensh1n.music.objs.message;

public class MessageObj {
    public LyricObj lyric;
    public CustomObj custom;
    public PAPIObj papi;
    public String version;

    public boolean check() {
        if (lyric == null || lyric.check()) {
            lyric = LyricObj.make();
        }
        if (custom == null || custom.check()) {
            custom = CustomObj.make();
        }
        if (papi == null || papi.check()) {
            papi = PAPIObj.make();
        }
        return true;
    }

    public void init() {
        lyric = LyricObj.make();
        custom = CustomObj.make();
        papi = PAPIObj.make();
    }

    public static MessageObj make() {
        MessageObj obj = new MessageObj();
        obj.init();

        return obj;
    }
}
