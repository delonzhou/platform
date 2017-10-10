package com.common.pay;


import com.common.pay.alipay.AliHeader;
import com.common.pay.alipay.AliPay;
import com.common.pay.wxpay.WxConfig;
import com.common.pay.wxpay.WxPay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Gmy
 * @version v0.0.1
 */
public class PayAPI {

    private PayAPI() {

    }

    private AliHeader ALI_PAY_HEADER;
    //---------------------------------
    private WxConfig WX_CONFIG;
    //-------------------------------
    private String notifyUrl;
    private String returnUrl;

    public static PayAPI instance() {
        return new PayAPI();
    }

    public PayAPI ali(String aliAppId,
                      String aliAppPrivateKey,
                      String aliAppPublicKey,
                      String aliPublicKey,
                      String aliDataType,
                      String aliSignType) {
        ALI_PAY_HEADER = new AliHeader();
        ALI_PAY_HEADER.setGateWay("https://openapi.alipay.com/gateway.do");
        ALI_PAY_HEADER.setAppId(aliAppId);
        ALI_PAY_HEADER.setPrivateKey(aliAppPrivateKey);
        ALI_PAY_HEADER.setAliPublicKey(aliAppPublicKey);
        ALI_PAY_HEADER.setPublicKey(aliPublicKey);
        ALI_PAY_HEADER.setDataType(aliDataType);
        ALI_PAY_HEADER.setCharset("utf-8");
        ALI_PAY_HEADER.setSignType(aliSignType);
        return this;
    }

    public PayAPI wx(String wxMchId, String wxAppId, String wxKey) {
        WX_CONFIG = new WxConfig();
        WX_CONFIG.setAppID(wxAppId);
        WX_CONFIG.setMchID(wxMchId);
        WX_CONFIG.setKey(wxKey);
        WX_CONFIG.setHttpConnectTimeoutMs(8000);
        WX_CONFIG.setHttpReadTimeoutMs(10000);
        return this;
    }

    public PayAPI notifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
        return this;
    }

    public PayAPI returnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(PayAPI.class);

    public String pay(PayParam payParam, int payMethod, String version) {
        ALI_PAY_HEADER.setReturnUrl(returnUrl);
        ALI_PAY_HEADER.setNotifyUrl(notifyUrl + PayMethod.ALI_TYPE);
        payParam.setNotifyUrl(notifyUrl + PayMethod.WX_TYPE);
        String payResults;
        if (PayMethod.ALI_H5 == payMethod) {
            payResults = AliPay.wapPay(ALI_PAY_HEADER, AliPay.toAliPayBody(payParam), version);
        } else if (PayMethod.ALI_MOBILE == payMethod) {
            payResults = AliPay.mobilePay(ALI_PAY_HEADER, AliPay.toAliPayBody(payParam), version);
        } else if (PayMethod.WX_PUBLIC == payMethod) {
            payResults = WxPay.publicPay(WX_CONFIG, WxPay.toWxPayBody(payParam));
        } else if (PayMethod.WX_MOBILE == payMethod) {
            payResults = WxPay.mobilePay(WX_CONFIG, WxPay.toWxPayBody(payParam));
        } else {
            throw new RuntimeException("不支持的支付类别");
        }
        LOGGER.info("支付结果[{}]", payResults);
        return payResults;
    }

    public PayResult query(PayParam payParam, int payType) {
        PayResult queryResult = null;
        if (PayMethod.ALI_TYPE == payType) {
            queryResult = AliPay.query(ALI_PAY_HEADER, AliPay.toAliQueryBody(payParam));
        } else if (PayMethod.WX_TYPE == payType) {
            queryResult = WxPay.query(WX_CONFIG, WxPay.toWxQueryBody(payParam));
        }
        if (queryResult == null) {
            throw new RuntimeException("未知的支付类别");
        }
        LOGGER.info("查询结果[{}]", queryResult);
        return queryResult;
    }

    // TODO: 2017/10/1 暂定，把退款申请成功当作退款成功
    public PayResult refund(PayParam payParam, int payType) {
        PayResult refundResult = null;
        if (PayMethod.ALI_TYPE == payType) {
            refundResult = AliPay.refund(ALI_PAY_HEADER, AliPay.toAliRefundBody(payParam));
        } else if (PayMethod.WX_TYPE == payType) {
            refundResult = WxPay.refund(WX_CONFIG, WxPay.toWxRefundBody(payParam));
        }
        if (refundResult == null) {
            throw new RuntimeException("不存在的支付类别");
        }
        return refundResult;
    }

    /**
     * 支付回调处理
     *
     * @param notifyParam 原生回调信息
     * @param payType     支付类别
     * @return 统一支付结果
     */
    @SuppressWarnings("unchecked")
    public PayResult processNotify(Object notifyParam, int payType) {
        PayResult payResult;
        if (PayMethod.ALI_TYPE == payType) {
            payResult = AliPay.processNotify(ALI_PAY_HEADER, (Map<String, String>) notifyParam);
        } else if (PayMethod.WX_TYPE == payType) {
            payResult = WxPay.processNotify(WX_CONFIG, (String) notifyParam);
        } else {
            throw new RuntimeException("未知的支付类型");
        }
        return payResult;
    }

}
