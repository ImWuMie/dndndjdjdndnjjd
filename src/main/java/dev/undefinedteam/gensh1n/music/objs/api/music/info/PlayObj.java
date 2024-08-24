package dev.undefinedteam.gensh1n.music.objs.api.music.info;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlayObj {
    @SerializedName("data")
    private List<obj> data;
    @SerializedName("code")
    private int code;

    public String getData() {
        if (data == null)
            return null;
        if (data.size() == 0)
            return null;
        obj obj = data.get(0);
        return obj.getUrl();
    }

    public int getCode() {
        return code;
    }
}

class obj {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }
}

