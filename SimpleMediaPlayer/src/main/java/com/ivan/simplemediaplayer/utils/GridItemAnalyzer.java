package com.ivan.simplemediaplayer.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Pair;

/**
 * @author: Ivan Vigoss
 * Date: 13-10-16
 * Time: AM11:28
 */
public class GridItemAnalyzer {
    private Context mContext;
    private int width;
    private int height;
    public final static int GRID_PADDING = 8;

    private final static float SCREEN_16_9 = 1.777777F;
    private final static float SCREEN_4_3 = 1.333333F;

    public GridItemAnalyzer(Context mContext) {
        this.mContext = mContext;
        DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    /**
     * 获得图片在横屏或者竖屏情况下的宽度以及每一列的数目
     * first--> iconWidth
     * second--> columns
     *
     * @return
     */
    public Pair<Integer, Integer> getIconConfig() {
        GridColumns gridColumns = getGridColumns();
        int iconWidth;
        int columns;
        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columns = gridColumns.getLandscapeColumns();
            iconWidth = getIconWidth(width, columns);
        } else {
            columns = gridColumns.getPortraitColumns();
            iconWidth = getIconWidth(height, columns);
        }

        return Pair.create(iconWidth, columns);
    }

    private int getIconWidth(int screenWidth, int columns) {
        int paddingSpace = GRID_PADDING * (columns + 1);
        return (screenWidth - paddingSpace) / columns;
    }

    private GridColumns getGridColumns() {
        if (width == 800 && height == 480) {
            return GridColumns.SCREEN_800_480;
        } else if (width == 1024 && height == 600) {
            return GridColumns.SCREEN_1024_600;
        } else if (width == 1280 && height == 800) {
            return GridColumns.SCREEN_1280_800;
        } else if (width == 1920 && height == 1200) {
            return GridColumns.SCREEN_1920_1200;
        } else if (width == 1024 && height == 768) {
            return GridColumns.SCREEN_1024_768;
        } else if (width == 2048 && height == 1536) {
            return GridColumns.SCREEN_2048_1536;
        } else if (isScreen16_9()) {
            return GridColumns.SCREEN_1024_600;
        } else {
            return GridColumns.SCREEN_1024_768;
        }

    }

    private boolean isScreen16_9() {
        final float widthFloat = width;
        final float heightFloat = height;

        float result = widthFloat / heightFloat;
        if (result >= SCREEN_16_9) {
            return true;
        } else if (result < SCREEN_16_9 && result > SCREEN_4_3) {
            float to_16_9 = SCREEN_16_9 - result;
            float to_4_3 = SCREEN_4_3 - result;
            return to_16_9 < to_4_3;
        } else {
            return false;
        }

    }


    private enum GridColumns {
        SCREEN_800_480(3, 2),
        SCREEN_1024_600(5, 3),
        SCREEN_1280_800(6, 4),
        SCREEN_1920_1200(6, 4),
        SCREEN_1024_768(5, 4),
        SCREEN_2048_1536(5, 4);

        private int landscapeColumns;

        private int portraitColumns;

        GridColumns(int landscapeColumns, int portraitColumns) {
            this.landscapeColumns = landscapeColumns;
            this.portraitColumns = portraitColumns;
        }

        public int getLandscapeColumns() {
            return landscapeColumns;
        }

        public int getPortraitColumns() {
            return portraitColumns;
        }
    }
}
