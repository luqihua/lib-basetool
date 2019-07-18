package com.lu.tool.widget.banner;

import android.content.Context;
import android.widget.ImageView;

/**
 * author: luqihua
 * date:2018/7/14
 * description:
 **/
public interface IBannerCreator {
    ImageView createBanner(Context context);

    void showBanner(Context context, ImageView imageView, Object url);
}
