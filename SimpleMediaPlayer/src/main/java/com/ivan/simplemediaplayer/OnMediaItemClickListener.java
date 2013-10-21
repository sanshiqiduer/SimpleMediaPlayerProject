package com.ivan.simplemediaplayer;

import android.view.LayoutInflater;
import android.view.View;

import com.ivan.simplemediaplayer.domain.Media;

/**
 * @author: Ivan Vigoss
 * Date: 13-10-13
 * Time: PM8:14
 */
public interface OnMediaItemClickListener {

    void onMediaItemClick(Media media, int position, String basePath, String oldPath);

    /**
     * 当Item为空时的默认View
     *
     * @return
     */
    View getEmptyView(LayoutInflater inflater);

}
