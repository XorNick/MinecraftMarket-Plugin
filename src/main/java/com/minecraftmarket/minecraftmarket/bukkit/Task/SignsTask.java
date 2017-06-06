package com.minecraftmarket.minecraftmarket.bukkit.Task;

import com.minecraftmarket.minecraftmarket.bukkit.Configs.SignsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.core.MCMApi;
import com.r4g3baby.pluginutils.Items.SkullUtils;
import com.r4g3baby.pluginutils.Mojang.ProfileUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SignsTask implements Runnable {
    private final MCMarket plugin;

    public SignsTask(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        updateSigns();
    }

    public void updateSigns() {
        if (plugin.isAuthenticated()) {
            List<MCMApi.RecentDonor> recentDonors = plugin.getApi().getRecentDonors();
            Map<Integer, Set<SignsConfig.DonorSign>> donorSigns = plugin.getSignsConfig().getDonorSigns();
            for (Integer key : donorSigns.keySet()) {
                if (key <= recentDonors.size()) {
                    MCMApi.RecentDonor recentDonor = recentDonors.get(key - 1);
                    for (SignsConfig.DonorSign donorSign : donorSigns.get(key)) {
                        if (donorSign.getBlock().getState() instanceof Sign) {
                            Sign sign = (Sign) donorSign.getBlock().getState();
                            List<String> lines = plugin.getMessagesConfig().getSignsLayout();
                            if (lines.size() >= 1) {
                                sign.setLine(0, replaceVars(lines.get(0), recentDonor));
                            }
                            if (lines.size() >= 2) {
                                sign.setLine(1, replaceVars(lines.get(1), recentDonor));
                            }
                            if (lines.size() >= 3) {
                                sign.setLine(2, replaceVars(lines.get(2), recentDonor));
                            }
                            if (lines.size() >= 4) {
                                sign.setLine(3, replaceVars(lines.get(3), recentDonor));
                            }

                            Block attached = getAttachedBlock(donorSign.getBlock());
                            if (attached != null) {
                                Block up = attached.getRelative(BlockFace.UP);
                                if (up != null && up.getState() instanceof Skull) {
                                    ProfileUtils.getUniqueId(recentDonor.getUser(), uuid -> {
                                        if (uuid != null) {
                                            ProfileUtils.getProfile(uuid.toString(), profile -> {
                                                if (profile != null && profile.get("skin") != null) {
                                                    SkullUtils.setSkullWithNonPlayerProfile(profile.get("skin"), recentDonor.getUser(), up);
                                                } else {
                                                    SkullUtils.setSkullWithNonPlayerProfile(plugin.getMainConfig().getDefaultHeadSkin(), recentDonor.getUser(), up);
                                                }
                                            });
                                        } else {
                                            SkullUtils.setSkullWithNonPlayerProfile(plugin.getMainConfig().getDefaultHeadSkin(), recentDonor.getUser(), up);
                                        }
                                    });
                                }
                            }
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                sign.update();
                                sign.update(true, true);
                            });
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

    private String replaceVars(String msg, MCMApi.RecentDonor recentDonor) {
        msg = msg.replace("{donor_id}", "" + recentDonor.getId());
        msg = msg.replace("{donor_name}", recentDonor.getUser());
        msg = msg.replace("{donor_item}", recentDonor.getItem());
        msg = msg.replace("{donor_price}", recentDonor.getPrice());
        msg = msg.replace("{donor_currency}", recentDonor.getCurrency());
        msg = msg.replace("{donor_date}", recentDonor.getDate());
        return msg;
    }
}