package com.ivan.simplemediaplayer.domain;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * @author: Ivan Vigoss
 * Date: 13-9-27
 * Time: AM11:01
 */
public class Media implements Serializable {

    public final static int MEDIA_TYPE_VIDEO = 1;
    public final static int MEDIA_TYPE_AUDIO = 2;
    public final static int MEDIA_TYPE_IMAGE = 3;
    public final static int MEDIA_TYPE_DIR = 4;
    public final static int MEDIA_TYPE_OTHER = 5;

    private String displayName;

    private String path;

    private int duration;


    private int mediaType;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }
}
