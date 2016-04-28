package com.tingken.infoshower.util;

import android.util.Log;

import com.tingken.infoshower.core.LocalService;
import com.tingken.infoshower.core.LocalServiceFactory;

/**
 * Created by Administrator on 2016/4/27.
 */
public class SwitchMachineTask {
    private String url = "http://10.0.205.13:211/WebInterface/VideoDisplay.svc/DeviceWeb/Switch/";
    private LocalService localService = LocalServiceFactory.getSystemLocalService();

    public void SwitchMachine() {

    }

    public String getReturn() {
        Log.e("id",localService.getLoginId());

        NetUtils.Get(url+localService.getLoginId(), null, new MyProgressCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                Log.e("开关机测试", result);
            }
        });
        return null;
    }
}
