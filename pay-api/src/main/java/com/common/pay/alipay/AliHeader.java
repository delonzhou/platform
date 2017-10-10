package com.common.pay.alipay;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliHeader {

    private String gateWay;
    private String appId;
    private String privateKey;
    private String publicKey;
    private String dataType;
    private String charset;
    private String AliPublicKey;
    private String signType;
    private String returnUrl;
    private String notifyUrl;

    public AliHeader() {
    }

    public AliHeader(String gateWay, String appId, String privateKey, String publicKey, String dataType, String charset, String aliPublicKey, String signType) {
        this.gateWay = gateWay;
        this.appId = appId;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.dataType = dataType;
        this.charset = charset;
        this.AliPublicKey = aliPublicKey;
        this.signType = signType;
    }

    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getAliPublicKey() {
        return AliPublicKey;
    }

    public void setAliPublicKey(String aliPublicKey) {
        AliPublicKey = aliPublicKey;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Override
    public String toString() {
        return "AliPayHeader{" +
                "gateWay='" + gateWay + '\'' +
                ", appId='" + appId + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", dataType='" + dataType + '\'' +
                ", charset='" + charset + '\'' +
                ", AliPublicKey='" + AliPublicKey + '\'' +
                ", signType='" + signType + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                '}';
    }
}
