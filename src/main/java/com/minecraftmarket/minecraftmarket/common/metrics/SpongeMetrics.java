package com.minecraftmarket.minecraftmarket.common.metrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class SpongeMetrics {
    private static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/bukkit";
    private static String serverUUID;
    private static boolean logFailedRequests;
    private PluginContainer plugin;

    @Inject
    public SpongeMetrics(PluginContainer plugin, @ConfigDir(sharedRoot = true) Path configDir) {
        this.plugin = plugin;

        boolean enabled;
        try {
            Path configPath = configDir.resolve("bStats");
            configPath.toFile().mkdirs();
            File configFile = new File(configPath.toFile(), "config.conf");
            HoconConfigurationLoader configurationLoader = HoconConfigurationLoader.builder().setFile(configFile).build();
            CommentedConfigurationNode node;
            if (!configFile.exists()) {
                configFile.createNewFile();
                node = configurationLoader.load();

                node.getNode("enabled").setValue(true);
                node.getNode("serverUuid").setValue(UUID.randomUUID().toString());
                node.getNode("logFailedRequests").setValue(false);
                node.getNode("enabled").setComment(
                        "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                                "To honor their work, you should not disable it.\n" +
                                "This has nearly no effect on the server performance!\n" +
                                "Check out https://bStats.org/ to learn more :)"
                );

                configurationLoader.save(node);
            } else {
                node = configurationLoader.load();
            }

            enabled = node.getNode("enabled").getBoolean(true);
            serverUUID = node.getNode("serverUuid").getString();
            logFailedRequests = node.getNode("logFailedRequests").getBoolean(false);
        } catch (IOException e) {
            plugin.getLogger().warn("Failed to load bStats config!", e);
            return;
        }

        if (enabled) {
            startSubmitting();
        }
    }

    private void startSubmitting() {
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Sponge.getPluginManager().isLoaded(plugin.getId())) {
                    timer.cancel();
                    return;
                }

                Scheduler scheduler = Sponge.getScheduler();
                Task.Builder taskBuilder = scheduler.createTaskBuilder();
                taskBuilder.execute(() -> submitData()).submit(plugin);
            }
        }, 1000 * 60, 1000 * 60 * 30);
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
                plugin.getLogger().warn("Could not submit plugin stats!", e);
            }
        }
    }

    private JsonObject getServerData() {
        int playerAmount = Sponge.getServer().getOnlinePlayers().size();
        int onlineMode = Sponge.getServer().getOnlineMode() ? 1 : 0;
        String spongeVersion = Sponge.getGame().getPlatform().getMinecraftVersion().getName();

        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();
        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("bukkitVersion", "Sponge v" + spongeVersion);

        data.addProperty("javaVersion", javaVersion);
        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    private JsonObject getPluginData() {
        JsonObject data = new JsonObject();

        String pluginName = plugin.getName();
        String pluginVersion = plugin.getVersion().orElse("unknown");

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
}