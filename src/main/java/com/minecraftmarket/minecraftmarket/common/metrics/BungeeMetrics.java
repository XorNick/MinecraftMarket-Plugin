package com.minecraftmarket.minecraftmarket.common.metrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

public class BungeeMetrics {
    private static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/bukkit";
    private static String serverUUID;
    private static boolean logFailedRequests;
    private final Plugin plugin;

    public BungeeMetrics(Plugin plugin) {
        this.plugin = plugin;

        boolean enabled;
        try {
            Path configPath = plugin.getDataFolder().toPath().getParent().resolve("bStats");
            configPath.toFile().mkdirs();
            File configFile = new File(configPath.toFile(), "config.yml");
            if (!configFile.exists()) {
                writeFile(configFile,
                        "#bStats collects some data for plugin authors like how many servers are using their plugins.",
                        "#To honor their work, you should not disable it.",
                        "#This has nearly no effect on the server performance!",
                        "#Check out https://bStats.org/ to learn more :)",
                        "enabled: true",
                        "serverUuid: \"" + UUID.randomUUID().toString() + "\"",
                        "logFailedRequests: false");
            }

            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            enabled = configuration.getBoolean("enabled", true);
            serverUUID = configuration.getString("serverUuid");
            logFailedRequests = configuration.getBoolean("logFailedRequests", false);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load bStats config!", e);
            return;
        }

        if (enabled) {
            startSubmitting();
        }
    }

    private void startSubmitting() {
        plugin.getProxy().getScheduler().schedule(plugin, this::submitData, 1, 30, TimeUnit.MINUTES);
    }

    private void submitData() {
        final JsonObject data = getServerData();

        final JsonArray pluginData = new JsonArray();
        pluginData.add(getPluginData());
        data.add("plugins", pluginData);

        try {
            sendData(data);
        } catch (Exception e) {
            if (logFailedRequests) {
                plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats!", e);
            }
        }
    }

    private JsonObject getServerData() {
        int playerAmount = plugin.getProxy().getOnlineCount();
        int onlineMode = plugin.getProxy().getConfig().isOnlineMode() ? 1 : 0;
        String bungeecordVersion = plugin.getProxy().getVersion();
        if (bungeecordVersion.contains(":")) {
            String[] split = bungeecordVersion.split(":");
            if (split.length == 5) {
                bungeecordVersion = split[2].split("-")[0];
            }
        }

        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();
        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("bukkitVersion", "Bungee v" + bungeecordVersion);

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
            throw new IllegalArgumentException("Data cannot be null");
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

    private void writeFile(File file, String... lines) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        try (
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }
}