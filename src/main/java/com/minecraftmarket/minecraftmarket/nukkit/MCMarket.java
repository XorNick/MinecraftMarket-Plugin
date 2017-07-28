package com.minecraftmarket.minecraftmarket.nukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.HandlerList;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import com.minecraftmarket.minecraftmarket.common.api.MCMApi;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.metrics.NukkitMetrics;
import com.minecraftmarket.minecraftmarket.common.utils.FileUtils;
import com.minecraftmarket.minecraftmarket.nukkit.commands.*;
import com.minecraftmarket.minecraftmarket.nukkit.configs.MainConfig;
import com.minecraftmarket.minecraftmarket.nukkit.configs.SignsConfig;
import com.minecraftmarket.minecraftmarket.nukkit.configs.SignsLayoutConfig;
import com.minecraftmarket.minecraftmarket.nukkit.listeners.SignsListener;
import com.minecraftmarket.minecraftmarket.nukkit.tasks.PurchasesTask;
import com.minecraftmarket.minecraftmarket.nukkit.tasks.SignsTask;
import com.minecraftmarket.minecraftmarket.nukkit.utils.updater.Updater;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MCMarket extends PluginBase {
    private final List<Cmd> subCmds = new ArrayList<>();
    private I18n i18n;
    private MainConfig mainConfig;
    private SignsConfig signsConfig;
    private SignsLayoutConfig signsLayoutConfig;
    private boolean authenticated;
    private SignsTask signsTask;
    private PurchasesTask purchasesTask;

    @Override
    public void onEnable() {
        i18n = new I18n(getLanguageFolder(), null);
        i18n.onEnable();

        reloadConfigs(null);

        subCmds.add(new ApiKey(this));
        subCmds.add(new Check(this));
        subCmds.add(new UpdateSigns(this));
        subCmds.add(new Reload(this));
        subCmds.add(new Version(this));

        new NukkitMetrics(this);
        new Updater(this, 44031, pluginURL -> {
            getLogger().warning(I18n.tl("new_version"));
            getLogger().warning(pluginURL);
        });
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTask(this);
        i18n.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String cmd : getDescription().getCommands().keySet()) {
            if (command.getName().equalsIgnoreCase(cmd)) {
                if (args.length > 0) {
                    List<String> subCmdArgs = new ArrayList<>(Arrays.asList(args));
                    subCmdArgs.remove(0);
                    for (Cmd subCmd : subCmds) {
                        if (subCmd.getCommand().equalsIgnoreCase(args[0])) {
                            subCmd.run(sender, subCmdArgs.toArray(new String[subCmdArgs.size()]));
                            return true;
                        }
                    }
                    sendHelp(sender);
                } else {
                    sendHelp(sender);
                }
                return true;
            }
        }
        return false;
    }

    public void reloadConfigs(Response<Boolean> response) {
        mainConfig = new MainConfig(this);
        signsConfig = new SignsConfig(this);
        signsLayoutConfig = new SignsLayoutConfig(this);

        i18n.updateLocale(mainConfig.getLang());

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTask(this);

        setKey(mainConfig.getApiKey(), false, result -> {
            if (mainConfig.isUseSigns()) {
                if (signsTask == null) {
                    signsTask = new SignsTask(MCMarket.this);
                }
                getServer().getScheduler().scheduleDelayedRepeatingTask(MCMarket.this, signsTask, 20 * 10, 20 * 60 * mainConfig.getCheckInterval());
                getServer().getPluginManager().registerEvents(new SignsListener(MCMarket.this), MCMarket.this);
            }

            if (purchasesTask == null) {
                purchasesTask = new PurchasesTask(MCMarket.this);
            }
            getServer().getScheduler().scheduleRepeatingTask(MCMarket.this, purchasesTask, 20 * 60 * mainConfig.getCheckInterval(), true);

            if (response != null) {
                response.done(result);
            }
        });
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {
            @Override
            public void onRun() {
                new MCMApi(apiKey, mainConfig.isDebug(), MCMApi.ApiType.GSON);
                authenticated = getApi().authAPI();
                if (!authenticated) {
                    getLogger().warning(I18n.tl("invalid_key", "/MM apiKey <key>"));
                }
                if (response != null) {
                    response.done(authenticated);
                }
            }
        });
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public SignsConfig getSignsConfig() {
        return signsConfig;
    }

    public SignsLayoutConfig getSignsLayoutConfig() {
        return signsLayoutConfig;
    }

    public MCMarketApi getApi() {
        return MCMApi.getMarketApi();
    }

    public boolean isAuthenticated() {
        return authenticated;
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

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(TextFormat.GRAY.toString() + TextFormat.STRIKETHROUGH + "============= " + TextFormat.YELLOW + "MinecraftMarket Help " + TextFormat.GRAY.toString() + TextFormat.STRIKETHROUGH + "=============");
        for (Cmd subCmd : subCmds) {
            if (subCmd.getArgs().isEmpty()) {
                sender.sendMessage(TextFormat.GOLD + "/MM " + subCmd.getCommand() + TextFormat.DARK_GRAY + " - " + TextFormat.GRAY + subCmd.getDescription());
            } else {
                sender.sendMessage(TextFormat.GOLD + "/MM " + subCmd.getCommand() + " " + subCmd.getArgs() + TextFormat.DARK_GRAY + " - " + TextFormat.GRAY + subCmd.getDescription());
            }
        }
        sender.sendMessage(TextFormat.GRAY.toString() + TextFormat.STRIKETHROUGH + "===============================================");
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