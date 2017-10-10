package com.common.qrcode;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class BuildIntoAPI {

    /**
     *
     * @param qrCode 二维码
     * @param background 背景图(目标码)
     * @param topOffset top偏移
     * @param leftOffset left偏移
     * @param codeColor 二维码颜色填充
     * @throws IOException 获取背景图异常
     */
    public static void buildInto(BufferedImage qrCode, BufferedImage background, int topOffset, int leftOffset, int codeColor) throws IOException {
        //背景抠图
        int qrH = qrCode.getHeight();
        int qrW = qrCode.getWidth();
        for (int h = 0; h < qrH; h++) {
            for (int w = 0; w < qrW; w++) {
                int rgb = qrCode.getRGB(h, w);
                int red = rgb & 0xFF0000;
                int green = rgb & 0x00FF00;
                int blue = rgb & 0x0000FF;
                if (red > 127 && green > 127 && blue > 127) {
                    background.setRGB(h + topOffset, w + leftOffset, codeColor);
                }
            }
        }
    }

}
