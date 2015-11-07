package com.libertacao.libertacao.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataUtils {
    private static final int SMALL_IMAGE_HEIGHT = 128;
    private static final int SMALL_IMAGE_WIDTH = 512;
    private DataUtils() {
        // Do not allow instantiation
    }

    /**
     * Get full file data
     * @param path file path
     * @return byte array containing file data
     * @throws IOException if could not get the data
     */
    @SuppressWarnings("unused")
    public static byte[] getFileData(String path) throws IOException {
        byte[] data;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;
        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }
        input_stream.close();
        return buffer.toByteArray();
    }

    /**
     * Get small file data
     * @param path file path
     * @return byte array containing file data
     * @throws IOException if could not get the data
     */
    public static byte[] getSmallFileData(String path) throws IOException {
        Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), SMALL_IMAGE_WIDTH, SMALL_IMAGE_HEIGHT);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        thumb.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
