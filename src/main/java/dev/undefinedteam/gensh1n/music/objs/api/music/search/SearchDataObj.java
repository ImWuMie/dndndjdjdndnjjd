package dev.undefinedteam.gensh1n.music.objs.api.music.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchDataObj {
    @SerializedName("result")
    private result result;

    public boolean isOk() {
        return result != null && result.getSongs() != null;
    }

    public List<songs> getResult() {
        return result.getSongs();
    }
}

class result {
    @SerializedName("songs")
    private List<songs> songs;

    public List<songs> getSongs() {
        return songs;
    }
}


class artists {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}

class album {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
