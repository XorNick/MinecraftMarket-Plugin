package com.minecraftmarket.minecraftmarket.bukkit;

import com.minecraftmarket.minecraftmarket.bukkit.commands.MMCmd;
import com.minecraftmarket.minecraftmarket.bukkit.configs.LayoutsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.configs.MainConfig;
import com.minecraftmarket.minecraftmarket.bukkit.configs.SignsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.inventory.InventoryManager;
import com.minecraftmarket.minecraftmarket.bukkit.listeners.ShopCmdListener;
import com.minecraftmarket.minecraftmarket.bukkit.listeners.SignsListener;
import com.minecraftmarket.minecraftmarket.bukkit.tasks.PurchasesTask;
import com.minecraftmarket.minecraftmarket.bukkit.tasks.SignsTask;
import com.minecraftmarket.minecraftmarket.bukkit.utils.inventories.InventoryGUI;
import com.minecraftmarket.minecraftmarket.bukkit.utils.updater.Updater;
import com.minecraftmarket.minecraftmarket.common.api.MCMApi;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.metrics.BukkitMetrics;
import com.minecraftmarket.minecraftmarket.common.utils.FileUtils;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MCMarket extends JavaPlugin {
    private I18n i18n;
    private MainConfig mainConfig;
    private LayoutsConfig layoutsConfig;
    private SignsConfig signsConfig;
    private MCMApi api;
    private boolean authenticated;
    private InventoryManager inventoryManager;
    private SignsTask signsTask;
    private PurchasesTask purchasesTask;

    @Override
    public void onEnable() {
        i18n = new I18n(getLanguageFolder(), getLogger());
        i18n.onEnable();

        reloadConfigs(null);

        getCommand("MinecraftMarket").setExecutor(new MMCmd(this));

        new BukkitMetrics(this);
        new Updater(this, 44031, pluginURL -> {
            getLogger().warning(I18n.tl("new_version"));
            getLogger().warning(pluginURL);
        });
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        i18n.onDisable();
    }

    public void reloadConfigs(Response<Boolean> response) {
        mainConfig = new MainConfig(this);
        layoutsConfig = new LayoutsConfig(this);
        signsConfig = new SignsConfig(this);

        i18n.updateLocale(mainConfig.getLang());

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);

        setKey(mainConfig.getApiKey(), false, response);

        if (mainConfig.isUseGUI()) {
            if (inventoryManager == null) {
                inventoryManager = new InventoryManager(this);
            }
            getServer().getPluginManager().registerEvents(new ShopCmdListener(this), this);
            getServer().getPluginManager().registerEvents(InventoryGUI.getListener(), this);
        }

        if (mainConfig.isUseSigns()) {
            if (signsTask == null) {
                signsTask = new SignsTask(this);
            }
            getServer().getScheduler().runTaskTimerAsynchronously(this, signsTask, 20 * 10, 20 * 60 * mainConfig.getCheckInterval());
            getServer().getPluginManager().registerEvents(new SignsListener(this), this);
        }

        if (purchasesTask == null) {
            purchasesTask = new PurchasesTask(this);
        }
        getServer().getScheduler().runTaskTimerAsynchronously(this, purchasesTask, 20 * 10, 20 * 60 * mainConfig.getCheckInterval());
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            api = new MCMApi(apiKey, mainConfig.isDebug(), MCMApi.ApiType.JSON);
            authenticated = api.getMarketApi().authAPI();
            if (!authenticated) {
                getLogger().warning(I18n.tl("invalid_key", "/MM apiKey <key>"));
            }
            if (inventoryManager != null) {
                inventoryManager.load();
            }
            if (response != null) {
                response.done(authenticated);
            }
        });
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public LayoutsConfig getLayoutsConfig() {
        return layoutsConfig;
    }

    public SignsConfig getSignsConfig() {
        return signsConfig;
    }

    public MCMarketApi getApi() {
        return api.getMarketApi();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public SignsTask getSignsTask() {
        return signsTask;
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
                    saveResource(file, false);
                }
            }
        }
        return langFolder;
    }
}