package dev.undefinedteam.gensh1n.music;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.music.objs.*;
import dev.undefinedteam.gensh1n.music.objs.api.music.info.InfoObj;
import dev.undefinedteam.gensh1n.music.objs.api.music.list.PlayListDetailObj;
import dev.undefinedteam.gensh1n.music.objs.api.music.lyric.WLyricObj;
import dev.undefinedteam.gensh1n.music.objs.api.music.search.*;
import dev.undefinedteam.gensh1n.music.objs.api.music.trialinfo.TrialInfoObj;
import dev.undefinedteam.gensh1n.music.objs.api.program.info.PrInfoObj;
import dev.undefinedteam.gensh1n.music.objs.enums.*;
import dev.undefinedteam.gensh1n.music.objs.music.*;

import java.util.ArrayList;
import java.util.List;

import static dev.undefinedteam.gensh1n.Client.GSON;

public class MusicApi {

    /**
     * 获取音乐详情
     *
     * @param id 音乐ID
     * @return 结果
     */
    private SongInfoObj getMusicDetail(String id) {
        JsonObject params = new JsonObject();
        params.addProperty("c", "[{\"id\":" + id + "}]");

        HttpResObj res = HttpMusic.post("https://music.163.com/api/v3/song/detail", params, EncryptType.WEAPI, null);
        if (res != null && res.ok) {
            InfoObj temp = GSON.fromJson(res.data, InfoObj.class);
            if (temp.isOk()) {
                params = new JsonObject();
                params.addProperty("ids", "[" + id + "]");
                params.addProperty("br", "320000");
                res = HttpMusic.post("https://music.163.com/weapi/song/enhance/player/url", params, EncryptType.WEAPI, null);
                if (res == null || !res.ok) {
                    //AllMusic.log.warning("§d[AllMusic3]§c版权检索失败");
                    return null;
                }
                TrialInfoObj obj = GSON.fromJson(res.data, TrialInfoObj.class);
                return new SongInfoObj(temp.getAuthor(), temp.getName(),
                    id, temp.getAlia(), temp.getAl(), temp.getLength(),
                    temp.getPicUrl(), obj.isTrial(), obj.getFreeTrialInfo());
            }
        }
        return null;
    }

    public PlayListDetailObj getPlayListDetail(long id) {
        JsonObject params = new JsonObject();
        params.addProperty("id", id);

        HttpResObj res = HttpMusic.post("https://music.163.com/weapi/v6/playlist/detail", params, EncryptType.WEAPI, null);
        if (res != null && res.ok) {
            return GSON.fromJson(res.data,PlayListDetailObj.class);
            //return res.data;
        }
        return null;
    }

    /**
     * 获取音乐数据
     *
     * @param id 音乐ID
     * @return 结果
     */
    public SongInfoObj getMusic(String id) {
        SongInfoObj info = getMusicDetail(id);
        if (info != null)
            return info;
        JsonObject params = new JsonObject();
        params.addProperty("id", id);
        HttpResObj res = HttpMusic.post("https://music.163.com/api/dj/program/detail", params, EncryptType.WEAPI, null);
        if (res != null && res.ok) {
            PrInfoObj temp = GSON.fromJson(res.data, PrInfoObj.class);
            if (temp.isOk()) {
                return new SongInfoObj(temp.getAuthor(), temp.getName(),
                    temp.getId(), temp.getAlia(), "电台", temp.getLength(),
                    null, false, null);
            } else {
                //AllMusic.log.warning("§d[AllMusic3]§c歌曲信息获取为空");
            }
        }
        return info;
    }

