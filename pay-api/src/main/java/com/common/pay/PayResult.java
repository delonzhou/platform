package com.common.pay;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class PayResult {

    private int status;

    private String orderCode;

    private String tradeNo;

    private Date payTime;

    private String desc;

    private BigDecimal money;

    private int payType;

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    private Map<String, String> rawData;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Map<String, String> getRawData() {
        return rawData;
    }

    public void setRawData(Map<String, String> rawData) {
        this.rawData = rawData;
    }

    @JsonIgnoreType
    public static class PayStatus {
        //处理中
        public static final int process = 0;
        //支付成功
        public static final int success = 1;
        //支付失败
        public static final int fail = 2;
        //交易关闭 交易退款或交易取消
        public static final int close = 3;
        //交易异常
        public static final int exception = 4;
    }
}
