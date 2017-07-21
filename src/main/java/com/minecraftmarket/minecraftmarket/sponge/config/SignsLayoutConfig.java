package com.minecraftmarket.minecraftmarket.sponge.config;

import com.google.common.reflect.TypeToken;
import com.minecraftmarket.minecraftmarket.sponge.utils.config.ConfigFile;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.util.List;

public class SignsLayoutConfig extends ConfigFile {
    private List<String> activeSignsLayout;
    private List<String> waitingSignsLayout;

    public SignsLayoutConfig(File baseDir) {
        super(baseDir, "signsLayout");

        try {
            activeSignsLayout = config.getNode("SignsLayout", "Active").getList(TypeToken.of(String.class));
            waitingSignsLayout = config.getNode("SignsLayout", "Waiting").getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public List<String> getActiveSignsLayout() {
        return activeSignsLayout;
    }

    public List<String> getWaitingSignsLayout() {
        return waitingSignsLayout;
    }
}