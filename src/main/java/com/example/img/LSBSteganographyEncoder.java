package com.example.img;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class LSBSteganographyEncoder {

    public static void encode(String imagePath, String secretMessage, String outputImagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));
        int messageLength = secretMessage.length();
        if (messageLength * 8 + 32 > image.getWidth() * image.getHeight()) {
            throw new RuntimeException("Image not big enough to hold the message.");
        }

        // 将消息长度编码为4字节
        byte[] lengthBytes = intTo4ByteArray(messageLength);
        for (int i = 0; i < 4; i++) {
            encodeByte(image, lengthBytes[i], i);
        }

        // 编码每个字符
        for (int i = 0; i < messageLength; i++) {
            encodeByte(image, (byte) secretMessage.charAt(i), 4 + i);
        }

        ImageIO.write(image, "png", new File(outputImagePath));
    }

    private static void encodeByte(BufferedImage image, byte b, int pos) {
        int x = pos % image.getWidth();
        int y = pos / image.getWidth();
        int rgb = image.getRGB(x, y);

        // 修改最后一位
        rgb = (rgb & 0xFFFFFFFE) | ((b >> 7) & 0x01); // 第一位
        image.setRGB(x, y, rgb);
    }

    private static byte[] intTo4ByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }
}
