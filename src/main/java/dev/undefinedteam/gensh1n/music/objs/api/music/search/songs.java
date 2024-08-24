package dev.undefinedteam.gensh1n.music.objs.api.music.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class songs {
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("artists")
    private List<artists> artists;
    @SerializedName("album")
    private album album;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album.getName();
    }

    public String getArtists() {
        StringBuilder a = new StringBuilder();
        for (artists temp : artists) {
            a.append(temp.getName()).append(",");
        }
        return a.substring(0, a.length() - 1);
    }
}
