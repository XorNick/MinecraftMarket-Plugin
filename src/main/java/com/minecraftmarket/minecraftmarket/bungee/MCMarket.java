package com.minecraftmarket.minecraftmarket.bungee;

import com.minecraftmarket.minecraftmarket.bungee.Commands.MMCmd;
import com.minecraftmarket.minecraftmarket.bungee.Configs.MainConfig;
import com.minecraftmarket.minecraftmarket.bungee.Task.PurchasesTask;
import com.minecraftmarket.minecraftmarket.bungee.api.MCMApi;
import com.r4g3baby.pluginutils.Bungee.Updater;
import com.r4g3baby.pluginutils.File.FileUtils;
import com.r4g3baby.pluginutils.I18n.I18n;
import com.r4g3baby.pluginutils.Metrics.BungeeMetrics;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public final class MCMarket extends Plugin {
    private I18n i18n;
    private MainConfig mainConfig;
    private MCMApi api;
    private boolean authenticated;
    private PurchasesTask purchasesTask;

    @Override
    public void onEnable() {
        i18n = new I18n(getLanguageFolder(), getLogger());
        i18n.onEnable();

        mainConfig = new MainConfig(this);

        i18n.updateLocale(mainConfig.getLang());

        setKey(mainConfig.getApiKey(), false, null);

        getProxy().getPluginManager().registerCommand(this, new MMCmd(this));

        purchasesTask = new PurchasesTask(this);
        getProxy().getScheduler().schedule(this, purchasesTask, 10, 60 * mainConfig.getCheckInterval(), TimeUnit.SECONDS);

        new BungeeMetrics(this);
        new Updater(this, 29183, pluginURL -> {
            getLogger().warning(I18n.tl("newVersion"));
            getLogger().warning(pluginURL);
        });
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(this);
        i18n.onDisable();
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getProxy().getScheduler().runAsync(this, () -> {
            api = new MCMApi(apiKey, mainConfig.isDebug());
            authenticated = api.authAPI();
            if (!authenticated) {
                getLogger().warning(I18n.tl("invalidKey", "/MM apiKey <key>"));
            }
            if (response != null) {
                response.done(authenticated);
            }
        });
    }

    public MCMApi getApi() {
        return api;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public PurchasesTask getPurchasesTask() {
        return purchasesTask;
    }

    public interface Response<T> {
        void done(T t);
    }

    private File getLanguageFolder() {
        File langFolder = new File(getDataFolder(), "langs");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        for (String file : FileUtils.getJarResources(getClass().getProtectionDomain().getCodeSource())) {
            if (file.startsWith("langs/") && file.endsWith(".properties")) {
                File langFile = new File(getDataFolder(), file);
                if (!langFile.exists()) {
                    try {
                        Files.copy(getResourceAsStream(file), langFile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return langFolder;
    }
}