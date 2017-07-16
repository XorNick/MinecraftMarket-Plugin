package com.minecraftmarket.minecraftmarket.bukkit.utils.items;

import com.minecraftmarket.minecraftmarket.bukkit.utils.reflection.ReflectionUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.UUID;

public class SkullUtils {
    @SuppressWarnings("deprecation")
    public static void setSkullWithNonPlayerProfile(String skinURL, String name, Block skullBlock) {
        if (skullBlock.getState() instanceof Skull) {
            Skull skull = (Skull) skullBlock.getState();
            try {
                setSkullProfile(skull, getNonPlayerProfile(skinURL, name));
            } catch (Exception e) {
                e.printStackTrace();
            }
            skullBlock.getWorld().refreshChunk(skullBlock.getChunk().getX(), skullBlock.getChunk().getZ());
        }
    }

    private static void setSkullProfile(Skull skull, GameProfile someGameProfile) {
        ReflectionUtils.RefClass classCraftWorld = ReflectionUtils.getRefClass("{cb}.CraftWorld");
        ReflectionUtils.RefMethod methodGetHandle = classCraftWorld.getMethod("getHandle");
        Object world = methodGetHandle.of(skull.getWorld()).call();
        ReflectionUtils.RefClass classBlockPosition = ReflectionUtils.getRefClass("{nms}.BlockPosition");
        ReflectionUtils.RefConstructor constructorBlockPosition = classBlockPosition.getConstructor(int.class, int.class, int.class);
        ReflectionUtils.RefClass classWorldServer = ReflectionUtils.getRefClass("{nms}.WorldServer");
        ReflectionUtils.RefMethod methodGetTileEntity = classWorldServer.getMethod("getTileEntity", classBlockPosition.getRealClass());
        Object tileSkull = methodGetTileEntity.of(world).call(constructorBlockPosition.create(skull.getX(), skull.getY(), skull.getZ()));
        ReflectionUtils.RefClass classTileEntitySkull = ReflectionUtils.getRefClass("{nms}.TileEntitySkull");
        ReflectionUtils.RefMethod methodSetGameProfile = classTileEntitySkull.getMethod("setGameProfile", GameProfile.class);
        methodSetGameProfile.of(tileSkull).call(someGameProfile);
    }

    private static GameProfile getNonPlayerProfile(String skinURL, String name) {
        GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), name);
        newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinURL + "\"}}}")));
        return newSkinProfile;
    }
}