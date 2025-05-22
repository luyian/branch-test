package com.example.img;

public class ImageUtils {

    public static void main(String[] args) throws Exception {
        String inputFilePath = "D:\\workspace\\test\\29.png";
        String outputFilePath = "D:\\workspace\\test\\2.png";

        LSBSteganographyEncoder.encode(inputFilePath, "hello world", outputFilePath);

        String decode = LSBSteganographyDecoder.decode(outputFilePath);
        System.out.println(decode);
    }

}
