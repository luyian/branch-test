package com.example.test;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinTool {

    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    public enum Type {
        UPPERCASE,  // 全部大写
        LOWERCASE,  // 全部小写
        FIRSTUPPER  // 首字母大写
    }

    public PinyinTool() {
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);  // 默认大写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  // 默认无音调
    }

    public String toPinYin(String str, String separator, Type type) throws BadHanyuPinyinOutputFormatCombination {
        if (str == null || str.trim().length() == 0) return "";
        if (type == Type.UPPERCASE)
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        else
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        StringBuilder py = new StringBuilder();
        String[] t;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((int) c <= 128) {  // 英文字符直接添加
                py.append(c);
            } else {
                t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (t == null) py.append(c);  // 无拼音时直接加入字符
                else {
                    String temp = t[0];
                    if (type == Type.FIRSTUPPER)
                        temp = temp.substring(0, 1).toUpperCase() + temp.substring(1);  // 首字母大写
                    py.append(temp).append(i == str.length() - 1 ? "" : separator);
                }
            }
        }
        return py.toString().trim();
    }


    public static void main(String[] args) throws Exception{
        PinyinTool pinyinTool = new PinyinTool();
        String str = pinyinTool.toPinYin("你好", " ", Type.LOWERCASE);
        System.out.println(str);
    }
}