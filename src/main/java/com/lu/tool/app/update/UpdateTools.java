package com.lu.tool.app.update;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lu.tool.widget.util.LDialogBuilder;

/**
 * 检测升级工具类
 * 为了方便移植   网络请求采用了最基本的HttpUrlConnection
 * Created by lqh on 2018/8/19.
 */
public  class UpdateTools extends AUpdateTools {

    public UpdateTools(Activity activity) {
        super(activity);
    }

    @Override
    protected String getUpdateUrl() {
        // TODO: 2019-07-17
        return "";
    }

    @Override
    protected IVersionInfo parseVersionData(String response) {
        return new Gson().fromJson(response,new TypeToken<AppVersionInfo>(){}.getType());
    }

    /**
     * 版本更新的提示 dialog
     *
     * @param context
     * @param versionName
     * @param versionDesc
     * @return
     */
    protected Dialog getUpdateNotifyDialog(Context context, String versionName, String versionDesc) {
        return new LDialogBuilder(context)
                .setTitle("更新提醒")
                .setContent("版本:" + versionName + "\n" + versionDesc, 16, false)
                .setPositiveClickListener(new LDialogBuilder.BtnClickListener() {
                    @Override
                    public void click(Dialog dialog) {
                        confirmUpdate();
                    }
                })
                .setNegativeClickListener(new LDialogBuilder.BtnClickListener() {
                    @Override
                    public void click(Dialog dialog) {
                        cancelUpdate();
                    }
                })
                .setCanceledOnTouchOutside(false)
                .build();
    }
}
