package com.common.pay.alipay;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 目前只封装使用到的参数，后期可以在这里进行拓展即可
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliPayBody {

    //交易描述 F
    private String body;
    //交易标题 T
    private String subject;
    //商户单号 T
    private String out_trade_no;
    //有效时长 F
    private String timeout_express;//支付宝移动支付无time_expire字段，因材采用所有支付通用字段
    //订单金额0.00 T
    private String total_amount;
    //产品码 T
    private String product_code;

    public AliPayBody(String body, String subject, String out_trade_no, String timeout_express, String total_amount, String product_code) {
        this.body = body;
        this.subject = subject;
        this.out_trade_no = out_trade_no;
        this.timeout_express = timeout_express;
        this.total_amount = total_amount;
        this.product_code = product_code;
    }

    public AliPayBody() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTimeout_express() {
        return timeout_express;
    }

    public void setTimeout_express(String timeout_express) {
        this.timeout_express = timeout_express;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

}