    /**
     * 获取播放链接
     *
     * @param id 音乐ID
     * @return 结果
     */
    public String getPlayUrl(String id) {
        JsonObject params = new JsonObject();
        params.addProperty("ids", "[" + id + "]");
        params.addProperty("br", 32000);
        HttpResObj res = HttpMusic.post("https://music.163.com/weapi/song/enhance/player/url", params, EncryptType.WEAPI, null);
        if (res != null && res.ok) {
            try {
                TrialInfoObj obj = GSON.fromJson(res.data, TrialInfoObj.class);
                return obj.getUrl();
            } catch (Exception e) {
                //Logs.logWrite(res.data);
                //AllMusic.log.warning("§d[AllMusic3]§c播放连接解析错误");
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取歌词
     *
     * @param id 歌曲id
     * @return 结果
     */
    public LyricSave getLyric(String id) {
        LyricSave lyric = new LyricSave();
        JsonObject params = new JsonObject();
        params.addProperty("id", id);
        params.addProperty("cp", false);
        params.addProperty("tv", 0);
        params.addProperty("lv", 0);
        params.addProperty("rv", 0);
        params.addProperty("kv", 0);
        params.addProperty("yv", 0);
        params.addProperty("ytv", 0);
        params.addProperty("rtv", 0);
        HttpResObj res = HttpMusic.post("https://interface3.music.163.com/eapi/song/lyric/v1",
            params, EncryptType.EAPI, "/api/song/lyric/v1");
        if (res != null && res.ok) {
            try {
                WLyricObj obj = GSON.fromJson(res.data, WLyricObj.class);
                LyricDo temp = new LyricDo();
                for (int times = 0; times < 3; times++) {
                    if (temp.check(obj)) {
                        //AllMusic.log.warning("§d[AllMusic3]§c歌词解析错误，正在进行第" + times + "重试");
                    } else {
                        if (temp.isHave) {
                            lyric.setHaveLyric(true);
                            lyric.setLyric(temp.getTemp());
                            if (temp.isHaveK) {
                                lyric.setKlyric(temp.getKLyric());
                            }
                        }
                        return lyric;
                    }
                    Thread.sleep(1000);
                }
                //AllMusic.log.warning("§d[AllMusic3]§c歌词解析失败");
            } catch (Exception e) {
                //AllMusic.log.warning("§d[AllMusic3]§c歌词解析错误");
                e.printStackTrace();
            }
        }
        return lyric;
    }

    public SearchPlayListObj searchPlayList(String... name) {
        return searchPlayList(30, name);
    }

    /**
     * 搜歌
     *
     * @param name 关键字
     * @return 结果
     */
    public SearchPlayListObj searchPlayList(int limit, String... name) {
        StringBuilder name1 = new StringBuilder();
        for (int a = 0; a < name.length; a++) {
            name1.append(name[a]).append(" ");
        }
        String MusicName = name1.toString();
        MusicName = MusicName.substring(0, MusicName.length() - 1);

        JsonObject params = new JsonObject();
        params.addProperty("s", MusicName);
        params.addProperty("type", 1000);
        params.addProperty("limit", limit);
        params.addProperty("offset", 0);

        HttpResObj res = HttpMusic.post("https://music.163.com/weapi/search/get", params, EncryptType.WEAPI, null);
        if (res != null && res.ok) {
            return GSON.fromJson(res.data, SearchPlayListObj.class);
        }
        return null;
    }

    public SearchPageObj searchSingle(String... name) {
        return searchSingle(30, name);
    }

    /**
     * 搜歌
     *
     * @param name 关键字
     * @return 结果
     */
    public SearchPageObj searchSingle(int limit, String... name) {
        List<SearchMusicObj> resData = new ArrayList<>();
        int maxpage;

        StringBuilder name1 = new StringBuilder();
        for (int a = 0; a < name.length; a++) {
            name1.append(name[a]).append(" ");
        }
        String MusicName = name1.toString();
        MusicName = MusicName.substring(0, MusicName.length() - 1);

        JsonObject params = new JsonObject();
        params.addProperty("s", MusicName);
        params.addProperty("type", 1);
        params.addProperty("limit", limit);
        params.addProperty("offset", 0);

        HttpResObj res = HttpMusic.post("https://music.163.com/weapi/search/get", params, EncryptType.WEAPI, null);
        if (res != null && res.ok) {
            SearchDataObj obj = GSON.fromJson(res.data, SearchDataObj.class);
            if (obj != null && obj.isOk()) {
                List<songs> res1 = obj.getResult();
                SearchMusicObj item;
                for (songs temp : res1) {
                    item = new SearchMusicObj(String.valueOf(temp.getId()), temp.getName(),
                        temp.getArtists(), temp.getAlbum());
                    resData.add(item);
                }
                maxpage = res1.size() / 10;
                return new SearchPageObj(resData, maxpage);
            } else {
                //AllMusic.log.warning("§d[AllMusic3]§c歌曲搜索出现错误");
            }
        }
        return null;
    }
}
