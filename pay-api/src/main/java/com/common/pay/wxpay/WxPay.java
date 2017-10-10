package com.common.pay.wxpay;

import com.common.pay.PayMethod;
import com.common.pay.PayParam;
import com.common.pay.PayResult;
import com.common.pay.PayUtil;
import com.common.pay.common.JSON;
import com.common.pay.common.MapUtil;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WxPay {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxPay.class);

    public static String publicPay(WxConfig config, Map<String, String> body) {
        Map<String, String> payResults = pay(config, body, "JSAPI");
        Map<String, String> appResults = new HashMap<>();
        appResults.put("appId", config.getAppID());
        appResults.put("timeStamp", timeStamp());
        appResults.put("nonceStr", WXPayUtil.generateNonceStr());
        appResults.put("package", "prepay_id=" + payResults.get("prepay_id"));
        appResults.put("signType", "MD5");
        appResults.put("paySign", sign(appResults, config.getKey()));
        return JSON.toJSONStr(appResults);
    }

    public static String mobilePay(WxConfig config, Map<String, String> body) {
        Map<String, String> payResults = pay(config, body, "APP");
        Map<String, String> appResults = new HashMap<>();
        appResults.put("appid", config.getAppID());
        appResults.put("partnerid", config.getMchID());
        appResults.put("prepayid", payResults.get("prepay_id"));
        appResults.put("package", "Sign=WXPay");
        appResults.put("noncestr", WXPayUtil.generateNonceStr());
        appResults.put("timestamp", timeStamp());
        appResults.put("sign", sign(appResults, config.getKey()));
        return JSON.toJSONStr(appResults);
    }

    private static String timeStamp() {
        long ms = System.currentTimeMillis();
        return String.valueOf(ms / 1000L);
    }

    private static String sign(Map<String, String> map, String key) {
        try {
            return WXPayUtil.generateSignature(map, key);
        } catch (Exception e) {
            LOGGER.error("微信签名异常", e);
            throw new RuntimeException("微信签名异常");
        }

    }

    public static Map<String, String> pay(WxConfig config, Map<String, String> body, String tradeType) {
        body.put("trade_type", tradeType);
        WXPay wxpay = new WXPay(config);
        Map<String, String> resp;
        try {
            resp = wxpay.unifiedOrder(body);
            LOGGER.info("微信支付返回[{}]", resp);
            if (!"SUCCESS".equals(resp.get("return_code"))) {
                throw new RuntimeException("微信支付发起失败:" + resp.get("return_msg"));
            } else {
                if (!"SUCCESS".equals(resp.get("result_code"))) {
                    throw new RuntimeException("微信支付发起成功,处理失败:" + resp.get("err_code_des"));
                }
            }
            return resp;
        } catch (Exception e) {
            LOGGER.error("微信支付下单异常" + tradeType, tradeType, e);
            throw new RuntimeException("微信MOBILE支付下单异常");
        }
    }

    public static PayResult query(WxConfig config, Map<String, String> body) {
        PayResult queryResult = new PayResult();
        WXPay wxpay = new WXPay(config);
        Map<String, String> resp;
        try {
            resp = wxpay.orderQuery(body);
            LOGGER.info("微信查询返回[{}]", resp);
            if ("SUCCESS".equals(resp.get("return_code"))
                    && "SUCCESS".equals(resp.get("result_code"))
                    && "SUCCESS".equals(resp.get("trade_state"))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                queryResult.setPayTime(sdf.parse(resp.get("time_end")));
                queryResult.setDesc("主动查单-第三方支付成功");
                queryResult.setStatus(PayResult.PayStatus.success);
                queryResult.setPayType(PayMethod.WX_TYPE);
                queryResult.setTradeNo(resp.get("transaction_id"));
                queryResult.setOrderCode(resp.get("out_trade_no"));
                queryResult.setMoney(new BigDecimal(resp.get("total_fee")).divide(new BigDecimal("100")));
                queryResult.setRawData(resp);
            } else {
                queryResult.setStatus(PayResult.PayStatus.process);
            }
            return queryResult;
        } catch (Exception e) {
            LOGGER.error("微信查询订单异常", e);
            throw new RuntimeException("微信查询订单异常");
        }
    }

    public static PayResult refund(WxConfig config, Map<String, String> body) {
        WXPay wxpay = new WXPay(config);
        Map<String, String> resp;
        try {
            resp = wxpay.refund(body);
            LOGGER.info("微信退款返回[{}]", resp);

            PayResult payResult = new PayResult();
            if ("SUCCESS".equals(resp.get("return_code")) && "SUCCESS".equals(resp.get("result_code"))) {
                payResult.setStatus(PayResult.PayStatus.success);
            } else {
                payResult.setStatus(PayResult.PayStatus.fail);
                payResult.setDesc(String.format("退款申请失败->状态[%s] 原因[%s]", resp.get("return_code"),
                        resp.get("return_msg")));
            }
            payResult.setPayTime(new Date());
            payResult.setPayType(PayMethod.WX_TYPE);
            return payResult;
        } catch (Exception e) {
            LOGGER.error("微信退款异常", e);
            throw new RuntimeException("微信退款异常");
        }
    }

    public static String refundQuery(WxConfig config, Map<String, String> body) {
        WXPay wxpay = new WXPay(config);
        Map<String, String> resp;
        try {
            resp = wxpay.refundQuery(body);
            LOGGER.info("微信退款查询返回[{}]", resp);
            return JSON.toJSONStr(resp);
        } catch (Exception e) {
            LOGGER.error("微信退款查询异常", e);
            throw new RuntimeException("微信退款查询异常");
        }
    }


    public static Map<String, String> toWxPayBody(PayParam payParam) {
        Map<String, String> body = new HashMap<>();
        MapUtil.putIfNotNull(body, "nonce_str", WXPayUtil.generateNonceStr());
        MapUtil.putIfNotNull(body, "attach", payParam.getSubject());
        MapUtil.putIfNotNull(body, "body", payParam.getDesc());
        MapUtil.putIfNotNull(body, "out_trade_no", payParam.getOutTradeNo());
        MapUtil.putIfNotNull(body, "total_fee", PayUtil.yuan2fen(payParam.getMoney()));
        MapUtil.putIfNotNull(body, "spbill_create_ip", payParam.getClientIp());
        MapUtil.putIfNotNull(body, "time_expire", PayUtil.dateFormat(payParam.getTimeExpire(), "yyyyMMddHHmmss"));
        MapUtil.putIfNotNull(body, "notify_url", payParam.getNotifyUrl());
        MapUtil.putIfNotNull(body, "openid", payParam.getOpenId());
        MapUtil.putIfNotNull(body, "product_id", payParam.getProductId());
        return body;
    }

    public static Map<String, String> toWxQueryBody(PayParam payParam) {
        Map<String, String> body = new HashMap<>();
        MapUtil.putIfNotNull(body, "nonce_str", WXPayUtil.generateNonceStr());
        MapUtil.putIfNotNull(body, "out_trade_no", payParam.getOutTradeNo());
        MapUtil.putIfNotNull(body, "transaction_id", payParam.getTradeNo());
        return body;
    }

    public static Map<String, String> toWxRefundBody(PayParam payParam) {
        Map<String, String> body = new HashMap<>();
        MapUtil.putIfNotNull(body, "nonce_str", WXPayUtil.generateNonceStr());
        MapUtil.putIfNotNull(body, "transaction_id", payParam.getTradeNo());
        MapUtil.putIfNotNull(body, "out_trade_no", payParam.getOutTradeNo());
        MapUtil.putIfNotNull(body, "out_refund_no", payParam.getRefundNo());
        MapUtil.putIfNotNull(body, "total_fee", PayUtil.yuan2fen(payParam.getMoney()));
        MapUtil.putIfNotNull(body, "refund_fee", PayUtil.yuan2fen(payParam.getRefundAmount()));
        MapUtil.putIfNotNull(body, "refund_desc", payParam.getRefundReason());
        return body;
    }

    public static PayResult processNotify(WxConfig wxConfig, String notifyParam) {
        WXPay wxpay = new WXPay(wxConfig);
        Map<String, String> rawData;
        boolean check;
        try {
            rawData = WXPayUtil.xmlToMap(notifyParam);
            check = wxpay.isPayResultNotifySignatureValid(rawData);
        } catch (Exception e) {
            LOGGER.error("回调数据校验异常", e);
            throw new RuntimeException("回调参数校验异常");
        }
       /* if (!check) {
            throw new RuntimeException("回调参数校验失败");
        }*/
        //处理返回结果
        PayResult payResult = new PayResult();
        payResult.setRawData(rawData);
        String resultCode = rawData.get("result_code");
        if ("SUCCESS".equals(resultCode) || "".equals(resultCode)) {
            payResult.setStatus(PayResult.PayStatus.success);
            payResult.setOrderCode(rawData.get("out_trade_no"));
            String timeEnd = rawData.get("time_end");
            //组织支付时间
            Date payTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                payTime = sdf.parse(timeEnd);
            } catch (ParseException e) {
                LOGGER.error("payTime日期解析异常", e);
                payTime = new Date();
            }
            payResult.setPayTime(payTime);
            payResult.setTradeNo(rawData.get("transaction_id"));
            payResult.setDesc("支付成功");
            //组织支付金额，微信是分
            payResult.setMoney(new BigDecimal(rawData.get("total_fee")).divide(new BigDecimal("100")));
        } else if ("FAIL".equals(resultCode)) {
            payResult.setStatus(PayResult.PayStatus.fail);
            payResult.setDesc(String.format("微信支付错误码[%s] 错误信息[%s]", rawData.get("err_code"), rawData.get("err_code_des")));
        } else {
            throw new RuntimeException("未知的微信支付resultCode:" + resultCode);
        }
        payResult.setPayType(PayMethod.WX_TYPE);
        return payResult;
    }


}
