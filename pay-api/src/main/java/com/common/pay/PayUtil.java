package com.common.pay;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayUtil {

    public static String yuan2fen(String money) {
        BigDecimal bigDecimal = new BigDecimal(money);
        return String.valueOf(bigDecimal.
                multiply(new BigDecimal(100))
                .longValue());
    }

    //接收yyyy-MM-dd HH:mm:ss
    public static String dateFormat(String dateSrt, String format) {
        if (PayCheck.isBlank(dateSrt)) {
            return null;
        }
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dest = new SimpleDateFormat(format);
        Date date;
        try {
            date = source.parse(dateSrt);
        } catch (ParseException e) {
            throw new RuntimeException("时间解析失败");
        }
        return dest.format(date);
    }

    public static String timeNow(String dateSrt) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateSrt);
            long ex = (date.getTime() - System.currentTimeMillis()) / 1000 / 60;
            return ex + "m";
        } catch (ParseException e) {
            throw new RuntimeException("时间解析失败");
        }
    }

}
