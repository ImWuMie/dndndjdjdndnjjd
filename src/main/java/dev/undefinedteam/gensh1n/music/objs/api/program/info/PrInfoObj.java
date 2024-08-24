package dev.undefinedteam.gensh1n.music.objs.api.program.info;

import com.google.gson.annotations.SerializedName;

public class PrInfoObj {
    @SerializedName("program")

    private program program;

    public boolean isOk() {
        return (program != null && program.getMainSong().getName() != null);
    }

    public String getId() {

        return String.valueOf(program.getMainSong().getId());
    }

    public String getName() {

        return program.getMainSong().getName();
    }

    public String getAlia() {
        if (program.getDj() == null)
            return null;
        return program.getDj().getBrand();
    }

    public int getLength() {
        return program.getMainSong().getLength();
    }

    public String getAuthor() {
        return program.getDj().getNickname();
    }
}

class mainSong {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private int id;
    @SerializedName("hMusic")
    private hMusic hMusic;
    @SerializedName("mMusic")
    private hMusic mMusic;
    @SerializedName("lMusic")
    private hMusic lMusic;
    @SerializedName("bMusic")
    private hMusic bMusic;

    public int getLength() {
        if (hMusic != null)
            return hMusic.getLength();
        else if (mMusic != null)
            return mMusic.getLength();
        else if (lMusic != null)
            return lMusic.getLength();
        else if (bMusic != null)
            return bMusic.getLength();
        return 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class program {
    @SerializedName("dj")
    private dj dj;
    @SerializedName("mainSong")
    private mainSong mainSong;

    public dj getDj() {
        return dj;
    }

    public mainSong getMainSong() {
        return mainSong;
    }
}

class dj {
    @SerializedName("brand")
    private String brand;
    @SerializedName("nickname")
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public String getBrand() {
        return brand;
    }
}

class hMusic {
    @SerializedName("size")
    private int size;
    @SerializedName("bitrate")
    private int bitrate;

    public int getLength() {
        return size / bitrate * 8000;
    }
}
