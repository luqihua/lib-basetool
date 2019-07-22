package com.lu.tool.util;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * <加解密实现类>
 */
public class SecurityUtil {
    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] cipherData = md5.digest(str.getBytes());
            return byte2hex(cipherData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byte2hex(byte[] datas) {
        StringBuilder builder = new StringBuilder();
        for (byte cipher : datas) {
            String toHexStr = Integer.toHexString(cipher & 0xff);
            if (toHexStr.length() == 1) {
                builder.append("0");
            }
            builder.append(toHexStr);
        }
        return builder.toString();
    }

    public static String formatParams(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        Map<String, String> tmpMap = new TreeMap<>(params);

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
            stringBuilder.append("&");
        }

        return stringBuilder.substring(0, stringBuilder.lastIndexOf("&"));
    }


    public static String signParams(TreeMap<String, String> params, String secureKey) {
        if (params == null || params.size() == 0) return "";

        TreeMap<String, String> copy = new TreeMap<>(params);
        copy.put("secret", secureKey);

        String request = new Gson().toJson(copy);
        request = getMD5(request);
        return request;
    }

}
