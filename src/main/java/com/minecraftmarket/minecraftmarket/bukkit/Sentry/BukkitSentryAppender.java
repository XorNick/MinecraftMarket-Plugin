package com.minecraftmarket.minecraftmarket.bukkit.Sentry;

import com.getsentry.raven.Raven;
import com.getsentry.raven.event.Event;
import com.getsentry.raven.log4j2.SentryAppender;
import com.minecraftmarket.minecraftmarket.bukkit.Sentry.editors.EventEditor;
import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.Set;

public class BukkitSentryAppender extends SentryAppender {
    private Set<EventEditor> eventEditors = new HashSet<>();

    public BukkitSentryAppender(Raven raven) {
        super(raven);
    }

    public void shutdown() {
        for(EventEditor editor : eventEditors) {
            editor.shutdown();
        }
    }

    public void addEventEditor(EventEditor eventEditor) {
        eventEditors.add(eventEditor);
    }

    private Event.Level levelToEventLevel(Level level) {
        if(level.equals(Level.WARN)) {
            return Event.Level.WARNING;
        } else if(level.equals(Level.ERROR) || level.equals(Level.FATAL)) {
            return Event.Level.ERROR;
        } else if(level.equals(Level.DEBUG)) {
            return Event.Level.DEBUG;
        } else {
            return Event.Level.INFO;
        }
    }
}