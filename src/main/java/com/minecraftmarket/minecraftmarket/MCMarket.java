package com.minecraftmarket.minecraftmarket;

import com.minecraftmarket.minecraftmarket.Api.MCMApi;
import com.minecraftmarket.minecraftmarket.Configs.MessagesConfig;
import com.minecraftmarket.minecraftmarket.Configs.SignsConfig;
import com.minecraftmarket.minecraftmarket.Listeners.SignsListener;
import com.minecraftmarket.minecraftmarket.Task.PurchasesTask;
import com.minecraftmarket.minecraftmarket.Task.SignsTask;
import com.minecraftmarket.minecraftmarket.Commands.MMCmd;
import com.minecraftmarket.minecraftmarket.Configs.MainConfig;
import com.minecraftmarket.minecraftmarket.Inventory.InventoryManager;
import com.minecraftmarket.minecraftmarket.Listeners.ShopCmdListener;
import com.r4g3baby.pluginutils.Inventory.InventoryGUI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class MCMarket extends JavaPlugin {
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;
    private SignsConfig signsConfig;
    private MCMApi api;
    private boolean authenticated;
    private InventoryManager inventoryManager;
    private SignsTask signsTask;

    @Override // TODO implement Metrics() and Updater()
    public void onEnable() {
        mainConfig = new MainConfig(this);
        messagesConfig = new MessagesConfig(this);
        signsConfig = new SignsConfig(this);
        setKey(mainConfig.getApiKey(), false, null);
        if (mainConfig.isUseGUI()) {
            inventoryManager = new InventoryManager(this);
            getServer().getPluginManager().registerEvents(new ShopCmdListener(this), this);
        }
        if (mainConfig.isUseSigns()) {
            signsTask = new SignsTask(this);
            getServer().getScheduler().runTaskTimerAsynchronously(this, signsTask, 20 * 10, 20 * 60 * mainConfig.getCheckInterval());
            getServer().getPluginManager().registerEvents(new SignsListener(this), this);
        }
        getCommand("MinecraftMarket").setExecutor(new MMCmd(this));
        getServer().getPluginManager().registerEvents(InventoryGUI.getListener(), this);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new PurchasesTask(this), 20 * 10, 20 * 60 * mainConfig.getCheckInterval());
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            api = new MCMApi(apiKey, mainConfig.isDebug());
            authenticated = api.authAPI();
            if (!authenticated) {
                getLogger().log(Level.SEVERE, "Invalid APIKey! Check your config or use");
                getLogger().log(Level.SEVERE, "/MM apiKey <key> to setup your key.");
            } else if (inventoryManager != null) {
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

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public SignsConfig getSignsConfig() {
        return signsConfig;
    }

    public MCMApi getApi() {
        return api;
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

    public interface Response<T> {
        void done(T t);
    }
}