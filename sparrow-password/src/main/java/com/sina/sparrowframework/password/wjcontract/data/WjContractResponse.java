package com.sina.sparrowframework.password.wjcontract.data;

import java.io.Serializable;

/**
 * @Author liuyi
 * @Description
 * @Date 2021/11/8 14:28
 **/
public class WjContractResponse<T> implements Serializable {
    private static final long serialVersionUID = -2005035728645927292L;

    private T data;
    private Control control;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public static class Control {
        private Long error;
        private Long serverTime;
        private String message;
        private String detailMessage;
        private String reqId;

        public String getDetailMessage() {
            return detailMessage;
        }

        public void setDetailMessage(String detailMessage) {
            this.detailMessage = detailMessage;
        }

        public String getReqId() {
            return reqId;
        }

        public void setReqId(String reqId) {
            this.reqId = reqId;
        }

        public Long getError() {
            return error;
        }

        public void setError(Long error) {
            this.error = error;
        }

        public Long getServerTime() {
            return serverTime;
        }

        public void setServerTime(Long serverTime) {
            this.serverTime = serverTime;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
