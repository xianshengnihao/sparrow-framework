package com.sina.sparrowframework.log.adaptor;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author wxn
 * @date 2021/6/10 5:15 下午
 */
public class LogHttpServletResponse extends HttpServletResponseWrapper {

    private final LogWrapperServletOutputStream wrapperServletOutputStream = new LogWrapperServletOutputStream();

    public LogHttpServletResponse(HttpServletResponse response) {
        super(response);
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return wrapperServletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(wrapperServletOutputStream);
    }
    /**
     * 获取 数组body
     *
     */
    public byte[] getBodyBytes() {
        return wrapperServletOutputStream.out.toByteArray();
    }

    /**
     * 获取 字符串body utf-8编码
     */
    public String getBodyString() {
        try {
            return wrapperServletOutputStream.out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "[UNSUPPORTED ENCODING]";
        }
    }

    /**
     * 将 body内容 重新赋值到 response 中
     * 由于stream 只读一次  需要重写到response中
     */
    public void copyToResponse() {
        try {
            getResponse().getOutputStream().write(getBodyBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class LogWrapperServletOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream out = new ByteArrayOutputStream();

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
        @Override
        public void write(int b) throws IOException {
            out.write(b);
        }
        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
        }
    }
}
