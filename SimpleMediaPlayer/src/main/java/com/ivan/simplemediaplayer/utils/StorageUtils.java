package com.ivan.simplemediaplayer.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Ivan Vigoss
 * Date: 13-10-21
 * Time: PM6:11
 */
public class StorageUtils {

    public static List<String> getSystemFileDiskPath() {
        List<String> fileDiskList = new ArrayList<String>();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) {
                    continue;
                }
                if (line.contains("firmware")) {
                    continue;
                }
                if (line.contains("asec")) {
                    continue;
                }
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        String diskPath = columns[1];
                        fileDiskList.add(diskPath);
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        String diskPath = columns[1];
                        fileDiskList.add(diskPath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return fileDiskList;
        }
        return fileDiskList;
    }

    public static void main(String[] args) {
        List<String> list = getSystemFileDiskPath();
        for (String s : list) {
            System.out.println(s);
        }
    }
}
