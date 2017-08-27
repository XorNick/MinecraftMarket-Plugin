package com.minecraftmarket.minecraftmarket.nukkit.tasks;

import cn.nukkit.block.BlockSignPost;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.scheduler.AsyncTask;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.nukkit.configs.SignsConfig;

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
        this.mcmDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.dateFormat = new SimpleDateFormat(plugin.getMainConfig().getDateFormat());
    }

    @Override
    public void run() {
        updateSigns();
    }

    public void updateSigns() {
        plugin.getServer().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
            @Override
            public void onRun() {
                if (plugin.isAuthenticated()) {
                    List<MCMarketApi.Purchase> purchases = plugin.getApi().getPurchases();
                    Map<Integer, Set<SignsConfig.DonorSign>> donorSigns = plugin.getSignsConfig().getDonorSigns();
                    for (Integer key : donorSigns.keySet()) {
                        for (SignsConfig.DonorSign donorSign : donorSigns.get(key)) {
                            if (donorSign.getBlock() instanceof BlockSignPost) {
                                BlockEntitySign sign = (BlockEntitySign) donorSign.getBlock().getLevel().getBlockEntity(donorSign.getBlock());
                                if (key <= purchases.size()) {
                                    MCMarketApi.Purchase purchase = purchases.get(key - 1);
                                    List<String> lines = plugin.getSignsLayoutConfig().getActiveLayout();
                                    if (lines.size() == 1) {
                                        sign.setText(replaceVars(lines.get(0), purchase));
                                    } else if (lines.size() == 2) {
                                        sign.setText(replaceVars(lines.get(0), purchase), replaceVars(lines.get(1), purchase));
                                    } else if (lines.size() == 3) {
                                        sign.setText(replaceVars(lines.get(0), purchase), replaceVars(lines.get(1), purchase), replaceVars(lines.get(2), purchase));
                                    } else if (lines.size() == 4) {
                                        sign.setText(replaceVars(lines.get(0), purchase), replaceVars(lines.get(1), purchase), replaceVars(lines.get(2), purchase), replaceVars(lines.get(3), purchase));
                                    } else {
                                        sign.setText();
                                    }
                                } else {
                                    List<String> lines = plugin.getSignsLayoutConfig().getWaitingLayout();
                                    if (lines.size() == 1) {
                                        sign.setText(lines.get(0));
                                    } else if (lines.size() == 2) {
                                        sign.setText(lines.get(0), lines.get(1));
                                    } else if (lines.size() == 3) {
                                        sign.setText(lines.get(0), lines.get(1), lines.get(2));
                                    } else if (lines.size() == 4) {
                                        sign.setText(lines.get(0), lines.get(1), lines.get(2), lines.get(3));
                                    } else {
                                        sign.setText();
                                    }
                                }
                            } else {
                                plugin.getSignsConfig().removeDonorSign(donorSign.getBlock());
                            }
                        }
                    }
                }
            }
        });
    }

    private String replaceVars(String msg, MCMarketApi.Purchase purchase) {
        msg = msg.replace("{purchase_id}", "" + purchase.getId())
                .replace("{purchase_name}", purchase.getName())
                .replace("{purchase_price}", purchase.getPrice())
                .replace("{purchase_currency}", purchase.getCurrency().getCode())
                .replace("{player_name}", purchase.getPlayer().getName());
        try {
            msg = msg.replace("{purchase_date}", dateFormat.format(mcmDateFormat.parse(purchase.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return msg;
    }
}