package com.minecraftmarket.minecraftmarket.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {
    public static Set<String> getJarResources(Class<?> c) {
        Set<String> files = new HashSet<>();
        try {
            File jarFile = urlToFile(getLocation(c).toString());
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                files.add(name);
            }
            jar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    private static URL getLocation(Class<?> c) {
        if (c == null) {
            return null;
        }

        try {
            URL codeSourceLocation = c.getProtectionDomain().getCodeSource().getLocation();
            if (codeSourceLocation != null) {
                return codeSourceLocation;
            }
        } catch (SecurityException | NullPointerException ignored) {
        }
        URL classResource = c.getResource(c.getSimpleName() + ".class");
        if (classResource == null) {
            return null;
        }

        String url = classResource.toString();
        String suffix = c.getCanonicalName().replace('.', '/') + ".class";
        if (!url.endsWith(suffix)) {
            return null;
        }

        String path = url.substring(0, url.length() - suffix.length());
        if (path.startsWith("jar:")) {
            path = path.substring(4, path.length() - 2);
        }

        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File urlToFile(String url) {
        String path = url;
        if (path.startsWith("jar:")) {
            int index = path.indexOf("!/");
            path = path.substring(4, index);
        }
        try {
            if (System.getProperty("os.name").startsWith("Windows") && path.matches("file:[A-Za-z]:.*")) {
                path = "file:/" + path.substring(5);
            }
            return new File(new URL(path).toURI());
        } catch (MalformedURLException | URISyntaxException ignored) {
        }
        if (path.startsWith("file:")) {
            path = path.substring(5);
            return new File(path);
        }
        throw new IllegalArgumentException("Invalid URL: " + url);
    }
}