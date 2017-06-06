package com.minecraftmarket.minecraftmarket.bungee;

import com.minecraftmarket.minecraftmarket.bungee.Commands.MMCmd;
import com.minecraftmarket.minecraftmarket.bungee.Configs.MainConfig;
import com.minecraftmarket.minecraftmarket.bungee.Task.PurchasesTask;
import com.minecraftmarket.minecraftmarket.core.MCMApi;
import com.r4g3baby.pluginutils.Bungee.Updater;
import com.r4g3baby.pluginutils.Metrics.BungeeMetrics;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class MCMarket extends Plugin {
    private MainConfig mainConfig;
    private MCMApi api;
    private boolean authenticated;
    private PurchasesTask purchasesTask;

    @Override
    public void onEnable() {
        mainConfig = new MainConfig(this);

        setKey(mainConfig.getApiKey(), false, null);

        getProxy().getPluginManager().registerCommand(this, new MMCmd(this));

        purchasesTask = new PurchasesTask(this);
        getProxy().getScheduler().schedule(this, purchasesTask, 10, 60 * mainConfig.getCheckInterval(), TimeUnit.SECONDS);

        new BungeeMetrics(this);
        new Updater(this, 29183, pluginURL -> {
            getLogger().log(Level.WARNING, "New version available download at:");
            getLogger().log(Level.WARNING, pluginURL);
        });
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(this);
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getProxy().getScheduler().runAsync(this, () -> {
            api = new MCMApi(apiKey, mainConfig.isDebug());
            authenticated = api.authAPI();
            if (!authenticated) {
                getLogger().log(Level.SEVERE, "Invalid APIKey! Check your config or use");
                getLogger().log(Level.SEVERE, "/MM apiKey <key> to setup your key.");
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
}