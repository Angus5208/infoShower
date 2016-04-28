package com.tingken.infoshower.util;

/**
 * Created by Administrator on 2016/3/29.
 */

import android.content.Intent;
import android.util.Log;

import org.xutils.common.Callback;

public class MyProgressCallBack<ResultType> implements Callback.ProgressCallback<ResultType> {
    //private Intent intent = new Intent("android.intent.action.MY_BROADCAST");

    @Override

    public void onSuccess(ResultType result) {
        //intent.putExtra("msg", "______" + "onSuccess");
        Log.e("请求返回", "onSuccess"+result);
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        Log.e("请求返回", "错误：" + ex + "____" + isOnCallback);
    }

    @Override
    public void onCancelled(CancelledException cex) {
        Log.e("请求返回", "onCancelled"+cex);
    }

    @Override
    public void onFinished() {
        Log.e("请求返回", "onFinished");
    }

    @Override
    public void onWaiting() {
    }

    @Override
    public void onStarted() {
       // Log.e("视屏", "onStarted");
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {
       // Log.e("视屏", "onLoading");
    }
}
