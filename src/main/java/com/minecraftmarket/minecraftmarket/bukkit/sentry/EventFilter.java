package com.minecraftmarket.minecraftmarket.bukkit.sentry;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.*;

public class EventFilter extends AbstractFilter {
    private final Set<Integer> filterLevels;
    private final Set<String> filterSearch;

    /**
     * Constructor
     */
    public EventFilter() {
        super();

        // Setup levels to filter out
        filterLevels = new HashSet<>();
        Set<Level> filterLevelsRaw = new HashSet<>(Arrays.asList(Level.OFF, Level.ALL, Level.INFO, Level.TRACE, Level.DEBUG));
        for (Level level : filterLevelsRaw) {
            filterLevels.add(level.intLevel());
        }

        // Setup filter search terms
        filterSearch = new HashSet<>(Arrays.asList(
                "MinecraftMarket",
                "MCMarket",
                "/mm"
        ));
    }

    /**
     * Return result based on the level
     *
     * @param level The level to check
     * @return The result
     */
    private Result process(String message, Level level, Throwable throwable, String threadName, String loggerName) {
        // Only let WARN, ERROR and FATAL pass
        if (filterLevels.contains(level.intLevel())) {
            return Result.DENY;
        }


        // Collect information to search in
        List<String> toSearch = new ArrayList<>();
        toSearch.add(message);
        toSearch.add(threadName);
        toSearch.add(loggerName);
        if (throwable != null) {
            toSearch.add(ExceptionUtils.getStackTrace(throwable));
            toSearch.add(throwable.getMessage());
        }

        // Test all search terms
        boolean matchingSearch = false;
        for (String search : filterSearch) {
            // Case-insensitive search
            search = search.toLowerCase();
            for (String toSearchPart : toSearch) {
                if (toSearchPart != null && toSearchPart.toLowerCase().contains(search)) {
                    matchingSearch = true;
                    break;
                }
            }
            if (matchingSearch) {
                break;
            }
        }

        // Allow event to pass if search matched
        if (matchingSearch) {
            return Result.NEUTRAL;
        }

        return Result.DENY;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object... params) {
        return process(
                ParameterizedMessage.format(message, params),
                level,
                null,
                null,
                logger.getName()
        );
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object message, Throwable throwable) {
        return process(
                message == null ? null : message.toString(),
                level,
                throwable,
                null,
                logger == null ? null : logger.getName()
        );
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return process(
                message == null ? null : message.getFormattedMessage(),
                level,
                throwable,
                null,
                logger == null ? null : logger.getName()
        );
    }

    @Override
    public Result filter(LogEvent logEvent) {
        if (logEvent == null) {
            return onMismatch;
        }
        return process(
                logEvent.getMessage() == null ? null : logEvent.getMessage().getFormattedMessage(),
                logEvent.getLevel(),
                logEvent.getThrown(),
                logEvent.getThreadName(),
                logEvent.getLoggerName()
        );
    }
}