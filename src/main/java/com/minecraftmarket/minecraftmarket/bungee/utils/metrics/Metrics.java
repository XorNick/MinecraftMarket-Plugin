package com.minecraftmarket.minecraftmarket.bungee.utils.metrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

public class Metrics {
    private static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/bungeecord";
    private final Plugin plugin;
    private boolean enabled;
    private String serverUUID;
    private boolean logFailedRequests = false;
    private static final List<Object> knownMetricsInstances = new ArrayList<>();

    public Metrics(Plugin plugin) {
        this.plugin = plugin;

        try {
            loadConfig();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load bStats config!", e);
            return;
        }

        if (!enabled) {
            return;
        }

        Class<?> usedMetricsClass = getFirstBStatsClass();
        if (usedMetricsClass == null) {
            return;
        }
        if (usedMetricsClass == getClass()) {
            linkMetrics(this);
            startSubmitting();
        } else {
            try {
                usedMetricsClass.getMethod("linkMetrics", Object.class).invoke(null, this);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                if (logFailedRequests) {
                    plugin.getLogger().log(Level.WARNING, "Failed to link to first metrics class " + usedMetricsClass.getName() + "!", e);
                }
            }
        }
    }

    private static void linkMetrics(Object metrics) {
        knownMetricsInstances.add(metrics);
    }

    private void startSubmitting() {
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TaskScheduler scheduler = plugin.getProxy().getScheduler();
                scheduler.schedule(plugin, () -> submitData(), 0L, TimeUnit.SECONDS);
            }
        }, 1000 * 60 * 2, 1000 * 60 * 30);
    }

    private JsonObject getServerData() {
        int playerAmount = plugin.getProxy().getOnlineCount();
        playerAmount = playerAmount > 500 ? 500 : playerAmount;
        int onlineMode = plugin.getProxy().getConfig().isOnlineMode() ? 1 : 0;
        String bungeecordVersion = plugin.getProxy().getVersion();
        int managedServers = plugin.getProxy().getServers().size();

        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();

        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("managedServers", managedServers);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("bungeecordVersion", bungeecordVersion);

        data.addProperty("javaVersion", javaVersion);
        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    private void submitData() {
        final JsonObject data = getServerData();

        final JsonArray pluginData = new JsonArray();
        for (Object metrics : knownMetricsInstances) {
            try {
                Object plugin = metrics.getClass().getMethod("getPluginData").invoke(metrics);
                if (plugin instanceof JsonObject) {
                    pluginData.add((JsonObject) plugin);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        data.add("plugins", pluginData);

        new Thread(() -> {
            try {
                sendData(data);
            } catch (Exception e) {
                if (logFailedRequests) {
                    plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats!", e);
                }
            }
        }).start();
    }

    private void loadConfig() throws IOException {
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
    }

    private Class<?> getFirstBStatsClass() {
        Path configPath = plugin.getDataFolder().toPath().getParent().resolve("bStats");
        configPath.toFile().mkdirs();
        File tempFile = new File(configPath.toFile(), "temp.txt");

        try {
            String className = readFile(tempFile);
            if (className != null) {
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException ignored) {
                }
            }
            writeFile(tempFile, getClass().getName());
            return getClass();
        } catch (IOException e) {
            if (logFailedRequests) {
                plugin.getLogger().log(Level.WARNING, "Failed to get first bStats class!", e);
            }
            return null;
        }
    }

    private String readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            return bufferedReader.readLine();
        }
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
}