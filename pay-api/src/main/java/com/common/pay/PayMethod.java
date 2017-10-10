package com.common.pay;

/**
 * 订单约定的各种支付方式对应的编号
 */
public class PayMethod {

    public static final int ALI_TYPE = 1;
    public static final int WX_TYPE = 2;
    public static final int COUPON = 3;


    //支付宝支付
    public static final int ALI_MOBILE = 10;//移动支付
    public static final int ALI_H5 = 11;//H5支付


    //微信支付
    public static final int WX_MOBILE = 20;//移动支付
    public static final int WX_PUBLIC = 21;//公众号支付


    public static int checkType(int method) {
        switch (method) {
            case ALI_TYPE:
            case ALI_MOBILE:
            case ALI_H5:
                return ALI_TYPE;
            case WX_TYPE:
            case WX_MOBILE:
            case WX_PUBLIC:
                return WX_TYPE;
            case COUPON:
                return COUPON;
        }
        throw new RuntimeException("未知的支付类别");
    }


}
