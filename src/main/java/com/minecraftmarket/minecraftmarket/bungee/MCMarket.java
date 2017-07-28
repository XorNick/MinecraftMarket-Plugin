package com.minecraftmarket.minecraftmarket.bungee;

import com.minecraftmarket.minecraftmarket.bungee.commands.MMCmd;
import com.minecraftmarket.minecraftmarket.bungee.configs.MainConfig;
import com.minecraftmarket.minecraftmarket.bungee.tasks.PurchasesTask;
import com.minecraftmarket.minecraftmarket.bungee.utils.updater.Updater;
import com.minecraftmarket.minecraftmarket.common.api.MCMApi;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.metrics.BungeeMetrics;
import com.minecraftmarket.minecraftmarket.common.utils.FileUtils;
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

        reloadConfigs(null);

        getProxy().getPluginManager().registerCommand(this, new MMCmd(this));

        new BungeeMetrics(this);
        new Updater(this, 44031, pluginURL -> {
            getLogger().warning(I18n.tl("new_version"));
            getLogger().warning(pluginURL);
        });
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(this);
        i18n.onDisable();
    }

    public void reloadConfigs(Response<Boolean> response) {
        mainConfig = new MainConfig(this);

        i18n.updateLocale(mainConfig.getLang());

        getProxy().getScheduler().cancel(this);

        setKey(mainConfig.getApiKey(), false, result -> {
            if (purchasesTask == null) {
                purchasesTask = new PurchasesTask(MCMarket.this);
            }
            getProxy().getScheduler().schedule(MCMarket.this, purchasesTask, 10, 60 * mainConfig.getCheckInterval(), TimeUnit.SECONDS);

            if (response != null) {
                response.done(result);
            }
        });
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getProxy().getScheduler().runAsync(this, () -> {
            api = new MCMApi(apiKey, mainConfig.isDebug(), MCMApi.ApiType.GSON);
            authenticated = api.getMarketApi().authAPI();
            if (!authenticated) {
                getLogger().warning(I18n.tl("invalid_key", "/MM apiKey <key>"));
            }
            if (response != null) {
                response.done(authenticated);
            }
        });
    }

    public MCMarketApi getApi() {
        return api.getMarketApi();
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
        for (String file : FileUtils.getJarResources(getClass())) {
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