package com.jackh.okhttps.self.sign.utils;

import android.content.Context;

import com.jackh.okhttps.self.sign.sslutils.DefaultSSLCertificateListener;
import com.jackh.okhttps.self.sign.sslutils.SSLSocketFactoryUtils;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * Project Name：okhttps_self_sign
 * Created by hejunqiu on 2019/11/20 15:49
 * Description:
 */
public class OkHttpUtils {

    private static OkHttpUtils mInstance;

    private Context mContext;

    private OkHttpClient mClient;

    private OkHttpUtils(Context context) {
        mContext = context.getApplicationContext();

        /**
         * 自签名时:
         * isTrustAll: true, 信任所有证书,不做任何验证(不安全.).
         * isTrustAll: false, 验证自签名证书.
         */
        boolean isTrustAll = false;

        SSLSocketFactoryUtils.SSLParams sslParams = SSLSocketFactoryUtils
                .getSSLSocketFactory(mContext, isTrustAll, new DefaultSSLCertificateListener());

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier((hostname, session) -> true);

        mClient = builder.build();
    }

    public static OkHttpUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OkHttpUtils(context);
        }
        return mInstance;
    }

    public OkHttpClient getOkHttpClient() {
        return mClient;
    }
}
