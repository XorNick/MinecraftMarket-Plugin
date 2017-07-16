package com.minecraftmarket.minecraftmarket.common.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class I18n {
    private final File dataFolder;
    private final Logger logger;
    private static I18n instance;

    private static final String BASE_NAME = "messages";
    private static final Pattern NODOUBLEMARK = Pattern.compile("''");

    private Locale currentLocale = Locale.ENGLISH;
    private ResourceBundle resourceBundle;
    private Map<String, MessageFormat> messageFormatCache = new HashMap<>();

    public I18n(File dataFolder, Logger logger) {
        this.dataFolder = dataFolder;
        this.logger = logger;
        resourceBundle = ResourceBundle.getBundle(BASE_NAME, currentLocale, new FileResClassLoader(I18n.class.getClassLoader(), dataFolder));
    }

    public void onEnable() {
        instance = this;
    }

    public void onDisable() {
        instance = null;
    }

    private String translate(String string) {
        try {
            return resourceBundle.getString(string);
        } catch (MissingResourceException ex) {
            if (logger != null) logger.log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", ex.getKey(), resourceBundle.getLocale().toString()), ex);
            return string;
        }
    }

    public static String tl(String string, Object... objects) {
        if (instance == null) {
            return "";
        }
        if (objects.length == 0) {
            return NODOUBLEMARK.matcher(instance.translate(string)).replaceAll("'");
        } else {
            return instance.format(string, objects);
        }
    }

    private String format(String string, Object... objects) {
        String format = translate(string);
        MessageFormat messageFormat = messageFormatCache.get(format);
        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(format);
            } catch (IllegalArgumentException e) {
                if (logger != null) logger.log(Level.SEVERE, "Invalid Translation key for '" + string + "': " + e.getMessage());
                format = format.replaceAll("\\{(\\D*?)}", "\\[$1\\]");
                messageFormat = new MessageFormat(format);
            }
            messageFormatCache.put(format, messageFormat);
        }
        return messageFormat.format(objects);
    }

    public void updateLocale(String loc) {
        if (loc != null && !loc.isEmpty()) {
            final String[] parts = loc.split("[_.]");
            if (parts.length == 1) {
                currentLocale = new Locale(parts[0]);
            }
            if (parts.length == 2) {
                currentLocale = new Locale(parts[0], parts[1]);
            }
            if (parts.length == 3) {
                currentLocale = new Locale(parts[0], parts[1], parts[2]);
            }
        }
        ResourceBundle.clearCache();
        messageFormatCache = new HashMap<>();
        if (logger != null) logger.log(Level.INFO, String.format("Using locale %s", currentLocale.toString()));

        try {
            resourceBundle = ResourceBundle.getBundle(BASE_NAME, currentLocale, new FileResClassLoader(I18n.class.getClassLoader(), dataFolder));
        } catch (MissingResourceException e) {
            resourceBundle = ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH, new FileResClassLoader(I18n.class.getClassLoader(), dataFolder));
        }
    }

    private class FileResClassLoader extends ClassLoader {
        private final File dataFolder;

        FileResClassLoader(ClassLoader classLoader, File dataFolder) {
            super(classLoader);
            this.dataFolder = dataFolder;
        }

        @Override
        public URL getResource(String string) {
            File file = new File(dataFolder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException ignored) {
                }
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(String string) {
            File file = new File(dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ignored) {
                }
            }
            return null;
        }
    }
}