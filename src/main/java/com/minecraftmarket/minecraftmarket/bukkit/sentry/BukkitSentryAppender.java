package com.minecraftmarket.minecraftmarket.bukkit.sentry;

import com.getsentry.raven.Raven;
import com.getsentry.raven.event.Event;
import com.getsentry.raven.event.EventBuilder;
import com.getsentry.raven.event.interfaces.ExceptionInterface;
import com.getsentry.raven.event.interfaces.MessageInterface;
import com.getsentry.raven.event.interfaces.SentryException;
import com.getsentry.raven.log4j2.SentryAppender;
import com.minecraftmarket.minecraftmarket.bukkit.sentry.editors.EventEditor;
import com.minecraftmarket.minecraftmarket.bukkit.sentry.editors.PluginInformation;
import com.minecraftmarket.minecraftmarket.bukkit.sentry.editors.ServerInformation;
import com.minecraftmarket.minecraftmarket.bukkit.sentry.editors.StackInformation;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;

import java.util.*;

public class BukkitSentryAppender extends SentryAppender {
    private final List<EventEditor> eventEditors;

    public BukkitSentryAppender(Raven raven) {
        super(raven);

        eventEditors = Arrays.asList(
                new StackInformation(),
                new ServerInformation(),
                new PluginInformation()
        );
    }

    @Override
    public String getName() {
        return "MinecraftMarketAppender";
    }

    @Override
    protected Event buildEvent(LogEvent event) {
        Message eventMessage = event.getMessage();
        EventBuilder eventBuilder = new EventBuilder()
                .withTimestamp(new Date(SentryReporter.getTimeStamp(event)))
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

        if (eventMessage.getFormat() != null && eventMessage.getFormattedMessage() != null && !eventMessage.getFormattedMessage().equals(eventMessage.getFormat())) {
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

        if (event.getContextMap() != null) {
            for (Map.Entry<String, String> contextEntry : event.getContextMap().entrySet()) {
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
            eventEditor.processEvent(eventBuilder, event);
        }

        return eventBuilder.build();
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