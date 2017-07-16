package com.minecraftmarket.minecraftmarket.common.utils;

public class Utils {
    public static boolean isNullOrEmpty(Object input) {
        return input == null || (input instanceof String && ((String) input).isEmpty());
    }

    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int getInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean isFloat(String input) {
        try {
            Float.parseFloat(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static float getFloat(String input) {
        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static double getDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int roundDown(int n, int m) {
        return n >= 0 ? (n / m) * m : ((n - m + 1) / m) * m;
    }

    public static int roundUp(int n, int m) {
        return n >= 0 ? ((n + m - 1) / m) * m : (n / m) * m;
    }
}