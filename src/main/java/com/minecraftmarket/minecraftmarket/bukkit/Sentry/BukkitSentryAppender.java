package com.minecraftmarket.minecraftmarket.bukkit.Sentry;

import com.getsentry.raven.Raven;
import com.getsentry.raven.log4j2.SentryAppender;

public class BukkitSentryAppender extends SentryAppender {

    public BukkitSentryAppender(Raven raven) {
        super(raven);
    }
}