package com.ivan.simplemediaplayer.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * @author: Ivan Vigoss
 * Date: 13-10-17
 * Time: PM4:22
 */
public class FileMaskUtils {

    private final static int MASK_BYTE_LENGTH = 50;

    //用做混淆的数组
    private final static byte[] MASK_BYTES = new byte[MASK_BYTE_LENGTH];

    static {
        Arrays.fill(MASK_BYTES, (byte) 0);
    }

    public static void decodeFile(String filePath) throws IOException {
        RandomAccessFile accessFile = new RandomAccessFile(filePath, "rwd");
        final long fileLength = accessFile.length();
        final long tailOffset = fileLength - MASK_BYTE_LENGTH * 2;
        //read head bytes
        accessFile.seek(0);
        byte[] headBytes = new byte[MASK_BYTE_LENGTH];
        accessFile.readFully(headBytes);

        //read mask bytes in tail
        accessFile.seek(tailOffset);
        byte[] tailBytes = new byte[MASK_BYTE_LENGTH];
        accessFile.readFully(tailBytes);

        //if this file has been encoded
        if (Arrays.equals(MASK_BYTES, headBytes) && Arrays.equals(MASK_BYTES, tailBytes)) {
            accessFile.seek(fileLength - MASK_BYTE_LENGTH);
            byte[] realHeadBytes = new byte[MASK_BYTE_LENGTH];
            accessFile.readFully(realHeadBytes);

            //cut tail bytes
            accessFile.seek(0);
            accessFile.setLength(tailOffset);

            //replace with real head
            accessFile.seek(0);
            accessFile.write(realHeadBytes);

        }

        accessFile.close();

    }


    public static void encodeFile(String filePath) throws IOException {
        RandomAccessFile accessFile = new RandomAccessFile(filePath, "rwd");

        accessFile.seek(0);//seek to head
        byte[] headBytes = new byte[MASK_BYTE_LENGTH];
        accessFile.readFully(headBytes);

        //replace head to mask
        accessFile.seek(0);
        accessFile.write(MASK_BYTES);

        //append mask and head bytes
        accessFile.seek(accessFile.length());
        accessFile.write(MASK_BYTES);
        accessFile.write(headBytes);

        accessFile.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("encode start");
        encodeFile("/Users/ivan/Movies/video1.mp4");
        System.out.println("encode finish");

        System.out.println("dncode start");
        decodeFile("/Users/ivan/Movies/video1.mp4");
        System.out.println("dncode finish");
    }

}
