package com.sina.sparrowframework.tools.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.MonthDay;
import java.time.YearMonth;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * created  on 07/03/2018.
 */
public abstract class FileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    private static final Pattern FILE_PREFIX_PATTERN = Pattern.compile("^.+(\\(\\d+\\))$");


    protected FileUtils() {

    }

    public static File createTempFile() throws IOException {
        return createTempFile(null);
    }

    /**
     * @param extension 文件扩展名,如: txt,zip,或为 null ,则无
     */
    public static File createTempFile(String extension) throws IOException {

        String fileName = UUID.randomUUID().toString();
        if (StrToolkit.hasText(extension)) {
            fileName = fileName + "." + extension;
        }
        return createTempFileWithName(fileName);
    }


    public static File createTempFileWithName(String fileName) throws IOException {
        File directory, file;
        directory = new File(System.getProperty("java.io.tmpdir"), "e_finance_temp_i");
        file = new File(directory, fileName);
        createIfNotExists(file);
        return file;
    }


    /**
     * 获取临时目录.这个目录将跟日期有关
     */
    public static File getTempDir() {
        String dirName = "temp-space/"
                + YearMonth.now().format(TimeUtils.YEAR_MONTH_FORMATTER)
                + "/" + MonthDay.now().format(TimeUtils.MONTH_DAY_FORMATTER);
        File directory = new File(SystemUtils.getTempDir(), dirName);

        if (!directory.exists() && directory.mkdirs()) {
            LOG.trace("create temp dir {}", directory.getAbsolutePath());
        }
        return directory;
    }

    public static void createIfNotExists(File file) throws IOException {

        if (file.exists()) {
            return;
        }
        File dir = file.getParentFile();
        if (!dir.exists() && dir.mkdirs()) {
            LOG.trace("file[{}]create success", dir.getAbsolutePath());

        }
        if (!file.exists() && file.createNewFile()) {
            LOG.trace("file[{}]create success", file.getAbsolutePath());
        }
    }

    /**
     * 若文件名存在 则 追加序号作为文件名(如:temp.txt 追加序号为 temp(1).txt),直到不存在才创建文件
     *
     * @return 若文件名已存在则返回新的 {@link File} ,若不存在则返回原 {@link File}
     */
    public static File createNewNamedFileIfExists(final File file) throws IOException {

        final String fileNamePrefix = StrToolkit.getFileNamePrefix(file.getName());
        final String actualPrefix = parseFileNamePrefix(fileNamePrefix);
        final String extension = StrToolkit.getFilenameExtension(file.getName());
        String prefix, newName;

        File newFile = file;
        for (int i = getFileNameNumber(fileNamePrefix); ; i++) {
            if (newFile.exists()) {
                prefix = String.format("%s(%s)", actualPrefix, i);
                newName = String.format("%s.%s", prefix, extension);
                newFile = new File(file.getParent(), newName);
            } else {
                createIfNotExists(newFile);
                break;
            }

        }
        return newFile;
    }

    private static String parseFileNamePrefix(String fileName) {
        String prefix = fileName;
        if (FILE_PREFIX_PATTERN.matcher(fileName).matches()) {
            int index;
            index = fileName.lastIndexOf('(');
            if (index > 0) {
                prefix = fileName.substring(0, index);
            }
        }
        return prefix;
    }

    private static int getFileNameNumber(String fileNamePrefix) {
        if (!fileNamePrefix.endsWith(")")) {
            return 1;
        }
        Integer num, index;
        index = fileNamePrefix.lastIndexOf('(');
        if (index <= 0) {
            return 1;
        }
        try {
            num = Integer.parseInt(
                    fileNamePrefix.substring(index + 1, fileNamePrefix.length() - 1)
            );
            num++;
        } catch (NumberFormatException e) {
            num = 1;
        }

        return num;
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists() && file.delete()) {
            LOG.debug("delete file {}", file.getAbsolutePath());
        }
    }

    /**
     * 为避免同一目录重复,为文件名加上序号
     *
     * @param file 可能重复的文件(文件必须是不存在的)
     * @return 一个与 file 在同目录不重名且不存在的文件对象
     * @throws IOException 当序号增长大过一定上限时抛出.
     */
    public static File createFileAvoidDuplicate(final File file) throws IOException {
        Assert.isTrue(file.isFile(), "param file must be a file");
        if (!file.exists()) {
            return file;
        }

        final File dir = file.getParentFile();
        final String prefix = StrToolkit.getFileNamePrefix(file.getName());
        final String extension = StrToolkit.getFilenameExtension(file.getName());
        final int max = 1000;

        File newFile;
        for (int i = 2; i < max; i++) {
            newFile = new File(dir, prefix + "-" + i + "." + extension);
            if (!newFile.exists()) {
                return newFile;
            }
        }
        throw new IOException(String.format("file duplicate count great than %s", max));
    }


    /**
     * 为避免同一目录重复,为目录名加上序号
     *
     * @param directory 可能重复的目录(目录必须是不存在的)
     * @return 一个与 directory 在同目录不重名且不存在的目录对象
     * @throws IOException 当序号增长大过一定上限时抛出.
     */
    public static File createDirectoryAvoidDuplicate(final File directory) throws IOException {
        Assert.isTrue(directory.isDirectory(), "param directory must be a directory");
        if (!directory.exists() || ObjectToolkit.isEmpty(directory.list())) {
            return directory;
        }

        final File dir = directory.getParentFile();
        final String prefix = StrToolkit.getFileNamePrefix(directory.getName());
        final int max = 10;

        File newDir;
        for (int i = 2; i < max; i++) {
            newDir = new File(dir, prefix + "-" + i);
            if (!newDir.exists() || ObjectToolkit.isEmpty(newDir.list())) {
                return newDir;
            }
        }
        throw new IOException(String.format("directory duplicate count great than %s", max));
    }


}
