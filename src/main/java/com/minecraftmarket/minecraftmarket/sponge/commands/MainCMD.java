package com.minecraftmarket.minecraftmarket.sponge.commands;

import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import com.minecraftmarket.minecraftmarket.sponge.commands.subcmds.*;
import com.minecraftmarket.minecraftmarket.sponge.utils.chat.Colors;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainCMD implements CommandExecutor {
    private final List<Cmd> subCmds = new ArrayList<>();

    public MainCMD(MCMarket plugin) {
        subCmds.add(new ApiKey(plugin));
        subCmds.add(new Check(plugin));
        subCmds.add(new UpdateSigns(plugin));
        subCmds.add(new Reload(plugin));
        subCmds.add(new Version(plugin));
    }

    @Override
    public CommandResult execute(CommandSource sender, CommandContext arguments) throws CommandException {
        Optional<String> optionalArg1 = arguments.getOne("arg1");
        if (optionalArg1.isPresent()) {
            List<String> subCmdArgs = new ArrayList<>();
            arguments.<String>getOne("arg2").ifPresent(subCmdArgs::add);
            for (Cmd subCmd : subCmds) {
                if (subCmd.getCommand().equalsIgnoreCase(optionalArg1.get())) {
                    subCmd.run(sender, subCmdArgs.toArray(new String[subCmdArgs.size()]));
                    return CommandResult.success();
                }
            }
            sendHelp(sender);
        } else {
            sendHelp(sender);
        }

        return CommandResult.success();
    }

    private void sendHelp(CommandSource sender) {
        sender.sendMessage(Colors.color("&7&m================ " + "&eMinecraftMarket Help " + "&7&m================"));
        for (Cmd subCmd : subCmds) {
            if (subCmd.getArgs().isEmpty()) {
                sender.sendMessage(Colors.color("&6/MM " + subCmd.getCommand() + " &8- &7" + subCmd.getDescription()));
            } else {
                sender.sendMessage(Colors.color("&6/MM " + subCmd.getCommand() + " " + subCmd.getArgs() + " &8- &7" + subCmd.getDescription()));
            }
        }
        sender.sendMessage(Colors.color("&7&m==================================================="));
    }
}