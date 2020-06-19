package com.sina.sparrowframework.poi;

import com.sina.sparrowframework.tools.struct.CodeEnum;
import com.sina.sparrowframework.tools.utility.Assert;
import com.sina.sparrowframework.tools.utility.TimeUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;
import com.sina.sparrowframework.poi.annotation.SheetHeader;
import com.sina.sparrowframework.poi.annotation.ExcelMeta;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * 将数据 导出 成 excel 文件的工具类
 * <p>
 * 本类不是线程安全的
 * </p>
 * created  on 2018/7/18.
 *
 * @see SheetHeader
 * @see ExcelMeta
 */
class DefaultExcelExporter implements ExcelExporter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExcelExporter.class);

    private static final String ACCEPT_ROW = "tasty.excel.exporter.acceptRow";

    private static final Pattern DATE_PATTERN = Pattern.compile("\\$\\{date\\}");

    private final Workbook workbook = new SXSSFWorkbook(excelAcceptRowCount());

    private final Map<Class<?>, SheetBean> classToSheetMap;

    private boolean workbookClosed = false;

    /**
     * 封装导出一个 sheet 所必要的数据.
     */
    private static class SheetBean {

        private final List<Header> headerList;

        private final Sheet sheet;

        // 从 1 开始 0 作为 sheet  header
        private int offset = 1;

        private SheetBean(List<Header> headerList, Sheet sheet) {
            this.headerList = headerList;
            this.sheet = sheet;
        }
    }


    /**
     * 用一表示 {@link SheetHeader}
     */
    private static class Header {

        private final String name;

        private final int index;

        private final String propName;

        Header(String name, int index, String propName) {
            this.name = name;
            this.index = index;
            this.propName = propName;
        }
    }

    private static int excelAcceptRowCount() {
        return 1000;
    }


    DefaultExcelExporter(List<Class<?>> dataClassList) {
        Assert.assertNotEmpty(dataClassList, "dataClassList must not empty");
        if (dataClassList.size() == 1) {
            Class<?> dataClass = dataClassList.get(0);
            this.classToSheetMap = Collections.singletonMap(dataClass, createSheetBean(dataClass));
        } else {
            Map<Class<?>, SheetBean> map = new HashMap<>((int) (dataClassList.size() / 0.75f));
            for (Class<?> dataClass : dataClassList) {
                map.put(dataClass, createSheetBean(dataClass));
            }
            this.classToSheetMap = Collections.unmodifiableMap(map);
        }
    }


    /**
     * 向 excel 加追加数据
     *
     * @param list 数据
     */
    @Override
    public <T> ExcelExporter appendData(final List<T> list, Class<T> elementClass) {
        SheetBean sheetBean = classToSheetMap.get(elementClass);

        Assert.notNull(sheetBean, () -> String.format("not found sheet of elementClass[%s]", elementClass));
        BeanWrapper wrapper;
        Object value;

        Row row;
        Cell cell;
        final Sheet sheet = sheetBean.sheet;
        final List<Header> headerList = sheetBean.headerList;
        int rowIndex = sheetBean.offset;

        for (T data : list) {
            wrapper = PropertyAccessorFactory.forBeanPropertyAccess(data);
            row = sheet.createRow(rowIndex);

            for (Header header : headerList) {
                cell = row.createCell(header.index);
                cell.setCellType(CellType.STRING);
                value = wrapper.getPropertyValue(header.propName);
                cell.setCellValue(convertToString(value));
            }
            rowIndex++;
        }
        sheetBean.offset += list.size();
        return this;
    }


    public HttpEntity<Resource> exportToHttpEntity() throws IOException {
        return exportToHttpEntity(UUID.randomUUID().toString());
    }

    @Override
    public HttpEntity<Resource> exportToHttpEntity(String excelName) throws IOException {
        return new HttpEntity<>(exportAsResource(), createHttpHeader(excelName));
    }

    @Override
    public Resource exportAsResource() throws IOException {
        if (workbookClosed) {
            throw new IOException("excel 文件已导出,不能重复导出.");
        }
        final File file = getExportExcelFile();
        try (FileOutputStream out = new FileOutputStream(file); Workbook w = workbook) {
            w.write(out);
            workbookClosed = true;
        }

        return new FileResource(file);
    }





    /*######################################### private method ################################################*/


    private HttpHeaders createHttpHeader(String excelName) {

        ZonedDateTime now = ZonedDateTime.now(TimeUtils.ZONE8);
        ContentDisposition disposition = ContentDisposition.builder("attachment")
                .creationDate(now)
                .modificationDate(now)
                .filename(createExcelFileName(excelName)
                        , StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(disposition);
        headers.setContentType(MediaType.valueOf("application/vnd.ms-excel"));
        return headers;
    }

    private String createExcelFileName(String excelName) {
        Assert.hasText(excelName, "excelName required");
        String excelFileName;

        final String extension = "." + XSSFWorkbookType.XLSX.getExtension();
        if (excelName.endsWith(extension)) {
            excelFileName = excelName;
        } else {
            excelFileName = excelName + extension;
        }
        return excelFileName;

    }

    private File getExportExcelFile() throws IOException {
        String fileName = UUID.randomUUID().toString() + "." + XSSFWorkbookType.XLSX.getExtension();
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);
        if (!file.exists() && file.createNewFile()) {
            LOG.info("创建临时文件:{}", file.getAbsolutePath());
        }
        return file;
    }

    private SheetBean createSheetBean(Class<?> dataClass) {
        SheetBean sheetBean = new SheetBean(
                createExcelHeader(dataClass),
                createExcelSheet(dataClass)
        );
        // 初始化 sheet 的 header
        initSheetHeader(sheetBean);
        return sheetBean;
    }

    private List<Header> createExcelHeader(Class<?> dataClass) {
        final Set<Integer> indexSet = new HashSet<>();
        List<Header> list = new ArrayList<>();
        ReflectionUtils.doWithFields(dataClass, fc -> {
            SheetHeader sheetHeader = AnnotationUtils.findAnnotation(fc, SheetHeader.class);
            if (sheetHeader == null) {
                return;
            }

            if (indexSet.contains(sheetHeader.index())) {
                throw new RuntimeException(String.format("%s %s.index() duplicate", dataClass.getName(),
                        SheetHeader.class.getName()));
            }
            list.add(
                    new Header(sheetHeader.value(), sheetHeader.index(), fc.getName())
            );
            indexSet.add(sheetHeader.index());
        });
        return Collections.unmodifiableList(list);
    }

    /**
     * 写一个 sheet 的 header
     */
    private void initSheetHeader(SheetBean sheetBean) {
        Row row = sheetBean.sheet.getRow(0);
        if (row == null) {
            row = sheetBean.sheet.createRow(0);
        }
        Cell cell;
        for (Header header : sheetBean.headerList) {
            cell = row.createCell(header.index);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(header.name);
        }
    }


    private Sheet createExcelSheet(Class<?> dataClass) {
        ExcelMeta meta = AnnotationUtils.findAnnotation(dataClass, ExcelMeta.class);
        String name;
        if (meta == null) {
            name = LocalDate.now().format(TimeUtils.DATE_FORMATTER);
        } else {
            name = meta.sheet();
            if (!StringUtils.hasText(name)) {
                name = DATE_PATTERN.matcher(meta.name()).replaceAll("");
            }
        }
        return workbook.createSheet(name);

    }


    private String convertToString(Object value) {
        String text;
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal) {
            BigDecimal decimal = ((BigDecimal) value);
            if (decimal.scale() < 2) {
                decimal = decimal.setScale(2, RoundingMode.HALF_UP);
            }
            text = decimal.toPlainString();
        } else if (value instanceof LocalDate) {
            text = ((LocalDate) value).format(TimeUtils.DATE_FORMATTER);
        } else if (value instanceof LocalDateTime) {
            text = ((LocalDateTime) value).format(TimeUtils.DATETIME_FORMATTER);
        } else if (value instanceof CodeEnum) {
            text = ((CodeEnum) value).display();
        } else {
            text = value.toString();
        }
        return text;
    }

}
