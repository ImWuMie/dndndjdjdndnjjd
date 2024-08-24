package dev.undefinedteam.gensh1n.music.objs.api.music.trialinfo;

import com.google.gson.annotations.SerializedName;

public class freeTrialInfo {
    @SerializedName("start")
    private int start;
    @SerializedName("end")
    private int end;

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }
}
