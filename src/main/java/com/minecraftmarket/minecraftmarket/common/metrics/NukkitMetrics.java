package com.minecraftmarket.minecraftmarket.common.metrics;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.LogLevel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class NukkitMetrics {
    private static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/bukkit";
    private static String serverUUID;
    private static boolean logFailedRequests;
    private final PluginBase plugin;


    public NukkitMetrics(PluginBase plugin) {
        this.plugin = plugin;

        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        Config config = new Config(configFile, Config.YAML);

        if (!config.exists("serverUuid")) {
            config.set("enabled", true);
            config.set("serverUuid", UUID.randomUUID().toString());
            config.set("logFailedRequests", false);
            config.save(configFile);
        }

        serverUUID = config.getString("serverUuid");
        logFailedRequests = config.getBoolean("logFailedRequests", false);
        if (config.getBoolean("enabled", true)) {
            startSubmitting();
        }
    }

    private void startSubmitting() {
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    timer.cancel();
                    return;
                }
                plugin.getServer().getScheduler().scheduleTask(plugin, () -> submitData());
            }
        }, 1000 * 60, 1000 * 60 * 30);
    }

    private void submitData() {
        final JsonObject data = getServerData();

        final JsonArray pluginData = new JsonArray();
        pluginData.add(getPluginData());
        data.add("plugins", pluginData);

        new Thread(() -> {
            try {
                sendData(data);
            } catch (Exception e) {
                if (logFailedRequests) {
                    plugin.getLogger().log(LogLevel.WARNING, "Could not submit plugin stats of " + plugin.getName(), e);
                }
            }
        }).start();
    }

    private JsonObject getServerData() {
        int playerAmount = plugin.getServer().getOnlinePlayers().size();
        int onlineMode = 1;
        String nukkitVersion = plugin.getServer().getVersion();

        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();
        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("bukkitVersion", "Nukkit " + nukkitVersion);

        data.addProperty("javaVersion", javaVersion);
        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    private JsonObject getPluginData() {
        JsonObject data = new JsonObject();

        String pluginName = plugin.getDescription().getName();
        String pluginVersion = plugin.getDescription().getVersion();

        data.addProperty("pluginName", pluginName);
        data.addProperty("pluginVersion", pluginVersion);
        data.add("customCharts", new JsonArray());

        return data;
    }

    private static void sendData(JsonObject data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null!");
        }

        HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

        byte[] compressedData = compress(data.toString());

        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(compressedData);
        outputStream.flush();
        outputStream.close();

        connection.getInputStream().close();
    }

    private static byte[] compress(final String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return outputStream.toByteArray();
    }
}