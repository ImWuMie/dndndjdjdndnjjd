package dev.undefinedteam.gensh1n.music.objs.api.music.trialinfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrialInfoObj {
    @SerializedName("data")
    private List<song> data;

    public boolean isTrial() {
        if (data.size() == 0)
            return false;
        song song = data.get(0);
        return song.getCode() != 200;
    }

    public freeTrialInfo getFreeTrialInfo() {
        song song = data.get(0);
        return song.getFreeTrialInfo() == null ? new freeTrialInfo() {{
            this.setEnd(30);
        }} : song.getFreeTrialInfo();
    }

    public String getUrl() {
        if (data.size() == 0)
            return null;
        song song = data.get(0);
        return song.getUrl();
    }
}

class song {
    @SerializedName("freeTrialInfo")
    private freeTrialInfo freeTrialInfo;
    @SerializedName("code")
    private int code;
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public int getCode() {
        return code;
    }

    public freeTrialInfo getFreeTrialInfo() {
        return freeTrialInfo;
    }
}
