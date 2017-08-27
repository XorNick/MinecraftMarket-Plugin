package com.minecraftmarket.minecraftmarket.sponge.config;

import com.google.common.reflect.TypeToken;
import com.minecraftmarket.minecraftmarket.sponge.utils.config.ConfigFile;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.util.List;

public class SignsLayoutConfig extends ConfigFile {
    private List<String> activeLayout;
    private List<String> waitingLayout;

    public SignsLayoutConfig(File baseDir) {
        super(baseDir, "signsLayout");

        try {
            activeLayout = config.getNode("Active").getList(TypeToken.of(String.class));
            waitingLayout = config.getNode("Waiting").getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public List<String> getActiveLayout() {
        return activeLayout;
    }

    public List<String> getWaitingLayout() {
        return waitingLayout;
    }
}