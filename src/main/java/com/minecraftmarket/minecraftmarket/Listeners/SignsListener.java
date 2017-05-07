package com.minecraftmarket.minecraftmarket.Listeners;

import com.minecraftmarket.minecraftmarket.MCMarket;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
    public void onBlockBreak(BlockBreakEvent e) {
        if (plugin.getSignsConfig().getDonorSignFor(e.getBlock()) != null) {
            e.setCancelled(true);
            if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
                if (plugin.getSignsConfig().removeDonorSign(e.getBlock())) {
                    e.getPlayer().sendMessage(plugin.getMessagesConfig().getPrefix() + " §aSign removed. Updating signs..");
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                    e.getBlock().breakNaturally();
                }
            }
        } else {
            for (BlockFace blockFace : blockFaces) {
                Block block = e.getBlock().getRelative(blockFace);
                if (block != null && block.getState() instanceof Sign && Objects.equals(getAttachedBlock(block), e.getBlock())) {
                    if (plugin.getSignsConfig().getDonorSignFor(block) != null) {
                        e.setCancelled(true);
                        if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
                            if (plugin.getSignsConfig().removeDonorSign(block)) {
                                e.getPlayer().sendMessage(plugin.getMessagesConfig().getPrefix() + " §aSign removed. Updating signs..");
                                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                                block.breakNaturally();
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