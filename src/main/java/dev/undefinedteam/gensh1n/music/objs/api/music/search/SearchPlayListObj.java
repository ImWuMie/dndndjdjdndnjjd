package dev.undefinedteam.gensh1n.music.objs.api.music.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchPlayListObj {
    @SerializedName("result")
    public _Result result;

    @SerializedName("code")
    public int code;

    public static class _Result {
        @SerializedName("playlists")
        public List<_Data> data;

        @SerializedName("playlistCount")
        public int total;
        @SerializedName("hlWords")
        public List<String> hlWords;
    }

    public static class _Data {
        @SerializedName("id")
        public long id;
        @SerializedName("name")
        public String name;
        @SerializedName("coverImgUrl")
        public String iconUrl;
        @SerializedName("creator")
        public _CreatorData creator;
        @SerializedName("trackCount")
        public int totalMusic;
        @SerializedName("playCount")
        public int totalPlays;
        @SerializedName("bookCount")
        public int totalBooks;
        @SerializedName("description")
        public String desc;
    }

    public static class _CreatorData {
        @SerializedName("nickname")
        public String name;
        @SerializedName("userId")
        public int id;
        @SerializedName("avatarUrl")
        public String iconUrl;
    }
}
