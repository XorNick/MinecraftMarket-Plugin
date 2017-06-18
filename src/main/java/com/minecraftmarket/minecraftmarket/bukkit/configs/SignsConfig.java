package com.minecraftmarket.minecraftmarket.bukkit.configs;

import com.r4g3baby.pluginutils.bukkit.Utils;
import com.r4g3baby.pluginutils.configs.BukkitConfigFile;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SignsConfig extends BukkitConfigFile {
    private final Map<Integer, Set<DonorSign>> donorSigns = new HashMap<>();

    public SignsConfig(JavaPlugin plugin) {
        super(plugin, "signs");

        for (String key : config.getKeys(false)) {
            if (Utils.isInt(key)) {
                Set<DonorSign> signs = new HashSet<>();
                for (Location loc : Utils.stringsToLocArray(config.getStringList(key))) {
                    Block block = loc.getWorld().getBlockAt(loc);
                    if (block.getState() instanceof Sign) {
                        signs.add(new DonorSign(Utils.getInt(key), block));
                    }
                }
                donorSigns.put(Utils.getInt(key), signs);
            }
        }
    }

    public Map<Integer, Set<DonorSign>> getDonorSigns() {
        return donorSigns;
    }

    public boolean addDonorSign(Integer key, Block block) {
        DonorSign donorSign = getDonorSignFor(block);
        if (donorSign == null) {
            Set<DonorSign> signs;
            if (donorSigns.containsKey(key)) {
                signs = donorSigns.get(key);
            } else {
                signs = new HashSet<>();
            }
            signs.add(new DonorSign(key, block));
            donorSigns.put(key, signs);
            List<String> locs = new ArrayList<>();
            for (DonorSign ds : signs) {
                locs.add(Utils.locToString(ds.getBlock().getLocation()));
            }
            config.set(String.valueOf(key), locs);
            saveConfig();
            return true;
        }
        return false;
    }

    public boolean removeDonorSign(Block block) {
        DonorSign donorSign = getDonorSignFor(block);
        if (donorSign != null) {
            Set<DonorSign> signs = donorSigns.get(donorSign.getKey());
            signs.remove(donorSign);
            List<String> locs = new ArrayList<>();
            for (DonorSign ds : signs) {
                locs.add(Utils.locToString(ds.getBlock().getLocation()));
            }
            config.set(String.valueOf(donorSign.getKey()), locs);
            saveConfig();
            return true;
        }
        return false;
    }

    public DonorSign getDonorSignFor(Block block) {
        for (Set<SignsConfig.DonorSign> donorSigns : donorSigns.values()) {
            for (SignsConfig.DonorSign donorSign : donorSigns) {
                if (donorSign.isFor(block)) {
                    return donorSign;
                }
            }
        }
        return null;
    }

    public class DonorSign {
        private final Integer key;
        private final Block block;

        DonorSign(Integer key, Block block) {
            this.key = key;
            this.block = block;
        }

        public Integer getKey() {
            return key;
        }

        public Block getBlock() {
            return this.block;
        }

        public boolean isFor(Block block) {
            Location loc = this.block.getLocation();
            Location bLoc = block.getLocation();
            return loc.getBlockX() == bLoc.getBlockX() && loc.getBlockY() == bLoc.getBlockY() && loc.getBlockZ() == bLoc.getBlockZ();
        }
    }
}