package com.sina.sparrowframework.tools.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件或目录的工具类
 * created  on 2018/10/13.
 */
public abstract class CompressUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CompressUtil.class);

    public static final String ZIP = "zip";

    public static final String ZIP_EXTENSION = ".zip";


    /**
     * 解压过程中有文件名重复的处理策略
     */
    public enum DuplicateStrategy {
        /**
         * 遇到文件名重复 则操持两者
         */
        KEEP,

        /**
         * 遇到文件名重复 则抛出异常
         */
        ERROR,

        /**
         * 遇到文件名重复 则覆盖
         */
        COVER
    }


    /**
     * 以 zip 格式压缩 一个目录或文件.
     *
     * @param file 源目录或文件
     * @return 压缩后的 zip 格式文件
     * @throws IOException 压缩出错
     */
    public static File compressToZip(File file) throws IOException {
        File temp = FileUtils.createTempFile(ZIP);
        compressToZip(file, temp);
        return temp;
    }


    /**
     * 以 zip 格式压缩 一个目录或文件. 若 target 为 /data0/myfile.zip 则 解压后 会在 目标目录内生成一个 myfile 目录.
     *
     * @param source 源目录或文件
     * @param target 压缩后的 zip 格式文件 位置(如: /data0/myfile.zip),若不存在则创建
     * @throws IOException 压缩出错
     */
    public static void compressToZip(File source, File target) throws IOException {
        prepareDirectory(target);

        if (!ObjectUtils.isEmpty(target.list())) {
            throw new IOException("target directory must be empty");
        }

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target))) {
            if (source.isFile()) {
                compressToZip(out, source, "");
            } else {
                String base = StrToolkit.stripFilenameExtension(target.getName());
                base = StrToolkit.getFilename(base) + "/";
                compressToZip(out, source, base);
            }
        }
    }

    /**
     * 以 zip 格式压缩 压缩多个文件或目录 . 解压后会在目标目录内展现出压缩前的结构,即不会创建 myfile 目录
     *
     * @param fileList 要被压缩的多个文件或者目录
     * @param target   压缩后的 zip 格式文件 位置(如: /data0/myfile.zip),若不存在则创建
     * @throws IOException 压缩出错
     */
    public static void compressToZIP(List<File> fileList, File target) throws IOException {
        final boolean exists = target.exists();
        prepareFile(target);
        if (ObjectToolkit.isEmpty(fileList)) {
            return;
        }

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target))) {

            for (File source : fileList) {
                Assert.isTrue(source.exists(), "file %s not exits", source.getAbsolutePath());
                compressToZip(out, source, "");
            }
        } catch (Exception e) {
            if (!exists) {
                FileUtils.deleteFile(target);
            }
        }
    }


    /**
     * 解压 zip 格式的文件,等价于 {@link #unCompressZip(File, File, DuplicateStrategy ERROR)}
     *
     * @param zip       zip 文件
     * @param targetDir 解压的目标目录
     */
    public static void unCompressZip(File zip, File targetDir) throws IOException {
        unCompressZip(zip, targetDir, DuplicateStrategy.ERROR);
    }

    /**
     * 解压 zip 格式的文件,
     *
     * @param zip               zip 文件
     * @param targetDir         解压的目标目录
     * @param duplicateStrategy 解压单文件结构重名时的处理策略
     */
    public static void unCompressZip(File zip, File targetDir, DuplicateStrategy duplicateStrategy)
            throws IOException {
        Assert.isFalse(targetDir.isFile(), "targetDir not directory");
        if (!targetDir.exists() && targetDir.mkdirs()) {
            LOG.debug("create targetDir {}", targetDir.getAbsolutePath());
        }

        try (ZipFile zipFile = new ZipFile(zip)) {
            LOG.debug("zipFile size : {}", zipFile.size());

            if (zipFile.size() == 1) {
                // 解压单个文件结构
                unCompressSingleZip(zipFile, targetDir, duplicateStrategy);
            } else {
                // 解压一个目录结构或多个文件
                unCompressDirectory(zipFile, targetDir, duplicateStrategy);
            }

        }
    }

    /**
     * @param base 以 {@code /} 结束
     */
    private static void compressToZip(ZipOutputStream out, File file, String base) throws IOException {
        if (file.isFile()) {
            compressFile(out, file, base);
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            if (files.length == 0) {
                out.putNextEntry(new ZipEntry(base));
            } else {
                // 压缩目录
                compressDirectoryToZip(out, files, base);
            }

        }
    }

    /**
     * 压缩同目录下的多个文件
     *
     * @param files 同一目录下的文件
     * @param base  base name
     * @throws IOException 压缩出错
     */
    private static void compressDirectoryToZip(ZipOutputStream out, File[] files, String base) throws IOException {
        String childPath;
        for (File f : files) {
            childPath = base;
            if (f.isDirectory()) {
                childPath = base + (StrToolkit.getFilename(f.getName()) + "/");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("compress childPath {}", childPath);
            }
            compressToZip(out, f, childPath);
        }
    }

    /**
     * 压缩目录结构中的一个文件
     *
     * @param source 被压缩文件
     * @param base   以 {@code /} 结束 or empty
     */
    private static void compressFile(ZipOutputStream out, File source, String base) throws IOException {
        try (FileInputStream in = new FileInputStream(source)) {
            out.putNextEntry(new ZipEntry(base + source.getName()));
            if (source.isFile()) {
                StreamUtils.copy(in, out);
            }
        }
    }


    /**
     * 解压一个目录
     */
    private static void unCompressDirectory(ZipFile zipFile, final File targetDir, DuplicateStrategy duplicateStrategy)
            throws IOException {
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        File file;
        for (ZipEntry zipEntry; enumeration.hasMoreElements(); ) {
            zipEntry = enumeration.nextElement();
            file = new File(targetDir, zipEntry.getName());
            // 准备 file
            prepareFile(file);
            // 写文件
            StreamUtils.copyToFileAndClose(zipFile.getInputStream(zipEntry), file);
        }
    }

    private static void prepareFile(File file) throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists() && dir.mkdirs()) {
            LOG.debug("create directory {}", dir.getAbsolutePath());
        }
        if (!file.exists() && file.createNewFile()) {
            LOG.debug("create file {}", file.getAbsolutePath());
        }
    }


    private static void prepareDirectory(File directory) {
        if (!directory.exists() && directory.mkdirs()) {
            LOG.debug("create directory {}", directory.getAbsolutePath());
        }
    }

    /**
     * 为 file 准备解压目录
     */
    private static File prepareUnCompressRoot(File file, File targetDir, ZipFile zipFile,
                                              DuplicateStrategy duplicateStrategy) throws IOException {
        File dir = file.getParentFile();
        if (targetDir.equals(dir)) {
            // 到这里则说明是多个文件的压缩结构.
            // 则先创建一个顶层结构
            dir = new File(targetDir, StrToolkit.getFileNamePrefix(zipFile.getName()));
            if (dir.exists()) {
                dir = doPrepareUnCompressDir(dir, duplicateStrategy);
            }
        } else {
            // 目录压缩结构
            if (dir.exists()) {
                dir = doPrepareUnCompressDir(dir, duplicateStrategy);
            } else if (dir.mkdirs()) {
                LOG.debug("create uncompression directory {} ", dir.getAbsolutePath());
            }
        }
        return dir;
    }


    /**
     * 根据 duplicateStrategy 准备目录
     */
    private static File doPrepareUnCompressDir(final File dir, DuplicateStrategy duplicateStrategy) throws IOException {
        File directory = dir;
        switch (duplicateStrategy) {
            case COVER:
                LOG.info("uncompress not empty directory:{}", dir.getAbsolutePath());
                break;
            case KEEP:
                directory = FileUtils.createDirectoryAvoidDuplicate(dir);
                if (!directory.exists() && directory.mkdirs()) {
                    LOG.debug("create directory to avoid duplicate,{}", dir.getAbsolutePath());
                }
                break;
            case ERROR:
                throw new IOException(String.format("directory not empty,%s", dir.getAbsoluteFile()));
            default:
                throw new IllegalArgumentException(
                        String.format("unknown duplicateStrategy[%s]", duplicateStrategy));
        }
        return directory;
    }

    // 解压单个 文件
    private static void unCompressSingleZip(ZipFile zipFile, File targetDir, DuplicateStrategy strategy)
            throws IOException {
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        if (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            // 根据 重复处理策略 创建 文件
            File file = createSingleFile(targetDir, zipEntry.getName(), strategy);
            // 准备文件
            prepareFile(file);
            StreamUtils.copyToFileAndClose(zipFile.getInputStream(zipEntry), file);
        }
    }

    // 根据 重复处理策略 创建 文件
    private static File createSingleFile(File targetDir, String name, DuplicateStrategy strategy) throws IOException {
        File file = new File(targetDir, name);
        if (!file.exists()) {
            return file;
        }

        switch (strategy) {
            case COVER:
                LOG.info("file name duplicate,cover file {}", file.getAbsolutePath());
                break;
            case KEEP:
                file = FileUtils.createFileAvoidDuplicate(file);
                break;
            case ERROR:
                throw new IOException(String.format("file name duplicate,%s", file.getAbsoluteFile()));
            default:
                throw new IllegalArgumentException(String.format("unknown DuplicateStrategy[%s]", strategy));
        }
        return file;
    }


}
