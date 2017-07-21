package com.minecraftmarket.minecraftmarket.bukkit.listeners;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SignsListener implements Listener {
    private final List<BlockFace> blockFaces = Arrays.asList(BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST);
    private final MCMarket plugin;

    public SignsListener(MCMarket plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent e) {
        if (plugin.getMainConfig().isUseSigns()) {
            if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
                List<String> lines = Arrays.asList(e.getLines());
                if (lines.size() > 1 && lines.get(0).equals("[RecentDonor]") && Utils.isInt(lines.get(1))) {
                    int order = Utils.getInt(lines.get(1));
                    if (order > 0) {
                        if (plugin.getSignsConfig().addDonorSign(order, e.getBlock())) {
                            e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_added")));
                            plugin.getSignsTask().updateSigns();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if (plugin.getMainConfig().isUseSigns()) {
            if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
                if (plugin.getSignsConfig().getDonorSignFor(e.getBlock()) != null) {
                    if (plugin.getSignsConfig().removeDonorSign(e.getBlock())) {
                        e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_removed")));
                        plugin.getSignsTask().updateSigns();
                    }
                } else {
                    for (BlockFace blockFace : blockFaces) {
                        Block block = e.getBlock().getRelative(blockFace);
                        if (block != null && block.getState() instanceof Sign && Objects.equals(getAttachedBlock(block), e.getBlock())) {
                            if (plugin.getSignsConfig().getDonorSignFor(block) != null) {
                                if (plugin.getSignsConfig().removeDonorSign(block)) {
                                    e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_removed")));
                                    plugin.getSignsTask().updateSigns();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Block getAttachedBlock(Block block) {
        MaterialData data = block.getState().getData();
        if (data instanceof Attachable) {
            return block.getRelative(((Attachable) data).getAttachedFace());
        }
        return null;
    }
}