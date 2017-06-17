package com.minecraftmarket.minecraftmarket.bukkit.Sentry.editors;

import com.getsentry.raven.event.EventBuilder;
import org.apache.logging.log4j.core.LogEvent;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigInteger;
import java.util.*;

public abstract class EventEditor {
    private final Random RANDOM = new Random();

    public abstract void processEvent(EventBuilder builder, LogEvent event);

    public void shutdown() {
    }

    public Object getValue(ConfigurationSection source, String path, Map<String, String> replacements) {
        if (source.isList(path)) {
            return applyReplacements(source.getStringList(path), replacements);
        } else if (source.isConfigurationSection(path)) {
            ConfigurationSection child = source.getConfigurationSection(path);
            if (child != null) {
                SortedMap<String, Object> childMap = new TreeMap<>();
                for (String childKey : child.getKeys(false)) {
                    Object childValue = getValue(child, childKey, replacements);
                    if (childValue != null) {
                        childMap.put(childKey, childValue);
                    }
                }
                if (!childMap.isEmpty()) {
                    return childMap;
                }
            }
        } else {
            return applyReplacements(source.getString(path), replacements);
        }
        return null;
    }

    public String applyReplacements(String target, Map<String, String> replacements) {
        if (target == null) {
            return null;
        }

        for (String replaceKey : replacements.keySet()) {
            target = target.replace("{" + replaceKey + "}", replacements.get(replaceKey));
        }

        target = target.replace("{random}", new BigInteger(130, RANDOM).toString(32));

        return target;
    }

    public List<String> applyReplacements(List<String> target, Map<String, String> replacements) {
        if (target == null) {
            return null;
        }
        List<String> resultList = new ArrayList<>();
        for (String entry : target) {
            resultList.add(applyReplacements(entry, replacements));
        }
        return resultList;
    }
}