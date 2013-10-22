package com.ivan.simplemediaplayer.provider;

import android.os.Environment;

import com.ivan.simplemediaplayer.domain.Media;

import junit.framework.Assert;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Ivan Vigoss
 * Date: 13-9-27
 * Time: AM11:08
 */
public class VideoService {

    private VideoService() {
    }

    public final static File HOME_DIR = Environment.getExternalStorageDirectory();

    public static List<Media> loadMedias(String basePath, String path) {
        return listMedias(getTargetDir(basePath, path), true);
    }

    private static File getTargetDir(String basePath, String path) {
        File dir;
        if (path != null && !path.equals("")) {
            dir = new File(basePath, path);
        } else {
            dir = new File(basePath);
        }
        return dir;
    }

    public static List<Media> loadMp4Medias(String basePath, String path) {
        return listMedias(getTargetDir(basePath, path), false);
    }

    private static List<Media> listMedias(File dir, final boolean includeDir) {
        File[] dirs = null;
        if (includeDir) {
            dirs = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden();
                }
            });
        }

        File[] mp4Files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() || pathname.isHidden()) {
                    return false;
                }
                String fileName = pathname.getName();
                if (fileName.contains(".")) {
                    String suffix = fileName.substring(fileName.lastIndexOf("."));
                    return suffix.equalsIgnoreCase(".mp4");
                }
                return false;
            }
        });

        List<Media> result = new ArrayList<Media>();
        //first add dirs
        if (dirs != null && dirs.length > 0) {
            Arrays.sort(dirs);
            for (File file : dirs) {
                Media media = dirToMedia(file);
                if (media != null) {
                    result.add(media);
                }
            }
        }

        if (mp4Files != null && mp4Files.length > 0) {
            //sort
            Arrays.sort(mp4Files);
            for (File mp4File : mp4Files) {
                Media media = fileToMedia(mp4File);
                if (media != null) {
                    result.add(media);
                }
            }
        }

        return result;
    }

    private static Media dirToMedia(File dir) {
        if (!dir.isDirectory()) return null;
        Media media = new Media();
        media.setDisplayName(dir.getName());
        media.setMediaType(Media.MEDIA_TYPE_DIR);
        media.setPath(dir.getPath());
        return media;
    }

    private static Media fileToMedia(File mp4File) {
        if (mp4File.isFile()) {
            Media media = new Media();
            media.setMediaType(Media.MEDIA_TYPE_VIDEO);
            media.setDisplayName(mp4File.getName());
            media.setPath(mp4File.getPath());
            return media;
        }
        return null;
    }

}
