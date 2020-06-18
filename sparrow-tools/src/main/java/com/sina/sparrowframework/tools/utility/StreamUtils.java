package com.sina.sparrowframework.tools.utility;

import java.io.*;

/**
 * created  on 2018/10/12.
 */
public abstract class StreamUtils {

    public static final int BUFFER_SIZE = 4096;

    /**
     * 复制 source 的内容到一个临时文件,并关闭 source
     */
    public static File copyToFileAndClose(InputStream source) throws IOException {
        File tempFile = FileUtils.createTempFile();
        copyToFileAndClose(source, tempFile);
        return tempFile;
    }


    /**
     * 复制 source 的内容到 file,并关闭 source
     */
    public static File copyToFileAndClose(InputStream source, File file) throws IOException {
        try (InputStream in = source; FileOutputStream out = new FileOutputStream(file)) {
            copy(in, out);
        }
        return file;
    }

    public static void copyAndClose(File file, OutputStream out) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            copy(in, out);
        }
    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Leaves both streams open when done.
     *
     * @param in  the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");

        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }
}
