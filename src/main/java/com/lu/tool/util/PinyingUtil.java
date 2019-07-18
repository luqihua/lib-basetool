package com.lu.tool.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by lu on 2016/4/11.
 */
public class PinyingUtil {
    /**
     * 汉字转全拼
     */
    public static String getPingYin(String src) {
        StringBuilder resultBuilder = new StringBuilder();// 用来保存要输出的拼音

        char[] t1 = src.toCharArray();
        String[] buffer;
        HanyuPinyinOutputFormat outPutFormat = new HanyuPinyinOutputFormat();// 此类用来设定输出的拼音格式

        // 设置输出格式
        outPutFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        outPutFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outPutFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        try {
            for (int i = 0; i < t1.length; i++) {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches("[\\u4e00-\\u9FA5]+")) {
                    buffer = PinyinHelper.toHanyuPinyinStringArray(t1[i],
                            outPutFormat);
                    resultBuilder.append(buffer[0]);
                } else {
                    resultBuilder.append(Character.toString(t1[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return resultBuilder.toString();
    }

    /**
     * 返回首字母
     *
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {
        StringBuilder resultBuilder = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] buffer = PinyinHelper.toHanyuPinyinStringArray(word);
            if (buffer != null && buffer.length > 0) {
                resultBuilder.append(buffer[0].charAt(0));
            } else {
                resultBuilder.append(word);
            }
        }
        return resultBuilder.toString();
    }

    /**
     * 将字符串转为ASCII码
     *
     * @param cnStr
     * @return
     */
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }
}
