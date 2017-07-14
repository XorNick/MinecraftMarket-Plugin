package com.minecraftmarket.minecraftmarket.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {
    public static Set<String> getJarResources(CodeSource codeSource) {
        Set<String> files = new HashSet<>();
        try {
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                files.add(name);
            }
            jar.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return files;
    }
}