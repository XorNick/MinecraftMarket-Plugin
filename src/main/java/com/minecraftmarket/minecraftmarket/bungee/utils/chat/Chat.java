package com.minecraftmarket.minecraftmarket.bungee.utils.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Chat {
    public static BaseComponent[] toComponent(String msg) {
        return TextComponent.fromLegacyText(msg);
    }

    public static String toString(BaseComponent... baseComponents) {
        return TextComponent.toLegacyText(baseComponents);
    }
}