package com.minecraftmarket.minecraftmarket.bungee.utils.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {
    private final String VERSION_URL = "https://api.spiget.org/v2/resources/%s/versions?sort=-name";
    private final String SPIGOT_URL = "https://www.spigotmc.org/resources/%s/";

    public Updater(Plugin plugin, int pluginID, UpdateCallback callback) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(String.format(VERSION_URL, pluginID)).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setUseCaches(false);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                JsonArray versionsArray = (JsonArray) new JsonParser().parse(bufferedReader);
                String lastVersion = ((JsonObject) versionsArray.get(0)).get("name").getAsString().replace(".", "");
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