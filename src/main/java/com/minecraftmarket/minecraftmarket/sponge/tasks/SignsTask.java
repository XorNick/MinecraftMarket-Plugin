package com.minecraftmarket.minecraftmarket.sponge.tasks;


import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import com.minecraftmarket.minecraftmarket.sponge.config.SignsConfig;
import com.minecraftmarket.minecraftmarket.sponge.utils.chat.Colors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            if (plugin.isAuthenticated()) {
                List<MCMarketApi.RecentDonor> recentDonors = plugin.getApi().getRecentDonors();
                Map<Integer, Set<SignsConfig.DonorSign>> donorSigns = plugin.getSignsConfig().getDonorSigns();
                for (Integer key : donorSigns.keySet()) {
                    for (SignsConfig.DonorSign donorSign : donorSigns.get(key)) {
                        if (donorSign.getLocation().getTileEntity().isPresent() && donorSign.getLocation().getTileEntity().get() instanceof Sign) {
                            Sign sign = (Sign) donorSign.getLocation().getTileEntity().get();
                            if (key <= recentDonors.size()) {
                                MCMarketApi.RecentDonor recentDonor = recentDonors.get(key - 1);
                                List<String> lines = plugin.getSignsLayoutConfig().getActiveSignsLayout();
                                Optional<SignData> optionalSignData = sign.get(SignData.class);
                                if (optionalSignData.isPresent()) {
                                    SignData signData = optionalSignData.get();
                                    if (signData.getValue(Keys.SIGN_LINES).isPresent()) {
                                        if (lines.size() == 1) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, replaceVars(lines.get(0), recentDonor)));
                                        } else if (lines.size() == 2) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, replaceVars(lines.get(0), recentDonor)));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, replaceVars(lines.get(1), recentDonor)));
                                        } else if (lines.size() == 3) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, replaceVars(lines.get(0), recentDonor)));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, replaceVars(lines.get(1), recentDonor)));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(2, replaceVars(lines.get(2), recentDonor)));
                                        } else if (lines.size() == 4) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, replaceVars(lines.get(0), recentDonor)));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, replaceVars(lines.get(1), recentDonor)));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(2, replaceVars(lines.get(2), recentDonor)));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(3, replaceVars(lines.get(3), recentDonor)));
                                        } else {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, Text.of("")));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, Text.of("")));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(2, Text.of("")));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(3, Text.of("")));
                                        }
                                        sign.offer(signData);
                                    }
                                }
                            } else {
                                List<String> lines = plugin.getSignsLayoutConfig().getWaitingSignsLayout();
                                Optional<SignData> optionalSignData = sign.get(SignData.class);
                                if (optionalSignData.isPresent()) {
                                    SignData signData = optionalSignData.get();
                                    if (signData.getValue(Keys.SIGN_LINES).isPresent()) {
                                        if (lines.size() == 1) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, Colors.color(lines.get(0))));
                                        } else if (lines.size() == 2) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, Colors.color(lines.get(0))));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, Colors.color(lines.get(1))));
                                        } else if (lines.size() == 3) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, Colors.color(lines.get(0))));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, Colors.color(lines.get(1))));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(2, Colors.color(lines.get(2))));
                                        } else if (lines.size() == 4) {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, Colors.color(lines.get(0))));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, Colors.color(lines.get(1))));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(2, Colors.color(lines.get(2))));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(3, Colors.color(lines.get(3))));
                                        } else {
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(0, Text.of("")));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(1, Text.of("")));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(2, Text.of("")));
                                            signData.set(signData.getValue(Keys.SIGN_LINES).get().set(3, Text.of("")));
                                        }
                                        sign.offer(signData);
                                    }
                                }
                            }
                        } else {
                            System.out.println("remove");
                            plugin.getSignsConfig().removeDonorSign(donorSign.getLocation());
                        }
                    }
                }
            }
        }).submit(plugin);
    }

    private Text replaceVars(String msg, MCMarketApi.RecentDonor recentDonor) {
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
        return Colors.color(msg);
    }
}