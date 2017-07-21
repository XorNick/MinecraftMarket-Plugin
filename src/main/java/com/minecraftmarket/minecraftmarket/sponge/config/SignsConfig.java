package com.minecraftmarket.minecraftmarket.sponge.config;

import com.google.common.reflect.TypeToken;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import com.minecraftmarket.minecraftmarket.sponge.utils.config.ConfigFile;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.*;

public class SignsConfig extends ConfigFile {
    private final Map<Integer, Set<DonorSign>> donorSigns = new HashMap<>();

    public SignsConfig(File baseDir) {
        super(baseDir, "signs");

        for (Object objKey : config.getChildrenMap().keySet()) {
            if (objKey instanceof String) {
                String key = (String) objKey;
                if (Utils.isInt(key)) {
                    Set<DonorSign> signs = new HashSet<>();
                    System.out.println("key " + key);
                    try {
                        for (Location<World> loc : stringsToLocArray(config.getChildrenMap().get(objKey).getList(TypeToken.of(String.class)))) {
                            if (loc != null && loc.getTileEntity().isPresent() && loc.getTileEntity().get() instanceof Sign) {
                                System.out.println("2");
                                signs.add(new DonorSign(Utils.getInt(key), loc));
                            }
                        }
                    } catch (ObjectMappingException e) {
                        e.printStackTrace();
                    }
                    donorSigns.put(Utils.getInt(key), signs);
                }
            }
        }
    }

    public Map<Integer, Set<DonorSign>> getDonorSigns() {
        return donorSigns;
    }

    public boolean addDonorSign(Integer key, Location<World> location) {
        DonorSign donorSign = getDonorSignFor(location);
        if (donorSign == null) {
            Set<DonorSign> signs;
            if (donorSigns.containsKey(key)) {
                signs = donorSigns.get(key);
            } else {
                signs = new HashSet<>();
            }
            signs.add(new DonorSign(key, location));
            donorSigns.put(key, signs);
            List<String> locs = new ArrayList<>();
            for (DonorSign ds : signs) {
                locs.add(locToString(ds.getLocation()));
            }
            if (locs.size() > 0) {
                config.getNode(String.valueOf(key)).setValue(locs);
            } else {
                config.getNode(String.valueOf(key)).setValue(null);
            }
            saveConfig();
            return true;
        }
        return false;
    }

    public boolean removeDonorSign(Location<World> location) {
        DonorSign donorSign = getDonorSignFor(location);
        if (donorSign != null) {
            Set<DonorSign> signs = donorSigns.get(donorSign.getKey());
            signs.remove(donorSign);
            List<String> locs = new ArrayList<>();
            for (DonorSign ds : signs) {
                locs.add(locToString(ds.getLocation()));
            }
            if (locs.size() > 0) {
                config.getNode(String.valueOf(donorSign.getKey())).setValue(locs);
            } else {
                config.getNode(String.valueOf(donorSign.getKey())).setValue(null);
            }
            saveConfig();
            return true;
        }
        return false;
    }

    public DonorSign getDonorSignFor(Location<World> location) {
        for (Set<SignsConfig.DonorSign> donorSigns : donorSigns.values()) {
            for (SignsConfig.DonorSign donorSign : donorSigns) {
                if (donorSign.isFor(location)) {
                    return donorSign;
                }
            }
        }
        return null;
    }

    public class DonorSign {
        private final Integer key;
        private final Location<World> location;

        DonorSign(Integer key, Location<World> location) {
            this.key = key;
            this.location = location;
        }

        public Integer getKey() {
            return key;
        }

        public Location<World> getLocation() {
            return location;
        }

        public boolean isFor(Location<World> location) {
            return this.location.getBlockX() == location.getBlockX() && this.location.getBlockY() == location.getBlockY() && this.location.getBlockZ() == location.getBlockZ();
        }
    }

    private String locToString(Location<World> loc) {
        return loc.getExtent().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    private Location<World> stringToLoc(String str) {
        String[] a = str.split(",");
        if (a.length < 4) {
            return null;
        }

        Optional<World> w = Sponge.getServer().getWorld(a[0]);
        if (!w.isPresent()) {
            return null;
        }

        double x = Double.parseDouble(a[1]);
        double y = Double.parseDouble(a[2]);
        double z = Double.parseDouble(a[3]);

        return new Location<>(w.get(), x, y, z);
    }

    private List<Location<World>> stringsToLocArray(List<String> strings) {
        List<Location<World>> locs = new ArrayList<>();
        for (String string : strings) {
            locs.add(stringToLoc(string));
        }
        return locs;
    }
}