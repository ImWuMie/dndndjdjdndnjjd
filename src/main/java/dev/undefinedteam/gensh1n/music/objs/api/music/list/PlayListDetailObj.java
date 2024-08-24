package dev.undefinedteam.gensh1n.music.objs.api.music.list;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.MusicApi;
import dev.undefinedteam.gensh1n.music.objs.api.music.search.SearchPlayListObj;
import dev.undefinedteam.gensh1n.music.objs.music.SongInfoObj;
import dev.undefinedteam.gensh1n.utils.json.GsonIgnore;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class PlayListDetailObj {
    @SerializedName("code")
    public int code;
    @SerializedName("playlist")
    public _Data data;

    @GsonIgnore
    public final List<Page> pages = new ArrayList<>();

    public Page getPage(int index) {
        return pages.get(index);
    }

    public void loadPages() {
        int index = data.trackIds.size();
        List<_TrackIdData> temp = new ArrayList<>(10);
        for (_TrackIdData trackIdData : data.trackIds) {
            index--;
            temp.add(trackIdData);
            if (temp.size() == 10 || index <= 0) {
                pages.add(new Page(temp));
                temp.clear();
            }
        }
    }

    @ToString
    public static class Page {
        protected List<_TrackIdData> trackIds = new ArrayList<>(10);
        public List<SongInfoObj> songs = new ArrayList<>(10);

        public Page(List<_TrackIdData> trackIds) {
            this.trackIds.addAll(trackIds);
        }

        public void loadInfo(MusicApi api) {
            songs.clear();
            for (_TrackIdData d : this.trackIds) {
                songs.add(api.getMusic(String.valueOf(d.musicId)));
            }
        }

        public int size() {
            return trackIds.size();
        }
    }

    public static class _Data {
        @SerializedName("id")
        public long id;
        @SerializedName("coverImgUrl")
        public String iconUrl;
        @SerializedName("creator")
        public SearchPlayListObj._CreatorData creator;
        @SerializedName("trackCount")
        public int totalMusics;
        @SerializedName("playCount")
        public int totalPlays;
        @SerializedName("subscribedCount")
        public int totalSubscribed;
        @SerializedName("description")
        public String desc;
        @SerializedName("trackIds")
        public List<_TrackIdData> trackIds;
    }

    public static class _TrackIdData {
        @SerializedName("id")
        public long musicId;
        @SerializedName("v")
        public int v;
        @SerializedName("t")
        public int t;
        @SerializedName("at")
        public long at;

        @Override
        public String toString() {
            return "(id=" + musicId + ",v=" + v + ",t=" + t + ")";
        }
    }
}
