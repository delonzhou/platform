package com.common.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.common.pay.PayCheck;
import com.common.pay.PayMethod;
import com.common.pay.PayParam;
import com.common.pay.PayResult;
import com.common.pay.PayUtil;
import com.common.pay.common.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AliPay {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPay.class);

    public static String wapPay(AliHeader header, AliPayBody body, String version) {
        String bizHeader = JSON.toJSONStr(header);
        String bizBody = JSON.toJSONStr(body);
        LOGGER.info("支付宝Wap支付请求 header[{}] body[{}]", bizHeader, bizBody);
        AlipayClient alipayClient = new DefaultAlipayClient(header.getGateWay(), header.getAppId(), header.getPrivateKey(),
                header.getDataType(), header.getCharset(), header.getPublicKey(), header.getSignType()); //获得初始化的AlipayClient
        body.setProduct_code("QUICK_WAP_WAY");//设置为Wap支付
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setReturnUrl(header.getReturnUrl());
        alipayRequest.setNotifyUrl(header.getNotifyUrl());
        alipayRequest.setBizContent(bizBody);
        try {
            String form;
            form = alipayClient.pageExecute(alipayRequest, "GET").getBody();
            LOGGER.info("支付宝Wap支付返回 [{}]", form);
            return form;
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝Wap支付异常", e);
            throw new RuntimeException("支付宝Wap支付异常");
        }
    }

    public static String mobilePay(AliHeader header, AliPayBody body, String version) {
        String bizHeader = JSON.toJSONStr(header);
        String bizBody = JSON.toJSONStr(body);
        LOGGER.info("支付宝MOBILE支付请求 header[{}] body[{}]", bizHeader, bizBody);
        AlipayClient alipayClient = new DefaultAlipayClient(header.getGateWay(), header.getAppId(), header.getPrivateKey(),
                header.getDataType(), header.getCharset(), header.getPublicKey(), header.getSignType()); //获得初始化的AlipayClient
        body.setProduct_code("QUICK_MSECURITY_PAY");//设置为Wap支付
        AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
        alipayRequest.setNotifyUrl(header.getNotifyUrl());
        alipayRequest.setBizContent(bizBody);
        try {
            String form;
            form = alipayClient.sdkExecute(alipayRequest).getBody();
            LOGGER.info("支付宝MOBILE支付返回 [{}]", form);
            return form;
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝MOBILE支付异常", e);
            throw new RuntimeException("支付宝MOBILE支付异常");
        }
    }

    public static PayResult query(AliHeader AliHeader, AliQueryBody aliQueryBody) {
        PayResult queryResult = new PayResult();
        String body = JSON.toJSONStr(aliQueryBody);
        LOGGER.info("支付宝查询订单请求 header[{}] body[{}]", JSON.toJSONStr(AliHeader), body);
        AlipayClient alipayClient = new DefaultAlipayClient(AliHeader.getGateWay(), AliHeader.getAppId(), AliHeader.getPrivateKey(),
                AliHeader.getDataType(), AliHeader.getCharset(), AliHeader.getAliPublicKey(),
                AliHeader.getSignType());
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        request.setBizContent(body);//设置业务参数
        AlipayTradeQueryResponse response;
        try {
            response = alipayClient.execute(request);
            if ("1000".equals(response.getCode()) && ("TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                    "TRADE_FINISHED".equals(response.getTradeStatus()))) {
                queryResult.setPayTime(response.getSendPayDate());
                queryResult.setDesc("主动查单-第三方支付成功");
                queryResult.setStatus(PayResult.PayStatus.success);
                queryResult.setPayType(PayMethod.ALI_TYPE);
                queryResult.setTradeNo(response.getTradeNo());
                queryResult.setOrderCode(response.getOutTradeNo());
                queryResult.setMoney(new BigDecimal(response.getTotalAmount()));
                queryResult.setRawData(response.getParams());
            } else {
                queryResult.setStatus(PayResult.PayStatus.process);
            }
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝查询订单异常", e);
            throw new RuntimeException("支付宝查询订单异常");
        }
        return queryResult;
    }

    public static PayResult refund(AliHeader AliHeader, AliRefundBody aliRefundBody) {
        String body = JSON.toJSONStr(aliRefundBody);
        LOGGER.info("支付宝退款请求 header[{}] body[{}]", JSON.toJSONStr(AliHeader), body);
        AlipayClient alipayClient = new DefaultAlipayClient(
                AliHeader.getGateWay(), AliHeader.getAppId(),
                AliHeader.getPrivateKey(),
                AliHeader.getDataType(),
                AliHeader.getCharset(),
                AliHeader.getAliPublicKey(),
                AliHeader.getSignType());
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(body);//设置业务参数
        AlipayTradeRefundResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝退款异常", e);
            throw new RuntimeException("支付宝查询订单异常");
        }
        PayResult payResult = new PayResult();
        if ("1000".equals(response.getCode())) {
            payResult.setStatus(PayResult.PayStatus.success);
        } else {
            payResult.setStatus(PayResult.PayStatus.fail);
            payResult.setDesc(String.format("退款申请失败->状态[%s] 原因[%s]", response.getCode(), response.getMsg()));
        }
        payResult.setPayTime(new Date());
        payResult.setPayType(PayMethod.ALI_TYPE);
        return payResult;
    }

    public static AliPayBody toAliPayBody(PayParam payParam) {
        AliPayBody aliPayBody = new AliPayBody();
        aliPayBody.setSubject(payParam.getSubject());
        aliPayBody.setBody(payParam.getDesc());
        aliPayBody.setOut_trade_no(payParam.getOutTradeNo());
//        aliPayBody.setTime_expire(PayUtil.dateFormat(payParam.getTimeExpire(), "yyyy-MM-dd HH:mm"));
//        aliPayBody.setTime_expire("2017-10-09 12:00");
        String timeExpire = payParam.getTimeExpire();
        if (!PayCheck.isBlank(timeExpire)) {
            //计算时差
            aliPayBody.setTimeout_express(PayUtil.timeNow(timeExpire));
        }
        aliPayBody.setTotal_amount(payParam.getMoney());
        return aliPayBody;
    }

    public static AliQueryBody toAliQueryBody(PayParam payParam) {
        AliQueryBody aliQueryBody = new AliQueryBody();
        aliQueryBody.setOut_trade_no(payParam.getOutTradeNo());
        aliQueryBody.setTrade_no(payParam.getTradeNo());
        return aliQueryBody;
    }

    public static AliRefundBody toAliRefundBody(PayParam payParam) {
        AliRefundBody aliRefundBody = new AliRefundBody();
        aliRefundBody.setTrade_no(payParam.getTradeNo());
        aliRefundBody.setRefund_amount(payParam.getRefundAmount());
        aliRefundBody.setOut_request_no(payParam.getRefundNo());
        aliRefundBody.setRefund_reason(payParam.getRefundReason());
        aliRefundBody.setOut_trade_no(payParam.getOutTradeNo());
        return aliRefundBody;
    }


    @SuppressWarnings("unchecked")
    public static PayResult processNotify(AliHeader aliHeader, Map<String, String> notifyParam) {
        boolean check;
        try {
            check = AlipaySignature.rsaCheckV1(notifyParam, aliHeader.getPublicKey(),
                    aliHeader.getCharset(), aliHeader.getSignType()); //调用SDK验证签名
        } catch (AlipayApiException e) {
            LOGGER.error("回调数据校验异常", e);
            throw new RuntimeException("回调参数校验异常");
        }
      /*  if (!check) {
            throw new RuntimeException("回调参数校验失败");
        }*/
        PayResult payResult = new PayResult();
        payResult.setRawData(notifyParam);
        String resultCode = notifyParam.get("trade_status");
        payResult.setDesc(resultCode);
        payResult.setMoney(new BigDecimal(notifyParam.get("total_amount")));
        Date payTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            payTime = sdf.parse(notifyParam.get("gmt_payment"));
        } catch (ParseException e) {
            LOGGER.error("payTime日期解析异常", e);
            payTime = new Date();
        }
        payResult.setPayTime(payTime);
        payResult.setOrderCode(notifyParam.get("out_trade_no"));
        payResult.setTradeNo(notifyParam.get("trade_no"));
        if ("TRADE_SUCCESS".equals(resultCode) || "TRADE_FINISHED".equals(resultCode)) {
            payResult.setStatus(PayResult.PayStatus.success);
        } else if ("TRADE_CLOSED".equals(resultCode)) {
            payResult.setStatus(PayResult.PayStatus.close);
        } else {
            throw new RuntimeException("交易结果不可用" + resultCode);
        }
        payResult.setPayType(PayMethod.ALI_TYPE);
        return payResult;
    }

}
