package com.minecraftmarket.minecraftmarket.bungee.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public abstract class BungeeRunnable implements Runnable {
    private int taskId = -1;

    public synchronized void cancel() throws IllegalStateException {
        ProxyServer.getInstance().getScheduler().cancel(getTaskId());
    }

    public synchronized ScheduledTask runAsync(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(ProxyServer.getInstance().getScheduler().runAsync(plugin, this));
    }

    public synchronized ScheduledTask schedule(Plugin plugin, long delay, TimeUnit unit) throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(ProxyServer.getInstance().getScheduler().schedule(plugin, this, delay, unit));
    }

    public synchronized ScheduledTask schedule(Plugin plugin, long delay, long period, TimeUnit unit) throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(ProxyServer.getInstance().getScheduler().schedule(plugin, this, delay, period, unit));
    }

    public synchronized int getTaskId() throws IllegalStateException {
        int id = this.taskId;
        if(id == -1) {
            throw new IllegalStateException("Not scheduled yet");
        } else {
            return id;
        }
    }

    private void checkState() {
        if(this.taskId != -1) {
            throw new IllegalStateException("Already scheduled as " + this.taskId);
        }
    }

    private ScheduledTask setupId(ScheduledTask task) {
        this.taskId = task.getId();
        return task;
    }
}