package com.sina.sparrowframework.log.converter;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author wxn
 * @date 2021/6/10 5:15 下午
 */
public class LogHttpServletRequest extends HttpServletRequestWrapper {

    private  byte[] requestBody;

    private String requestUri;

    public LogHttpServletRequest(HttpServletRequest request) {
        super(request);
        requestBody = this.getRequestBody(request).getBytes();
        requestUri = request.getRequestURI();
    }

    public String getRequestBody() {
        return new String(requestBody);
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() { return false; }
            @Override
            public boolean isReady() { return false;}
            @Override
            public void setReadListener(ReadListener readListener) {}
            @Override
            public int read(){
                return byteArrayInputStream.read();
            }
        };
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

    private String getRequestBody(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (
                InputStream inputStream = request.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
