package com.example.img;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class LSBSteganographyDecoder {

    public static String decode(String imagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));

        // 解码前4个字节获取消息长度
        int length = 0;
        for (int i = 0; i < 4; i++) {
            byte b = decodeByte(image, i);
            length = (length << 8) | (b & 0xFF);
        }

        // 解码消息内容
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte b = decodeByte(image, 4 + i);
            sb.append((char) b);
        }

        return sb.toString();
    }

    private static byte decodeByte(BufferedImage image, int pos) {
        int x = pos % image.getWidth();
        int y = pos / image.getWidth();
        int rgb = image.getRGB(x, y);
        return (byte) (rgb & 0x01);
    }
}
