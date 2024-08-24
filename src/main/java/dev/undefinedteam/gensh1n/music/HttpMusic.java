package dev.undefinedteam.gensh1n.music;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.music.objs.HttpResObj;
import dev.undefinedteam.gensh1n.music.objs.api.EncResObj;
import dev.undefinedteam.gensh1n.music.objs.enums.EncryptType;
import dev.undefinedteam.gensh1n.utils.network.Http;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HttpMusic {
    public static HttpResObj get(String path, String data) {
        try {
            data = URLEncoder.encode(data, StandardCharsets.UTF_8);


            Http.Request request = Http.get(path + data)
                .header("referer", "https://music.163.com")
                .header("content-type", "application/json;charset=UTF-8")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.864.41");

            return request.sendResponse(response -> {
                try {
                    if (response == null) {
                        //AllMusic.log.warning("§d[AllMusic3]§c获取网页错误");
                        return null;
                    }
                    int httpCode = response.getStatusLine().getStatusCode();
                    InputStream inputStream = response.getEntity().getContent();
                    boolean ok = httpCode == 200;
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    String data1 = result.toString(StandardCharsets.UTF_8.name());
                    if (!ok) {
                        //AllMusic.log.warning("§d[AllMusic3]§c服务器返回错误：" + data1);
                    }
                    return new HttpResObj(data1, ok);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });

        } catch (Exception e) {
            //AllMusic.log.warning("§d[AllMusic3]§c获取网页错误");
            e.printStackTrace();
        }
        return null;
    }

    public static HttpResObj post(String url, JsonObject data, EncryptType type, String ourl) {
        try {
            Http.Request http;
            EncResObj res;
            UrlEncodedFormEntity entity;
            List<Cookie> cookies = new ArrayList<>();
            if (GMusic.INSTANCE.cookieObj.cookieStore.containsKey("music.163.com")) {
                cookies = GMusic.INSTANCE.cookieObj.cookieStore.get("music.163.com");
            }
            if (type == EncryptType.WEAPI) {
                String csrfToken = "";
                for (Cookie item : cookies) {
                    if (item.getName().equalsIgnoreCase("__csrf")) {
                        csrfToken = item.getValue();
                    }
                }

                data.addProperty("csrf_token", csrfToken);
                res = HttpMusicCrypto.weapiEncrypt(Client.GSON.toJson(data));
                url = url.replaceFirst("\\w*api", "weapi");
                http = Http.post(url).cookieSpec(CookieSpecs.STANDARD);
                http.header("Referer", "https://music.163.com");
                http.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/13.10586");

                List<NameValuePair> formParams = new ArrayList<>();
                formParams.add(new BasicNameValuePair("params", res.params));
                formParams.add(new BasicNameValuePair("encSecKey", res.encSecKey));
                entity = new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8);
            } else if (type == EncryptType.EAPI) {
                JsonObject header = new JsonObject();
                header.addProperty("appver", "8.10.90");
                header.addProperty("versioncode", "140");
                header.addProperty("buildver", new Date().toString().substring(0, 10));
                header.addProperty("resolution", "1920x1080");
                header.addProperty("os", "android");
                String requestId = "0000" + (new Date() + "_" + Math.floor(Math.random() * 1000));
                header.addProperty("requestId", requestId);
                for (Cookie item : cookies) {
                    if (item.getName().equalsIgnoreCase("MUSIC_U")) {
                        header.addProperty("MUSIC_U", item.getValue());
                    } else if (item.getName().equalsIgnoreCase("MUSIC_A")) {
                        header.addProperty("MUSIC_A", item.getValue());
                    } else if (item.getName().equalsIgnoreCase("channel")) {
                        header.addProperty("channel", item.getValue());
                    } else if (item.getName().equalsIgnoreCase("mobilename")) {
                        header.addProperty("mobilename", item.getValue());
                    } else if (item.getName().equalsIgnoreCase("osver")) {
                        header.addProperty("osver", item.getValue());
                    } else if (item.getName().equalsIgnoreCase("__csrf")) {
                        header.addProperty("__csrf", item.getValue());
                    }
                }

                data.add("header", header);
                res = HttpMusicCrypto.eapi(ourl, data);
                url = url.replaceFirst("\\w*api", "eapi");
                http = Http.post(url).cookieSpec(CookieSpecs.STANDARD);
                http.header("User-Agent", "Mozilla/5.0 (Linux; Android 9; PCT-AL10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.64 HuaweiBrowser/10.0.3.311 Mobile Safari/537.36");
                List<NameValuePair> formParams = new ArrayList<>();
                formParams.add(new BasicNameValuePair("params", res.params));
                entity = new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8);
            } else {
                http = Http.post(url).cookieSpec(CookieSpecs.STANDARD);
                List<NameValuePair> formParams = new ArrayList<>();
                for (Map.Entry<String, JsonElement> item : data.entrySet()) {
                    formParams.add(new BasicNameValuePair(item.getKey(), item.getValue().getAsString()));
                }
                entity = new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8);
            }
            http.bodyForm(entity);
            return http.sendCtxResponse((ctx,response) -> {
                try {
                    if (response == null) {
                        //AllMusic.log.warning("§d[AllMusic3]§c获取网页错误");
                        return null;
                    }

                    try {
                        String host = "music.163.com";
                        CookieStore store1 = ctx.getCookieStore();

                        if (GMusic.INSTANCE.cookieObj.cookieStore.containsKey(host)) {
                            List<Cookie> responseCookies = GMusic.INSTANCE.cookieObj.cookieStore.get(host);
                            for (Cookie item : store1.getCookies()) {
                                for (Cookie item1 : responseCookies) {
                                    if (item.getName().equalsIgnoreCase(item1.getName())) {
                                        responseCookies.remove(item1);
                                        break;
                                    }
                                }
                                responseCookies.add(item);
                            }
                            GMusic.INSTANCE.cookieObj.cookieStore.put(host, responseCookies);
                        } else {
                            GMusic.INSTANCE.cookieObj.cookieStore.put(host, store1.getCookies());
                        }
                        GMusic.INSTANCE.saveCookie();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int httpCode = response.getStatusLine().getStatusCode();
                    InputStream inputStream = response.getEntity().getContent();
                    boolean ok = httpCode == 200;
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    String data1 = result.toString(StandardCharsets.UTF_8);
                    if (!ok) {
                        //AllMusic.log.warning("§d[AllMusic3]§c服务器返回错误：" + data1);
                    }
                    return new HttpResObj(data1, ok);
                } catch (Exception e) {

                }
                return null;
            });
        } catch (Exception e) {
            //AllMusic.log.warning("§d[AllMusic3]§c获取网页错误");
            e.printStackTrace();
        }
        return null;
    }
}
