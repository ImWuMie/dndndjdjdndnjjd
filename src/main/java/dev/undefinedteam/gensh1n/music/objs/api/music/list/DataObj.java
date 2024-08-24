package dev.undefinedteam.gensh1n.music.objs.api.music.list;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataObj {
    @SerializedName("playlist")
    private playlist playlist;

    public List<String> getPlaylist() {
        List<String> list = new ArrayList<>();
        for (track item : playlist.getTracks()) {
            list.add(item.getId());
        }
        return list;
    }

    public String getName() {
        return playlist.getName();
    }
}

class track {
    @SerializedName("id")
    private int id;

    public String getId() {
        return String.valueOf(id);
    }
}

class playlist {
    @SerializedName("trackIds")
    private List<track> trackIds;
    @SerializedName("name")
    private String name;

    public List<track> getTracks() {
        return trackIds;
    }

    public String getName() {
        return name;
    }
}
