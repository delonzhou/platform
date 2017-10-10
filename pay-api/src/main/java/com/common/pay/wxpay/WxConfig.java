package com.common.pay.wxpay;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.InputStream;

public class WxConfig implements WXPayConfig{

    private String AppID;

    private String MchID;

    private String key;

    private int httpConnectTimeoutMs;
    private int httpReadTimeoutMs;

    private InputStream certStream;

    @Override
    public InputStream getCertStream() {
        return certStream;
    }

    public void setCertStream(InputStream certStream) {
        this.certStream = certStream;
    }

    @Override
    public String getAppID() {
        return AppID;
    }

    public void setAppID(String appID) {
        AppID = appID;
    }

    @Override
    public String getMchID() {
        return MchID;
    }

    public void setMchID(String mchID) {
        MchID = mchID;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    public void setHttpConnectTimeoutMs(int httpConnectTimeoutMs) {
        this.httpConnectTimeoutMs = httpConnectTimeoutMs;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    public void setHttpReadTimeoutMs(int httpReadTimeoutMs) {
        this.httpReadTimeoutMs = httpReadTimeoutMs;
    }
}
