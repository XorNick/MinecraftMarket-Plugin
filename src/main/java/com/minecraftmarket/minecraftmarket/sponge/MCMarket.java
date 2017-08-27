package com.minecraftmarket.minecraftmarket.sponge;

import com.google.inject.Inject;
import com.minecraftmarket.minecraftmarket.common.api.MCMApi;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.metrics.SpongeMetrics;
import com.minecraftmarket.minecraftmarket.common.utils.FileUtils;
import com.minecraftmarket.minecraftmarket.sponge.commands.MainCMD;
import com.minecraftmarket.minecraftmarket.sponge.config.MainConfig;
import com.minecraftmarket.minecraftmarket.sponge.config.SignsConfig;
import com.minecraftmarket.minecraftmarket.sponge.config.SignsLayoutConfig;
import com.minecraftmarket.minecraftmarket.sponge.listeners.SignsListener;
import com.minecraftmarket.minecraftmarket.sponge.tasks.PurchasesTask;
import com.minecraftmarket.minecraftmarket.sponge.tasks.SignsTask;
import com.minecraftmarket.minecraftmarket.sponge.utils.updater.Updater;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "minecraftmarket",
        name = "MinecraftMarket",
        version = "3.4.1",
        description = "The #1 webstore platform for Minecraft servers",
        authors = "R4G3_BABY",
        url = "https://www.minecraftmarket.com"
)
public final class MCMarket {
    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path baseDirectory;

    @Inject
    private SpongeMetrics metrics;

    private I18n i18n;
    private MainConfig mainConfig;
    private SignsConfig signsConfig;
    private SignsLayoutConfig signsLayoutConfig;
    private boolean authenticated;
    private SignsTask signsTask;
    private PurchasesTask purchasesTask;

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent e) {
        i18n = new I18n(getLanguageFolder(), null);
        i18n.onEnable();
    }

    @Listener
    public void onGameStartingServerEvent(GameStartingServerEvent e) {
        reloadConfigs(null);

        CommandSpec mainCMD = CommandSpec.builder()
                .description(Text.of("Manage plugin functionalities"))
                .permission("minecraftmarket.use")
                .executor(new MainCMD(this))
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("arg1"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("arg2")))
                )
                .build();
        Sponge.getCommandManager().register(this, mainCMD, "minecraftmarket", "mm");

        new Updater(Sponge.getPluginManager().fromInstance(this).orElse(null), 44031, pluginURL -> {
            logger.warn(I18n.tl("new_version"));
            logger.warn(pluginURL);
        });
    }

    @Listener
    public void onGameStoppingServerEvent(GameStoppingServerEvent e) {
        Sponge.getEventManager().unregisterPluginListeners(this);
        for (Task task : Sponge.getScheduler().getScheduledTasks(this)) {
            task.cancel();
        }
        i18n.onDisable();
    }

    public void reloadConfigs(Response<Boolean> response) {
        mainConfig = new MainConfig(baseDirectory.toFile());
        signsConfig = new SignsConfig(baseDirectory.toFile());
        signsLayoutConfig = new SignsLayoutConfig(baseDirectory.toFile());

        i18n.updateLocale(mainConfig.getLang());

        Sponge.getEventManager().unregisterPluginListeners(this);
        for (Task task : Sponge.getScheduler().getScheduledTasks(this)) {
            task.cancel();
        }

        setKey(mainConfig.getApiKey(), false, result -> {
            if (mainConfig.isUseSigns()) {
                if (signsTask == null) {
                    signsTask = new SignsTask(MCMarket.this);
                }
                Sponge.getScheduler().createTaskBuilder().delayTicks(20 * 10).intervalTicks(20 * 60 * mainConfig.getCheckInterval()).execute(signsTask).submit(MCMarket.this);
                Sponge.getEventManager().registerListeners(MCMarket.this, new SignsListener(MCMarket.this));
            }

            if (purchasesTask == null) {
                purchasesTask = new PurchasesTask(MCMarket.this);
            }
            Sponge.getScheduler().createTaskBuilder().async().delayTicks(20 * 10).intervalTicks(20 * 60 * mainConfig.getCheckInterval()).execute(purchasesTask).submit(MCMarket.this);

            if (response != null) {
                response.done(result);
            }
        });
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        if (Sponge.isServerAvailable()) {
            Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
                new MCMApi(apiKey, mainConfig.isDebug(), MCMApi.ApiType.GSON, getUserAgent());
                authenticated = getApi().authAPI();
                if (!authenticated) {
                    logger.warn(I18n.tl("invalid_key", "/MM apiKey <key>"));
                }
                if (response != null) {
                    response.done(authenticated);
                }
            }).submit(this);
        } else {
            if (response != null) {
                response.done(false);
            }
        }
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

    private File getLanguageFolder() {
        File langFolder = new File(baseDirectory.toFile(), "langs");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        for (String file : FileUtils.getJarResources(getClass())) {
            if (file.startsWith("langs/") && file.endsWith(".properties")) {
                File langFile = new File(baseDirectory.toFile(), file);
                if (!langFile.exists()) {
                    try {
                        Files.copy(getClass().getClassLoader().getResourceAsStream(file), langFile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return langFolder;
    }

    private String getUserAgent() {
        Optional<PluginContainer> plugin = Sponge.getPluginManager().fromInstance(this);
        return plugin.map(pluginContainer -> pluginContainer.getName() + "-v" + pluginContainer.getVersion() + "-SPONGE").orElse(null);
    }
}