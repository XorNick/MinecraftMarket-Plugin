package com.minecraftmarket.minecraftmarket.bukkit.sentry;

import com.getsentry.raven.DefaultRavenFactory;
import com.getsentry.raven.dsn.Dsn;

import java.util.Collection;
import java.util.HashSet;

public class BukkitRavenFactory extends DefaultRavenFactory {

    private Collection<String> inAppFrames;

    public BukkitRavenFactory() {
        super();

        // Setup package names that should be considered in-app
        inAppFrames = new HashSet<>();
        inAppFrames.add("com.minecraftmarket");
    }

    @Override
    protected Collection<String> getInAppFrames(Dsn dsn) {
        return inAppFrames;
    }
}