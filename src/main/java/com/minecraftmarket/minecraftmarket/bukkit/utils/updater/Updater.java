package com.minecraftmarket.minecraftmarket.bukkit.utils.updater;

import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {
    private final String VERSION_URL = "https://api.spiget.org/v2/resources/%s/versions?sort=-name";
    private final String SPIGOT_URL = "https://www.spigotmc.org/resources/%s/";

    public Updater(JavaPlugin plugin, int pluginID, UpdateCallback callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(String.format(VERSION_URL, pluginID)).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setUseCaches(false);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                JSONArray versionsArray = (JSONArray) JSONValue.parseWithException(bufferedReader);
                String lastVersion = ((String) ((JSONObject) versionsArray.get(0)).get("name")).replace(".", "");
                String currentVersion = plugin.getDescription().getVersion().replace(".", "");
                if (Utils.isInt(currentVersion) && Utils.isInt(lastVersion)) {
                    if (Utils.getInt(currentVersion) < Utils.getInt(lastVersion)) {
                        callback.newVersion(String.format(SPIGOT_URL, pluginID));
                    }
                }
                conn.disconnect();
            } catch (Exception ignored) {}
        });
    }

    public interface UpdateCallback {
        void newVersion(String pluginURL);
    }
}