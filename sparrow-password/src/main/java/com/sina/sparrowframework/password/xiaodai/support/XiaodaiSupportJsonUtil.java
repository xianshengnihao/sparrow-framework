package com.sina.sparrowframework.password.xiaodai.support;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.gson.*;

/**
 * Json 转换相关<p>
 * 我们默认使用com.google.gson来做为我们业务处理的json工具类
 *
 * @author Ge Hui
 */
public final class XiaodaiSupportJsonUtil {

    private static final char DOT = '.';

    private static final String LENGTH = "length";

    private static final char LF = '[';

    private static final String TRUE = "true";

    private static final String FALSE = "false";

    private XiaodaiSupportJsonUtil() {
    }

    private static final Gson DEFAULT_GSON = new GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create();

    private static final Gson LOWER_CASE_WITH_UNDERSCORES_GSON = new GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static final Gson GSON_DISABLE_HTML_ESCAPING = new GsonBuilder().disableHtmlEscaping().create();

    private static final Gson SAFETY_INFO_LOG_GSON = new GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            // 过滤安全敏感字段
            .setExclusionStrategies(new ExclusionStrategy() {

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }

                @Override
                public boolean shouldSkipField(FieldAttributes f) {
//                    return f.getAnnotation(SafetyField.class) != null;
                    return false;
                }
            })
            .create();

    public static Gson defaultGson() {
        return DEFAULT_GSON;
    }

    private static final JsonParser DEFAULT_JSON_PARSER = new JsonParser();

    public static JsonParser defaultJsonParser() {
        return DEFAULT_JSON_PARSER;
    }

    /**
     * jsonPath:
     * .user.name
     * [0].item_id
     * .ref_user[0].user_id
     */
    private static JsonElement tryGetElement(JsonElement element, String fullPath, String path) {
        if (path.length() <= 0) {
            return element;
        } else if (path.charAt(0) == DOT) {
            int idx = path.length();
            final int dotIdx = path.indexOf('.', 1);
            if (dotIdx >= 0 && dotIdx < idx) {
                idx = dotIdx;
            }
            final int leftBracketIdx = path.indexOf('[', 1);
            if (leftBracketIdx >= 0 && leftBracketIdx < idx) {
                idx = leftBracketIdx;
            }

            String fieldName = path.substring(1, idx);
            if (fieldName.isEmpty()) {
                throw new IllegalArgumentException("invalid json path : '" + fullPath + "'");
            }

            if (LENGTH.equals(fieldName) && element.isJsonArray()) {
                return new JsonPrimitive(element.getAsJsonArray().size());
            }

            if (!element.isJsonObject()) {
                return null;
            }

            JsonElement e = element.getAsJsonObject().get(fieldName);
            if (e == null) {
                return null;
            }

            return tryGetElement(e, fullPath, path.substring(idx));
        } else if (path.charAt(0) == LF) {
            int rightBracketIdx = path.indexOf(']', 1);
            if (rightBracketIdx < 0) {
                throw new IllegalArgumentException("invalid json path : '" + fullPath + "'");
            }

            Integer index = Ints.tryParse(path.substring(1, rightBracketIdx));
            if (index == null || index < 0) {
                throw new IllegalArgumentException("invalid json path : '" + fullPath + "'");
            }

            if (!element.isJsonArray()) {
                return null;
            }

            JsonArray array = element.getAsJsonArray();
            JsonElement e = index < array.size() ? array.get(index) : null;

            if (e == null) {
                return null;
            }

            return tryGetElement(e, fullPath, path.substring(rightBracketIdx + 1));
        } else {
            throw new IllegalArgumentException("invalid json path : '" + path + "'");
        }
    }

    public static JsonElement tryGetElement(JsonElement element, String path) {
        return tryGetElement(element, path, path);
    }

    public static JsonObject tryGetObject(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonObject()) {
            return null;
        }
        return e.getAsJsonObject();
    }

    public static JsonArray tryGetArray(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonArray()) {
            return null;
        }
        return e.getAsJsonArray();
    }

    public static String tryGetString(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonPrimitive()) {
            return null;
        }
        return e.getAsString();
    }

    public static String getString(JsonElement element, String path, @Nullable String defaultValue) {
        return nullToValue(tryGetString(element, path), defaultValue);
    }

    public static Long tryGetLong(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonPrimitive()) {
            return null;
        }

        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isNumber()) {
            return p.getAsLong();
        } else {
            return Longs.tryParse(p.getAsString());
        }
    }

    public static Long getLong(JsonElement element, String path, @Nullable Long defaultValue) {
        return nullToValue(tryGetLong(element, path), defaultValue);
    }

    public static Integer tryGetInt(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonPrimitive()) {
            return null;
        }

        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isNumber()) {
            return p.getAsInt();
        } else {
            return Ints.tryParse(p.getAsString());
        }
    }

    public static Integer getInt(JsonElement element, String path, @Nullable Integer defaultValue) {
        return nullToValue(tryGetInt(element, path), defaultValue);
    }

    public static Double tryGetDouble(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonPrimitive()) {
            return null;
        }

        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isNumber()) {
            return p.getAsDouble();
        } else {
            return Doubles.tryParse(p.getAsString());
        }
    }

    public static Double getDouble(JsonElement element, String path, @Nullable Double defaultValue) {
        return nullToValue(tryGetDouble(element, path), defaultValue);
    }

    public static Float tryGetFloat(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonPrimitive()) {
            return null;
        }

        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isNumber()) {
            return p.getAsFloat();
        } else {
            return Floats.tryParse(p.getAsString());
        }
    }

    public static Float getFloat(JsonElement element, String path, @Nullable Float defaultValue) {
        return nullToValue(tryGetFloat(element, path), defaultValue);
    }

    public static Boolean tryGetBoolean(JsonElement element, String path) {
        JsonElement e = tryGetElement(element, path);
        if (e == null || !e.isJsonPrimitive()) {
            return null;
        }

        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isBoolean()) {
            return p.getAsBoolean();
        } else {
            String str = p.getAsString();
            if (TRUE.equalsIgnoreCase(str)) {
                return true;
            } else if (FALSE.equalsIgnoreCase(str)) {
                return false;
            } else {
                return null;
            }
        }
    }

    public static Boolean getBoolean(JsonElement element, String path, @Nullable Boolean defaultValue) {
        return nullToValue(tryGetBoolean(element, path), defaultValue);
    }

    public static <T> T fromJson(String json, Class<T> classOft) throws JsonSyntaxException {
        return DEFAULT_GSON.fromJson(json, classOft);
    }

    public static <T> T fromJson(String json, Type typeOft) throws JsonSyntaxException {
        return DEFAULT_GSON.fromJson(json, typeOft);
    }

    public static <T> T fromJsonWithUnderscoreToCamel(String json, Class<T> classOft) throws JsonSyntaxException {
        return LOWER_CASE_WITH_UNDERSCORES_GSON.fromJson(json, classOft);
    }

    public static <T> T fromJsonWithUnderscoreToCamel(String json, Type typeOft) throws JsonSyntaxException {
        return LOWER_CASE_WITH_UNDERSCORES_GSON.fromJson(json, typeOft);
    }

    /**
     * 获得打印日志用的json字符串
     *
     * @param obj obj
     * @return str
     */
    public static String toJsonForSafetyFiled(Object obj) {
        return SAFETY_INFO_LOG_GSON.toJson(obj);
    }

    private static <T> T nullToValue(T value, T def) {
        return value == null ? def : value;
    }

    public static String toJsonDisableHtmlEscaping(Object obj) {
        return GSON_DISABLE_HTML_ESCAPING.toJson(obj);
    }
}
