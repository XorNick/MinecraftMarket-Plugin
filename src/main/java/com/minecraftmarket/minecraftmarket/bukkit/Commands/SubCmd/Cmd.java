package com.minecraftmarket.minecraftmarket.bukkit.Commands.SubCmd;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class Cmd {
    private final String command;
    private final String description;
    private final String args;
    private final List<String> tabComplete;

    public Cmd(String command, String description) {
        this(command, description, "", Collections.emptyList());
    }

    public Cmd(String command, String description, String args) {
        this(command, description, args, Collections.emptyList());
    }

    public Cmd(String command, String description, String args, List<String> tabComplete) {
        this.command = command;
        this.description = description;
        this.args = args;
        this.tabComplete = tabComplete;
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

    public List<String> getTabcomplete() {
        return tabComplete;
    }

    public abstract void run(CommandSender sender, String[] args);
}