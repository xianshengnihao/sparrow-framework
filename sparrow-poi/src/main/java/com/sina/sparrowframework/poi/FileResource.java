package com.sina.sparrowframework.poi;

import com.sina.sparrowframework.tools.utility.Assert;
import com.sina.sparrowframework.tools.utility.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;

/**
 * 这个类是 {@link Resource} 这个类的特征是 当 {@link #getInputStream()} 关闭后, 文件会自动删除
 * 其它部分与 {@link FileSystemResource} 相似
 *
 * @see FileSystemResource
 */
public class FileResource implements Resource {

    private final File file;

    private final String path;

    public FileResource(File file) {
        Assert.notNull(file, "file required");
        Assert.isTrue(file.exists(), "file not exits");
        this.file = file;
        this.path = StringUtils.cleanPath(file.getPath());
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public URL getURL() throws IOException {
        return file.toURI().toURL();
    }

    @Override
    public URI getURI() throws IOException {
        return file.toURI();
    }

    @Override
    public File getFile() throws IOException {
        assertFileExists();
        return file;
    }

    @Override
    public long contentLength() throws IOException {
        assertFileExists();
        return file.length();
    }

    @Override
    public long lastModified() throws IOException {
        assertFileExists();
        return file.lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        String pathToUse = StringUtils.applyRelativePath(StringUtils.cleanPath(file.getPath()), relativePath);
        return new FileSystemResource(pathToUse);
    }

    @Override
    public String getFilename() {
        return file.getName();
    }

    @Override
    public String getDescription() {
        return "file [" + this.file.getAbsolutePath() + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        assertFileExists();
        return new FileInputStream(file) {

            @Override
            public void close() throws IOException {
                super.close();
                FileUtils.deleteFile(FileResource.this.file);
            }
        };
    }

    /**
     * This implementation opens a FileChannel for the underlying file.
     *
     * @see FileChannel
     */
    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        try {
            return FileChannel.open(this.file.toPath(), StandardOpenOption.READ);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }


    /**
     * This implementation compares the underlying File references.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof FileResource && this.path.equals(((FileResource) obj).path)));
    }

    @Override
    public String toString() {
        return String.format("%s,在 input stream close 后自动删除", file.getAbsolutePath());
    }

    private void assertFileExists() throws IOException {
        if (!file.exists()) {
            throw new IOException(String.format("file[%s] has deleted", file.getAbsolutePath()));
        }
    }
}
