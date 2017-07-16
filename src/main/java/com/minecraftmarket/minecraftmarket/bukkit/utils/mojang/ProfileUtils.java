package com.minecraftmarket.minecraftmarket.bukkit.utils.mojang;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileUtils {
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";
    private static final Cache<String, UUID> uuidCache = new Cache<>();
    private static final Cache<String, Map<String, String>> profileCache = new Cache<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final JSONParser jsonParser = new JSONParser();

    public static void getUniqueId(final String username, Consumer<UUID> action) {
        pool.execute(new Acceptor<UUID>(action) {
            @Override
            public UUID getValue() {
                return getUniqueId(username);
            }
        });
    }

    private static UUID getUniqueId(String username) {
        try {
            if (username == null) return null;
            if (ProfileUtils.uuidCache.contains(username)) return ProfileUtils.uuidCache.get(username);
            HttpURLConnection connection = makeConnection(String.format(UUID_URL, username));
            JSONObject profile = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            String id = (String) profile.get("id");
            UUID uuid = getUUID(id);
            ProfileUtils.uuidCache.put(username, uuid);
            return uuid;
        } catch (Exception | Error ignored) {}
        return null;
    }

    public static void getProfile(final String uuid, Consumer<Map<String, String>> action) {
        pool.execute(new Acceptor<Map<String, String>>(action) {
            @Override
            public Map<String, String> getValue() {
                return getProfile(uuid);
            }
        });
    }

    private static Map<String, String> getProfile(String uuid) {
        try {
            if (uuid == null) return null;
            if (ProfileUtils.profileCache.contains(uuid)) return ProfileUtils.profileCache.get(uuid);
            Map<String, String> response = new HashMap<>();
            HttpURLConnection connection = makeConnection(String.format(PROFILE_URL, uuid.replace("-", "")));
            JSONObject profile = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            JSONObject properties = (JSONObject) ((JSONArray) profile.get("properties")).get(0);
            JSONObject textures = (JSONObject) ((JSONObject) jsonParser.parse(Base64Coder.decodeString((String) properties.get("value")))).get("textures");
            response.put("id", (String) profile.get("id"));
            response.put("name", (String) profile.get("name"));
            JSONObject skin = (JSONObject) textures.get("SKIN");
            response.put("skin", skin != null ? (String) skin.get("url") : null);
            JSONObject cape = (JSONObject) textures.get("CAPE");
            response.put("cape", cape != null ? (String) cape.get("url") : null);
            ProfileUtils.profileCache.put(uuid, response);
            return response;
        } catch (Exception | Error ignored) {}
        return null;
    }

    private static HttpURLConnection makeConnection(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private static UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public interface Consumer<T> {
        void accept(T t);
    }

    public static abstract class Acceptor<T> implements Runnable {
        private final Consumer<T> consumer;

        Acceptor(Consumer<T> consumer) {
            this.consumer = consumer;
        }

        public abstract T getValue();

        @Override
        public void run() {
            consumer.accept(getValue());
        }
    }

    private static class Cache<K, V> {
        private long expireTime = 1000 * 60 * 30;
        private Map<K, CachedEntry<V>> map = new HashMap<>();

        boolean contains(K key) {
            return map.containsKey(key) && get(key) != null;
        }

        V get(K key) {
            CachedEntry<V> entry = map.get(key);
            if (entry == null) return null;
            if (entry.isExpired()) {
                map.remove(key);
                return null;
            } else {
                return entry.getValue();
            }
        }

        void put(K key, V value) {
            map.put(key, new CachedEntry<>(value, expireTime));
        }

        private static class CachedEntry<V> {

            CachedEntry(V value, long expireTime) {
                this.value = new SoftReference<>(value);
                this.expires = expireTime + System.currentTimeMillis();
            }

            private final SoftReference<V> value;
            private final long expires;

            V getValue() {
                if (isExpired()) {
                    return null;
                }
                return value.get();
            }

            boolean isExpired() {
                return value.get() == null || expires != -1 && expires < System.currentTimeMillis();
            }
        }
    }
}