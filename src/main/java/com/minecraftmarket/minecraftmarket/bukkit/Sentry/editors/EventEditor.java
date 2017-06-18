package com.minecraftmarket.minecraftmarket.bukkit.Sentry.editors;

import com.getsentry.raven.event.EventBuilder;
import org.apache.logging.log4j.core.LogEvent;

public abstract class EventEditor {

    /**
     * Process and incoming event
     *
     * @param builder The builder to apply changes to
     * @param event   The event that is happening
     */
    public abstract void processEvent(EventBuilder builder, LogEvent event);
}