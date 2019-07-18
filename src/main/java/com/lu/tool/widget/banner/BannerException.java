package com.lu.tool.widget.banner;

/**
 * author: luqihua
 * date:2018/7/16
 * description: 用于生成统一前缀的运行时错误
 **/
public class BannerException extends RuntimeException {
    public static final String TAG = "Banner: ";
    public BannerException(String message) {
        super(TAG+message);
    }
}
