package com.minecraftmarket.minecraftmarket.nukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import com.minecraftmarket.minecraftmarket.nukkit.api.MCMApi;
import com.minecraftmarket.minecraftmarket.nukkit.commands.ApiKey;
import com.minecraftmarket.minecraftmarket.nukkit.commands.Check;
import com.minecraftmarket.minecraftmarket.nukkit.commands.Cmd;
import com.minecraftmarket.minecraftmarket.nukkit.commands.Version;
import com.minecraftmarket.minecraftmarket.nukkit.configs.MainConfig;
import com.minecraftmarket.minecraftmarket.nukkit.tasks.PurchasesTask;
import com.r4g3baby.pluginutils.file.FileUtils;
import com.r4g3baby.pluginutils.i18n.I18n;
import com.r4g3baby.pluginutils.nukkit.SpigotUpdater;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MCMarket extends PluginBase {
    private final List<Cmd> subCmds = new ArrayList<>();
    private I18n i18n;
    private MainConfig mainConfig;
    private MCMApi api;
    private boolean authenticated;
    private PurchasesTask purchasesTask;

    @Override
    public void onEnable() {
        i18n = new I18n(getLanguageFolder(), null);
        i18n.onEnable();

        mainConfig = new MainConfig(this);

        i18n.updateLocale(mainConfig.getLang());

        setKey(mainConfig.getApiKey(), false, null);

        subCmds.add(new ApiKey(this));
        subCmds.add(new Check(this));
        subCmds.add(new Version(this));

        purchasesTask = new PurchasesTask(this);
        getServer().getScheduler().scheduleRepeatingTask(this, purchasesTask, 20 * 60 * mainConfig.getCheckInterval(), true);

        new SpigotUpdater(this, 29183, pluginURL -> {
            getLogger().warning(I18n.tl("new_version"));
            getLogger().warning(pluginURL);
        });
    }

    @Override
    public void onDisable() {
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

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {
            @Override
            public void onRun() {
                api = new MCMApi(apiKey, mainConfig.isDebug());
                authenticated = api.authAPI();
                if (!authenticated) {
                    getLogger().warning(I18n.tl("invalid_key", "/MM apiKey <key>"));
                }
                if (response != null) {
                    response.done(authenticated);
                }
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
        for (String file : FileUtils.getJarResources(getClass().getProtectionDomain().getCodeSource())) {
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