package com.example.android.notepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * 工具类：根据用户的主题偏好提供颜色配置，确保不同主题下的文字对比度。
 */
public final class ThemeHelper {

    private ThemeHelper() { }

    private static SharedPreferences prefs(Context context) {
        return SettingsActivity.getPreferences(context);
    }

    public static boolean isDarkTheme(Context context) {
        String theme = prefs(context).getString("theme", "Light");
        return "Dark".equalsIgnoreCase(theme);
    }

    public static int getPrimaryTextColor(Context context) {
        return isDarkTheme(context) ? Color.WHITE : Color.parseColor("#212121");
    }

    public static int getSecondaryTextColor(Context context) {
        return isDarkTheme(context) ? Color.parseColor("#E0E0E0") : Color.parseColor("#616161");
    }

    public static int getListBackgroundColor(Context context) {
        return isDarkTheme(context) ? Color.parseColor("#101010") : Color.parseColor("#FFFFFF");
    }

    public static int getCardBackgroundColor(Context context) {
        return isDarkTheme(context) ? Color.parseColor("#1E1E1E") : Color.parseColor("#FFFFFF");
    }

    public static int getToolbarBackgroundColor(Context context) {
        return isDarkTheme(context) ? Color.parseColor("#1C1C1C") : Color.parseColor("#F5F5F5");
    }

    /**
     * 根据背景色计算适合的文字颜色，保证对比度。
     */
    public static int getContrastingTextColor(int backgroundColor) {
        double luminance = (0.299 * Color.red(backgroundColor) +
                0.587 * Color.green(backgroundColor) +
                0.114 * Color.blue(backgroundColor)) / 255;
        return luminance > 0.6 ? Color.parseColor("#212121") : Color.WHITE;
    }
}

