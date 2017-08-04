package com.minecraftmarket.minecraftmarket.common.metrics;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

public class BukkitMetrics {
    private static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/bukkit";
    private static String serverUUID;
    private static boolean logFailedRequests;
    private final JavaPlugin plugin;


    public BukkitMetrics(JavaPlugin plugin) {
        this.plugin = plugin;

        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.isSet("serverUuid")) {
            config.addDefault("enabled", true);
            config.addDefault("serverUuid", UUID.randomUUID().toString());
            config.addDefault("logFailedRequests", false);
            config.options().header(
                    "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                            "To honor their work, you should not disable it.\n" +
                            "This has nearly no effect on the server performance!\n" +
                            "Check out https://bStats.org/ to learn more :)"
            ).copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException ignored) {
            }
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
                plugin.getServer().getScheduler().runTask(plugin, () -> submitData());
            }
        }, 1000 * 60, 1000 * 60 * 30);
    }

    private void submitData() {
        final JSONObject data = getServerData();

        final JSONArray pluginData = new JSONArray();
        pluginData.add(getPluginData());
        data.put("plugins", pluginData);

        new Thread(() -> {
            try {
                sendData(data);
            } catch (Exception e) {
                if (logFailedRequests) {
                    plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + plugin.getName(), e);
                }
            }
        }).start();
    }

    private JSONObject getServerData() {
        int playerAmount;
        try {
            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            playerAmount = onlinePlayersMethod.getReturnType().equals(Collection.class) ? ((Collection<?>) onlinePlayersMethod.invoke(plugin.getServer())).size() : ((Player[]) onlinePlayersMethod.invoke(plugin.getServer())).length;
        } catch (Exception e) {
            playerAmount = plugin.getServer().getOnlinePlayers().size();
        }
        int onlineMode = plugin.getServer().getOnlineMode() ? 1 : 0;
        String bukkitVersion = plugin.getServer().getVersion();
        bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);

        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JSONObject data = new JSONObject();
        data.put("serverUUID", serverUUID);

        data.put("playerAmount", playerAmount);
        data.put("onlineMode", onlineMode);
        data.put("bukkitVersion", "Bukkit v" + bukkitVersion);

        data.put("javaVersion", javaVersion);
        data.put("osName", osName);
        data.put("osArch", osArch);
        data.put("osVersion", osVersion);
        data.put("coreCount", coreCount);

        return data;
    }

    private JSONObject getPluginData() {
        JSONObject data = new JSONObject();

        String pluginName = plugin.getDescription().getName();
        String pluginVersion = plugin.getDescription().getVersion();

        data.put("pluginName", pluginName);
        data.put("pluginVersion", pluginVersion);
        data.put("customCharts", new JSONArray());

        return data;
    }

    private static void sendData(JSONObject data) throws Exception {
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