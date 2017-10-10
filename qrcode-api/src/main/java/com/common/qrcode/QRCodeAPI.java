package com.common.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class QRCodeAPI {

    private static final int DEFAULT_WIDTH = 430;
    private static final int DEFAULT_HEIGHT = 430;
    private static final ErrorCorrectionLevel DEFAULT_ERROR_LEVEL = ErrorCorrectionLevel.H;
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 创建我们的二维码图片
     *
     * @param content   二维码内容
     * @param format    生成二维码的格式
     * @param width     长度
     * @param height    高度
     * @param outStream 二维码信息流载体
     * @throws IOException     抛出io异常
     * @throws WriterException 抛出书写异常
     */
    public static void getQrCode(String content,
                                  String format,
                                  int width,
                                  int height,
                                  ByteArrayOutputStream outStream,
                                  ErrorCorrectionLevel errorLevel,
                                  String charset) throws IOException, WriterException {

        if (outStream == null) {
            throw new NullPointerException("outStream 必须指定");
        }
        //如果存储大小的不为空，那么对我们图片的大小进行设定
        width = width < 10 ? DEFAULT_WIDTH : width;
        height = height < 10 ? DEFAULT_HEIGHT : width;

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
        hints.put(EncodeHintType.ERROR_CORRECTION, errorLevel == null ? DEFAULT_ERROR_LEVEL : errorLevel);
        // 内容所使用字符集编码
        hints.put(EncodeHintType.CHARACTER_SET, charset == null ? DEFAULT_CHARSET : charset);
        hints.put(EncodeHintType.MARGIN, 1);//设置二维码边的空度，非负数

        //要编码的内容
        //编码类型，目前zxing支持：Aztec 2D,CODABAR 1D format,Code 39 1D,Code 93 1D ,Code 128 1D,
        //Data Matrix 2D , EAN-8 1D,EAN-13 1D,ITF (Interleaved Two of Five) 1D,
        //MaxiCode 2D barcode,PDF417,QR Code 2D,RSS 14,RSS EXPANDED,UPC-A 1D,UPC-E 1D,UPC/EAN extension,UPC_EAN_EXTENSION
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE,
                width, //条形码的宽度
                height, //条形码的高度
                hints);//生成条形码时的一些配置,此项可选
        MatrixToImageWriter.writeToStream(bitMatrix, format, outStream);
    }


}
