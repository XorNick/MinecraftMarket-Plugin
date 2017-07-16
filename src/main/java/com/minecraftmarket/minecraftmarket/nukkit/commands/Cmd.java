package com.minecraftmarket.minecraftmarket.nukkit.commands;

import cn.nukkit.command.CommandSender;

public abstract class Cmd {
    private final String command;
    private final String description;
    private final String args;

    public Cmd(String command, String description) {
        this(command, description, "");
    }

    public Cmd(String command, String description, String args) {
        this.command = command;
        this.description = description;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getArgs() {
        return args;
    }

    public abstract void run(CommandSender sender, String[] args);
}