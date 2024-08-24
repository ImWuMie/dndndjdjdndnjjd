package dev.undefinedteam.gensh1n.music.objs;

import com.google.gson.annotations.SerializedName;
import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieObj {
    @SerializedName("cookies")
    public final Map<String, List<Cookie>> cookieStore = new HashMap<>();
}
