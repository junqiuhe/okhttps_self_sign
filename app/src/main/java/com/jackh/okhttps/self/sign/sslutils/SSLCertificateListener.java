package com.jackh.okhttps.self.sign.sslutils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Project Name：okhttps_self_sign
 * Created by hejunqiu on 2019/11/21 15:06
 * Description:
 */
public interface SSLCertificateListener {

    InputStream[] genCertificateIs(Context context) throws IOException;

    ClientBksParams getClientBksParams();

    class ClientBksParams{
        String clientBksFile;
        String clientBksPass;
    }
}