package dev.undefinedteam.gensh1n.utils.json;

import com.google.gson.*;

public class GsonUtils {
    private static final Gson GSON = newBuilderNoPretty().create();

    public static GsonBuilder custom() {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                if (fieldAttributes.getAnnotation(GsonIgnore.class) != null) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                if (aClass.getAnnotation(GsonIgnore.class) != null) {
                    return true;
                }
                return false;
            }
        });
    }

    public static GsonBuilder newBuilder() {
        return custom().setPrettyPrinting();
    }

    public static GsonBuilder newBuilderNoPretty() {
        return custom();
    }

    public static String beanToJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> objClass) {
        return GSON.fromJson(jsonStr, objClass);
    }

    public static String jsonFormatter(String uglyJsonStr) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJsonStr);
        return gson.toJson(je);
    }
}
