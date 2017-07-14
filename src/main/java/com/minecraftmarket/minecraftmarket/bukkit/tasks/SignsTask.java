package com.minecraftmarket.minecraftmarket.bukkit.tasks;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.configs.SignsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.utils.items.SkullUtils;
import com.minecraftmarket.minecraftmarket.bukkit.utils.mojang.ProfileUtils;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SignsTask implements Runnable {
    private final MCMarket plugin;
    private final DateFormat mcmDateFormat;
    private final DateFormat dateFormat;

    public SignsTask(MCMarket plugin) {
        this.plugin = plugin;
        this.mcmDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateFormat = new SimpleDateFormat(plugin.getMainConfig().getDateFormat());
    }

    @Override
    public void run() {
        updateSigns();
    }

    public void updateSigns() {
        if (plugin.isAuthenticated()) {
            List<MCMarketApi.RecentDonor> recentDonors = plugin.getApi().getRecentDonors();
            Map<Integer, Set<SignsConfig.DonorSign>> donorSigns = plugin.getSignsConfig().getDonorSigns();
            for (Integer key : donorSigns.keySet()) {
                for (SignsConfig.DonorSign donorSign : donorSigns.get(key)) {
                    if (donorSign.getBlock().getState() instanceof Sign) {
                        Sign sign = (Sign) donorSign.getBlock().getState();
                        if (key <= recentDonors.size()) {
                            MCMarketApi.RecentDonor recentDonor = recentDonors.get(key - 1);
                            List<String> lines = plugin.getLayoutsConfig().getActiveSignsLayout();
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
                        } else {
                            List<String> lines = plugin.getLayoutsConfig().getWaitingSignsLayout();
                            if (lines.size() >= 1) {
                                sign.setLine(0, lines.get(0));
                            }
                            if (lines.size() >= 2) {
                                sign.setLine(1, lines.get(1));
                            }
                            if (lines.size() >= 3) {
                                sign.setLine(2, lines.get(2));
                            }
                            if (lines.size() >= 4) {
                                sign.setLine(3, lines.get(3));
                            }

                            Block attached = getAttachedBlock(donorSign.getBlock());
                            if (attached != null) {
                                Block up = attached.getRelative(BlockFace.UP);
                                if (up != null && up.getState() instanceof Skull) {
                                    SkullUtils.setSkullWithNonPlayerProfile(plugin.getMainConfig().getDefaultHeadSkin(), "Steve", up);
                                }
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

    private Block getAttachedBlock(Block block) {
        MaterialData data = block.getState().getData();
        if (data instanceof Attachable) {
            return block.getRelative(((Attachable) data).getAttachedFace());
        }
        return null;
    }

    private String replaceVars(String msg, MCMarketApi.RecentDonor recentDonor) {
        msg = msg.replace("{donor_id}", "" + recentDonor.getId())
                .replace("{donor_name}", recentDonor.getUser())
                .replace("{donor_item}", recentDonor.getItem())
                .replace("{donor_price}", recentDonor.getPrice())
                .replace("{donor_currency}", recentDonor.getCurrency());
        try {
            msg = msg.replace("{donor_date}", dateFormat.format(mcmDateFormat.parse(recentDonor.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return msg;
    }
}