package com.common.qrcode;

import com.google.zxing.common.BitMatrix;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Properties;

import javax.imageio.ImageIO;

import free6om.research.qart4j.Image;
import free6om.research.qart4j.ImageUtil;
import free6om.research.qart4j.MatrixToImageConfig;
import free6om.research.qart4j.MatrixToImageWriter;
import free6om.research.qart4j.QRCode;

/**
 * Hello world!
 */
public class QArt2 {
    private static final Logger LOGGER = LoggerFactory.getLogger("test");

    public static void main(String[] args) {


        //input
        String filename = "D:\\IdeaProjects\\qrcode\\src\\main\\resources\\qr\\8x8.jpg";//(String) options.valueOf("i");
        String url ="哈哈哈哈哈哈";// (String) options.valueOf("u");

        //QR code
        int version = 6;//(Integer) options.valueOf("v");
        int mask = 0;//(Integer) options.valueOf("m");
        int quietZone = 1;//(Integer) options.valueOf("q");
        int rotation = 0;//(Integer) options.valueOf("r");
        int size = 200;//(Integer) options.valueOf("z");
        int colorBlack = 0xFF000000;//(int) Long.parseLong((String) options.valueOf("cb"), 16);
        int colorWhite =0xEFFFFFFF; //(int) Long.parseLong((String) options.valueOf("cw"), 16);

        //how to generate QR code
        boolean randControl = true;//(Boolean) options.valueOf("randControl");
        long seed = -1;
        if (seed == -1) {
            seed = System.currentTimeMillis();
        }
        boolean dither = false;//(Boolean) options.valueOf("d");
        boolean onlyDataBits = false;//(Boolean) options.valueOf("onlyData");
        boolean saveControl = false;//(Boolean) options.valueOf("saveControl");

        //output image
        int width = 800;//(Integer) options.valueOf("w");
        int height = 800;//(Integer) options.valueOf("h");

        Integer marginTop = null;//options.has("mt") ? (Integer) options.valueOf("mt") : null;
        Integer marginBottom = null;// options.has("mb") ? (Integer) options.valueOf("mb") : null;
        Integer marginLeft =null; //options.has("ml") ? (Integer) options.valueOf("ml") : null;
        Integer marginRight = null;//options.has("mr") ? (Integer) options.valueOf("mr") : null;

        String outputFormat = "jpg";
        String output = "D:\\IdeaProjects\\qrcode\\src\\main\\resources\\qr\\output.jpg";

//        configLog(log4j);

        //todo validate input params, make sure all of them are valid

        try {
            BufferedImage input = ImageUtil.loadImage(filename, width, height);

            int qrSizeWithoutQuiet = 17 + 4*version;
            int qrSize = qrSizeWithoutQuiet + quietZone * 2;
            if(size < qrSize) { //don't scale
                size = qrSize;
            }
            int scale = size / qrSize;
            int targetQrSizeWithoutQuiet = qrSizeWithoutQuiet * scale;

            Rectangle inputImageRect = new Rectangle(new free6om.research.qart4j.Point(0, 0), width, height);
            int startX = 0, startY = 0;
            if(marginLeft != null) {
                startX = marginLeft;
            } else if(marginRight != null) {
                startX = width - marginRight - size;
            }
            if(marginTop != null) {
                startY = marginTop;
            } else if(marginBottom != null) {
                startY = height - marginBottom - size;
            }

            Rectangle qrRect = new Rectangle(new free6om.research.qart4j.Point(startX, startY), size, size);
            Rectangle qrWithoutQuietRect = new Rectangle(new free6om.research.qart4j.Point(startX + (size-targetQrSizeWithoutQuiet)/2, startY + (size-targetQrSizeWithoutQuiet)/2), targetQrSizeWithoutQuiet, targetQrSizeWithoutQuiet);

            int[][] target = null;
            int dx = 0, dy = 0;
            BufferedImage targetImage = null;
            Rectangle targetRect = inputImageRect.intersect(qrWithoutQuietRect);
            if(targetRect == null) {
                LOGGER.warn("no intersect zone");
                target = new int[0][0];
            } else {
                targetImage = input.getSubimage(targetRect.start.x, targetRect.start.y, targetRect.width, targetRect.height);
                int scaledWidth = targetRect.width/scale;
                int scaledHeight = targetRect.height/scale;
                BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = scaledImage.createGraphics();
                graphics.drawImage(targetImage, 0, 0, scaledWidth, scaledHeight, null);
                graphics.dispose();

                target = ImageUtil.makeTarget(scaledImage, 0, 0, scaledWidth, scaledHeight);
                dx = (qrWithoutQuietRect.start.x - targetRect.start.x)/scale;
                dy = (qrWithoutQuietRect.start.y - targetRect.start.y)/scale;
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

            if(outputFormat.toUpperCase().contentEquals("JPEG")){
                // Creating a non Alpha channel bufferedImage so that alpha channel does not corrupt jpeg.
                BufferedImage nonAlpha = new BufferedImage(finalImage.getWidth(), finalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics nonAlphaGraphics = nonAlpha.createGraphics();

                nonAlphaGraphics.setColor(Color.white);
                nonAlphaGraphics.fillRect(0,0, finalImage.getWidth(), finalImage.getHeight());
                nonAlphaGraphics.drawImage(finalImage, 0, 0, null);
                nonAlphaGraphics.dispose();

                finalImage = nonAlpha;
            }

            ImageIO.write(finalImage, outputFormat , new File(output));

        } catch (Exception e) {
            LOGGER.error("encode error", e);
        }

    }

    private static void configLog(String configFile) {
        if(new File(configFile).exists()) {
            PropertyConfigurator.configure(configFile);
            return;
        }

        Properties properties = new Properties();

        properties.setProperty("log4j.rootLogger", "DEBUG, CA");
        properties.setProperty("log4j.appender.CA", "org.apache.log4j.ConsoleAppender");
        properties.setProperty("log4j.appender.CA.layout", "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.CA.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} %-4r [%t] %-5p %c %x - %m%n");
        PropertyConfigurator.configure(properties);
    }
}
