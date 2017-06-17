package com.minecraftmarket.minecraftmarket.bukkit.Sentry;

import com.getsentry.raven.Raven;
import com.getsentry.raven.event.Event;
import com.getsentry.raven.event.EventBuilder;
import com.getsentry.raven.event.interfaces.ExceptionInterface;
import com.getsentry.raven.event.interfaces.MessageInterface;
import com.getsentry.raven.event.interfaces.SentryException;
import com.getsentry.raven.log4j2.SentryAppender;
import com.minecraftmarket.minecraftmarket.bukkit.Sentry.editors.EventEditor;
import com.r4g3baby.pluginutils.Bukkit.Utils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BukkitSentryAppender extends SentryAppender {
    private Set<EventEditor> eventEditors = new HashSet<>();

    public BukkitSentryAppender(Raven raven) {
        super(raven);
    }

    public void shutdown() {
        for (EventEditor editor : eventEditors) {
            editor.shutdown();
        }
    }

    public void addEventEditor(EventEditor eventEditor) {
        eventEditors.add(eventEditor);
    }

    @Override
    protected Event buildEvent(LogEvent event) {
        Message eventMessage = event.getMessage();
        EventBuilder eventBuilder = new EventBuilder()
                .withTimestamp(new Date(getTimeStamp(event)))
                .withMessage(eventMessage.getFormattedMessage())
                .withLogger(event.getLoggerName())
                .withLevel(levelToEventLevel(event.getLevel()))
                .withExtra(THREAD_NAME, event.getThreadName());

        if (!Utils.isNullOrEmpty(serverName)) {
            eventBuilder.withServerName(serverName.trim());
        }

        if (!Utils.isNullOrEmpty(release)) {
            eventBuilder.withRelease(release.trim());
        }

        if (!Utils.isNullOrEmpty(environment)) {
            eventBuilder.withEnvironment(environment.trim());
        }

        if (eventMessage.getFormattedMessage() != null && !eventMessage.getFormattedMessage().equals(eventMessage.getFormat())) {
            eventBuilder.withSentryInterface(new MessageInterface(
                    eventMessage.getFormat(),
                    formatMessageParameters(eventMessage.getParameters()),
                    eventMessage.getFormattedMessage()));
        }

        Throwable throwable = event.getThrown();
        if (throwable != null) {
            Deque<SentryException> exceptionDeque = SentryException.extractExceptionQueue(throwable);
            if (!exceptionDeque.isEmpty()) {
                SentryException firstException = exceptionDeque.removeFirst();
                if (firstException != null) {
                    String exceptionMessage = firstException.getExceptionMessage();
                    if (exceptionMessage == null || exceptionMessage.isEmpty()) {
                        exceptionMessage = eventMessage.getFormattedMessage();
                    }
                    firstException = new SentryException(
                            exceptionMessage,
                            firstException.getExceptionClassName(),
                            firstException.getExceptionPackageName(),
                            firstException.getStackTraceInterface()
                    );
                    exceptionDeque.addFirst(firstException);
                }
            }
            eventBuilder.withSentryInterface(new ExceptionInterface(exceptionDeque));
        }

        eventBuilder.withCulprit(event.getLoggerName());

        if (event.getContextStack() != null && !event.getContextStack().asList().isEmpty()) {
            eventBuilder.withExtra(LOG4J_NDC, event.getContextStack().asList());
        }

        if (event.getContextData() != null) {
            for (Map.Entry<String, String> contextEntry : event.getContextData().toMap().entrySet()) {
                if (extraTags.contains(contextEntry.getKey())) {
                    eventBuilder.withTag(contextEntry.getKey(), contextEntry.getValue());
                } else {
                    eventBuilder.withExtra(contextEntry.getKey(), contextEntry.getValue());
                }
            }
        }

        if (event.getMarker() != null) {
            eventBuilder.withTag(LOG4J_MARKER, event.getMarker().getName());
        }

        for (Map.Entry<String, String> tagEntry : tags.entrySet()) {
            eventBuilder.withTag(tagEntry.getKey(), tagEntry.getValue());
        }

        raven.runBuilderHelpers(eventBuilder);

        for (EventEditor eventEditor : eventEditors) {
            try {
                eventEditor.processEvent(eventBuilder, event);
            } catch (Exception e) {
                Bukkit.getLogger().severe("EventEditor " + eventEditor.getClass().getName() + " failed: " + ExceptionUtils.getStackTrace(e));
            }
        }

        return eventBuilder.build();
    }

    private long getTimeStamp(LogEvent event) {
        if (Utils.isForVersion("1.7", "1.8", "1.9", "1.10", "1.11")) {
            try {
                Method method = event.getClass().getMethod("getMillis");
                return (long) method.invoke(event);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return Calendar.getInstance().getTimeInMillis();
            }
        }
        return event.getTimeMillis();
    }

    private Event.Level levelToEventLevel(Level level) {
        if (level.equals(Level.WARN)) {
            return Event.Level.WARNING;
        } else if (level.equals(Level.ERROR) || level.equals(Level.FATAL)) {
            return Event.Level.ERROR;
        } else if (level.equals(Level.DEBUG)) {
            return Event.Level.DEBUG;
        } else {
            return Event.Level.INFO;
        }
    }
}