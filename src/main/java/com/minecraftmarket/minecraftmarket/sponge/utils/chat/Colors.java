package com.minecraftmarket.minecraftmarket.sponge.utils.chat;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;

public class Colors {
    public static Text color(String msg) {
        Text.Builder builder = Text.builder();

        while (!msg.isEmpty()) {
            TextColor color = null;
            List<TextStyle> format = new ArrayList<>();
            String s;

            while (color == null && getNextFormat(msg).equals("color")) {
                color = getColor(msg.charAt(1));
                msg = msg.substring(2);
            }

            while (getNextFormat(msg).equals("format")) {
                TextStyle style = getFormat(msg.charAt(1));

                assert style != null;
                if (style.equals(TextStyles.RESET)) {
                    color = null;
                }

                if (!format.contains(style)) {
                    format.add(style);
                }

                msg = msg.substring(2);
            }


            int index = getNextFormatIndex(msg);
            if (index > 0) {
                s = msg.substring(0, index);
                msg = msg.substring(index);
            } else {
                s = msg;
                msg = "";
            }

            addFormats(builder, color, format, s);
        }

        return builder.build();
    }

    public static List<Text> colorList(List<String> list) {
        List<Text> newList = new ArrayList<>();
        for (String msg : list) {
            newList.add(color(msg));
        }
        return newList;
    }

    private static void addFormats(Text.Builder builder, TextColor color, List<TextStyle> styles, String s) {
        if (color != null) {
            if (styles.size() == 0) {
                builder.append(Text.of(color, s));
            }
            if (styles.size() == 1) {
                builder.append(Text.of(color, styles.get(0), s));
            }
            if (styles.size() == 2) {
                builder.append(Text.of(color, styles.get(0), styles.get(1), s));
            }
            if (styles.size() == 3) {
                builder.append(Text.of(color, styles.get(0), styles.get(1), styles.get(2), s));
            }
            if (styles.size() == 4) {
                builder.append(Text.of(color, styles.get(0), styles.get(1), styles.get(2), styles.get(3), s));
            }
            if (styles.size() > 4) {
                builder.append(Text.of(color, styles.get(0), styles.get(1), styles.get(2), styles.get(3), styles.get(4), s));
            }
        } else {
            if (styles.size() == 0) {
                builder.append(Text.of(s));
            }
            if (styles.size() == 1) {
                builder.append(Text.of(styles.get(0), s));
            }
            if (styles.size() == 2) {
                builder.append(Text.of(styles.get(0), styles.get(1), s));
            }
            if (styles.size() == 3) {
                builder.append(Text.of(styles.get(0), styles.get(1), styles.get(2), s));
            }
            if (styles.size() == 4) {
                builder.append(Text.of(styles.get(0), styles.get(1), styles.get(2), styles.get(3), s));
            }
            if (styles.size() > 4) {
                builder.append(Text.of(styles.get(0), styles.get(1), styles.get(2), styles.get(3), styles.get(4), s));
            }
        }
    }

    private static int getNextFormatIndex(String s) {
        int indexFormat = s.indexOf('&');

        if (indexFormat == -1) {
            return -1;
        }

        char c = s.charAt(indexFormat + 1);
        if (getColor(c) != null) {
            return indexFormat;
        }

        if (getFormat(c) != null) {
            return indexFormat;
        }

        indexFormat = getNextFormatIndex(s.substring(indexFormat + 1));

        int indexNewLine = s.indexOf("\\n");

        if (indexFormat > indexNewLine) {
            return indexFormat;
        } else {
            return indexNewLine;
        }
    }

    private static String getNextFormat(String s) {
        if (s.charAt(0) != '&') {
            return "text";
        }
        char c = s.charAt(1);

        if (getColor(c) != null) {
            return "color";
        }

        if (getFormat(c) != null) {
            return "format";
        }

        return "text";
    }

    private static TextColor getColor(char c) {
        switch (c) {
            case '0':
                return TextColors.BLACK;
            case '1':
                return TextColors.DARK_BLUE;
            case '2':
                return TextColors.DARK_GREEN;
            case '3':
                return TextColors.DARK_AQUA;
            case '4':
                return TextColors.DARK_RED;
            case '5':
                return TextColors.DARK_PURPLE;
            case '6':
                return TextColors.GOLD;
            case '7':
                return TextColors.GRAY;
            case '8':
                return TextColors.DARK_GRAY;
            case '9':
                return TextColors.BLUE;
            case 'a':
                return TextColors.GREEN;
            case 'b':
                return TextColors.AQUA;
            case 'c':
                return TextColors.RED;
            case 'd':
                return TextColors.LIGHT_PURPLE;
            case 'e':
                return TextColors.YELLOW;
            case 'f':
                return TextColors.WHITE;
            default:
                return null;
        }
    }

    private static TextStyle getFormat(char c) {
        switch (c) {
            case 'k':
                return TextStyles.OBFUSCATED;
            case 'l':
                return TextStyles.BOLD;
            case 'm':
                return TextStyles.STRIKETHROUGH;
            case 'n':
                return TextStyles.UNDERLINE;
            case 'o':
                return TextStyles.ITALIC;
            case 'r':
                return TextStyles.RESET;
            default:
                return null;
        }
    }
}