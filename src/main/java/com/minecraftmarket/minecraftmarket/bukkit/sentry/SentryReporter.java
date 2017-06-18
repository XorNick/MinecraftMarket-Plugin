package com.minecraftmarket.minecraftmarket.bukkit.sentry;

import com.getsentry.raven.Raven;
import com.getsentry.raven.RavenFactory;
import com.getsentry.raven.dsn.InvalidDsnException;
import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.r4g3baby.pluginutils.bukkit.Utils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

public class SentryReporter {
    private final MCMarket plugin;
    private Raven raven;
    private BukkitSentryAppender appender;

    /**
     * Constructor
     * Collection is already enabled here instead of onEnable to also capture
     * loading bugs in other plugins (like missing dependencies)
     */
    public SentryReporter(MCMarket plugin) {
        this.plugin = plugin;
        // Add factory, normally done automatically but we relocated the Sentry classes
        RavenFactory.registerFactory(new BukkitRavenFactory());
    }

    /**
     * Should be called at onDisable() to stop error collection properly
     */
    public void stop() {
        // Remove and shutdown appender to prevent double appenders because of disable/enable
        Logger logger = (Logger) LogManager.getRootLogger();
        if (appender != null) {
            logger.removeAppender(appender);
            appender.stop();
        }
    }

    /**
     * Start collecting events
     */
    public void start() {
        try {
            raven = RavenFactory.ravenInstance("http://05658170ebc24339b815641b8451b96a:fe7a6725a43548479538496d228d69d7@sentry.buckingham.io/8");
        } catch (InvalidDsnException | IllegalArgumentException e) {
            plugin.getLogger().info("Failed to setup automatic error collection: " + ExceptionUtils.getStackTrace(e));
            return;
        }

        Logger logger = (Logger) LogManager.getRootLogger();

        // Start collecting errors from the Logger
        appender = new BukkitSentryAppender(raven);

        // Filters
        appender.addFilter(new EventFilter());

        // Default data
        appender.setServerName(getServerName());
        appender.setRelease(getRelease());

        // Start the collector
        appender.start();
        logger.addAppender(appender);
    }

    /**
     * Get the server name that should be used
     *
     * @return The server name
     */
    public String getServerName() {
        String serverName = plugin.getServer().getServerName();

        // Server name can never be null/empty, this will cause Raven to lookup the hostname and kills the server somehow
        if (Utils.isNullOrEmpty(serverName)) {
            serverName = "Unknown";
        }
        return serverName;
    }

    /**
     * Get the release to use
     *
     * @return Version of the MinecraftMarket plugin
     */
    public String getRelease() {
        return plugin.getDescription().getVersion();
    }

    /**
     * Get the Raven instance, for example for adding extra BuilderHelpers
     *
     * @return The used Raven instance
     */
    public Raven getRaven() {
        return raven;
    }

    /**
     * Get the timestamp from a LogEvent
     *
     * @param event LogEvent to get the timestamp from
     * @return Timestamp of the LogEvent
     */
    public static long getTimeStamp(LogEvent event) {
        if (Utils.isForVersion("1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "1.10", "1.11")) {
            try {
                Method method = event.getClass().getMethod("getMillis");
                return (long) method.invoke(event);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return Calendar.getInstance().getTimeInMillis();
            }
        }
        return event.getTimeMillis();
    }
}