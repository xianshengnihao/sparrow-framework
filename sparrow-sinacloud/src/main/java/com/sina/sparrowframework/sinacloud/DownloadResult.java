package com.sina.sparrowframework.sinacloud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sina.sparrowframework.tools.utility.JsonUtils;
import org.springframework.lang.NonNull;

import java.io.InputStream;

/**
 * created  on 2018/10/12.
 */
public class DownloadResult extends CloudMeta {

    @NonNull
    @JsonIgnore
    private InputStream inputStream;



    @NonNull
    public InputStream getInputStream() {
        return inputStream;
    }

    public DownloadResult setInputStream(@NonNull InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }


    @Override
    public String toString() {
        return JsonUtils.writeToJson( this, true );
    }
}
