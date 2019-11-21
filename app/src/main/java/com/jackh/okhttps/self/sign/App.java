package com.jackh.okhttps.self.sign;

import android.app.Application;

import com.jackh.okhttps.self.sign.utils.OkHttpUtils;

/**
 * Project Name：okhttps_self_sign
 * Created by hejunqiu on 2019/11/20 15:48
 * Description:
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpUtils.getInstance(this);
    }

}
