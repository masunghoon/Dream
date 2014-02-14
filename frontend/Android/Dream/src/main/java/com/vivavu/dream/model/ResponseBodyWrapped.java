package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuja on 14. 2. 14.
 */
public class ResponseBodyWrapped<T> {
    @SerializedName("status")
    private String status;
    @SerializedName("reason")
    private String reason;

    @SerializedName("data")
    private T data;

    public ResponseBodyWrapped(String status, String reason, T data) {
        this.status = status;
        this.reason = reason;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
