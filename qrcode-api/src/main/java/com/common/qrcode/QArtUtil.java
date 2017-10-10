package com.common.qrcode;

import com.google.zxing.common.BitMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import free6om.research.qart4j.Image;
import free6om.research.qart4j.ImageUtil;
import free6om.research.qart4j.MatrixToImageConfig;
import free6om.research.qart4j.MatrixToImageWriter;
import free6om.research.qart4j.QRCode;

/**
 * 具体的每个值请参考{@link free6om.research.qart4j.QArt}
 */
public class QArtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger("test");

    public static void main(String[] args) {
        //input
        String filename = "D:\\IdeaProjects\\qrcode\\src\\main\\resources\\qr\\8x8.jpg";//背景图
        String url = "各种姿势的二维码";//实际就是网址信息

        //QR code
        int version = 6;
        int mask = 2;//隐藏0-7
        int quietZone = 1;//放大缩小二维码(非常智能) 值越大越小
        int rotation = 0;//回转0-3
        int colorBlack = 0xEFFFFFFF;// 0xFF000000;//码色
        int colorWhite = 0;// 0xEFFFFFFF; 背景

        //how to generate QR code
        boolean randControl = false;
        long seed = 1;
        if (seed == -1) {
            seed = System.currentTimeMillis();
        }
        boolean dither = false;
        boolean onlyDataBits = false;
        boolean saveControl = false;

        //输出图片大小
        int width = 260;
        int height = 260;
        //二维码大小
        int size = 260;
        //二维码坐标
        Integer marginTop = 0;
        Integer marginLeft = 0;
        Integer marginBottom = 0;
        Integer marginRight = 0;

        String outputFormat = "jpg";
        String output = "D:\\IdeaProjects\\qrcode\\src\\main\\resources\\qr\\output.jpg";

        try {
            //背景
            BufferedImage input = ImageUtil.loadImage(filename, width, height);

            int qrSizeWithoutQuiet = 17 + 4 * version;
            int qrSize = qrSizeWithoutQuiet + quietZone * 2;
            if (size < qrSize) { //don't scale
                size = qrSize;
            }
            int scale = size / qrSize;
            int targetQrSizeWithoutQuiet = qrSizeWithoutQuiet * scale;

            Rectangle inputImageRect = new Rectangle(new free6om.research.qart4j.Point(0, 0), width, height);
            int startX = 0, startY = 0;
            if (marginLeft != null) {
                startX = marginLeft;
            } else if (marginRight != null) {
                startX = width - marginRight - size;
            }
            if (marginTop != null) {
                startY = marginTop;
            } else if (marginBottom != null) {
                startY = height - marginBottom - size;
            }

            Rectangle qrRect = new Rectangle(new free6om.research.qart4j.Point(startX, startY), size, size);
            Rectangle qrWithoutQuietRect = new Rectangle(new free6om.research.qart4j.Point(startX + (size - targetQrSizeWithoutQuiet) / 2, startY + (size - targetQrSizeWithoutQuiet) / 2), targetQrSizeWithoutQuiet, targetQrSizeWithoutQuiet);

            int[][] target = null;
            int dx = 0, dy = 0;
            BufferedImage targetImage = null;
            //二维码
            Rectangle targetRect = inputImageRect.intersect(qrWithoutQuietRect);
            if (targetRect == null) {
                LOGGER.warn("no intersect zone");
                target = new int[0][0];
            } else {
                targetImage = input.getSubimage(targetRect.start.x, targetRect.start.y, targetRect.width, targetRect.height);
                int scaledWidth = targetRect.width / scale;
                int scaledHeight = targetRect.height / scale;
                BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = scaledImage.createGraphics();
                graphics.drawImage(targetImage, 0, 0, scaledWidth, scaledHeight, null);
                graphics.dispose();

                target = ImageUtil.makeTarget(scaledImage, 0, 0, scaledWidth, scaledHeight);
                dx = (qrWithoutQuietRect.start.x - targetRect.start.x) / scale;
                dy = (qrWithoutQuietRect.start.y - targetRect.start.y) / scale;
            }


            free6om.research.qart4j.Image image = new Image(target, dx, dy, url, version, mask, rotation, randControl, seed, dither, onlyDataBits, saveControl);

            QRCode qrCode = image.encode();
            BitMatrix bitMatrix = ImageUtil.makeBitMatrix(qrCode, quietZone, size);

            MatrixToImageConfig config = new MatrixToImageConfig(colorBlack, colorWhite);
            BufferedImage finalQrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

            Rectangle finalRect = qrRect.union(inputImageRect);
            BufferedImage finalImage = new BufferedImage(finalRect.width, finalRect.height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = finalImage.createGraphics();

            graphics.drawImage(input,
                    inputImageRect.start.x - finalRect.start.x, inputImageRect.start.y - finalRect.start.y,
                    inputImageRect.width, inputImageRect.height, null);
            graphics.drawImage(finalQrImage,
                    qrRect.start.x - finalRect.start.x, qrRect.start.y - finalRect.start.y,
                    qrRect.width, qrRect.height, null);
            graphics.dispose();

            if (outputFormat.toUpperCase().contentEquals("jpg")) {
                // Creating a non Alpha channel bufferedImage so that alpha channel does not corrupt jpeg.
                BufferedImage nonAlpha = new BufferedImage(finalImage.getWidth(), finalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics nonAlphaGraphics = nonAlpha.createGraphics();

                nonAlphaGraphics.setColor(Color.BLACK);
                nonAlphaGraphics.fillRect(0, 0, finalImage.getWidth(), finalImage.getHeight());
                nonAlphaGraphics.drawImage(finalImage, 0, 0, null);
                nonAlphaGraphics.dispose();

                finalImage = nonAlpha;
            }

            ImageIO.write(finalImage, outputFormat, new File(output));

        } catch (Exception e) {
            LOGGER.error("encode error", e);
        }

    }

}
