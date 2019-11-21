package com.jackh.okhttps.self.sign.sslutils;

import android.content.Context;
import android.text.TextUtils;

import com.jackh.okhttps.self.sign.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Project Name：okhttps_self_sign
 * Created by hejunqiu on 2019/11/21 15:11
 * Description:
 */
public class DefaultSSLCertificateListener implements SSLCertificateListener {

    private static String CER_SIGN_STR = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDdzCCAl+gAwIBAgIEMZgZtTANBgkqhkiG9w0BAQsFADBsMQswCQYDVQQGEwJ6\n" +
            "aDERMA8GA1UECBMIc2hlbnpoZW4xETAPBgNVBAcTCHNoZW56aGVuMREwDwYDVQQK\n" +
            "EwhoZWp1bnFpdTERMA8GA1UECxMIaGVqdW5xaXUxETAPBgNVBAMTCGhlanVucWl1\n" +
            "MB4XDTE5MTEyMDAyMTAzNFoXDTI5MDkyODAyMTAzNFowbDELMAkGA1UEBhMCemgx\n" +
            "ETAPBgNVBAgTCHNoZW56aGVuMREwDwYDVQQHEwhzaGVuemhlbjERMA8GA1UEChMI\n" +
            "aGVqdW5xaXUxETAPBgNVBAsTCGhlanVucWl1MREwDwYDVQQDEwhoZWp1bnFpdTCC\n" +
            "ASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJeIM8X1xcHXe5K9rnRTRz0Y\n" +
            "faeSH5qFUzvliSFRF6Qml9rOnfwVqJ1r8nuja36DgunAyOPWdxjGO/E2Mf9cyUgR\n" +
            "mLdCDv2UBoYKl/TqC6TmcqT2gVsTlrGF5QRWIO/+LoHfcHWUx8uOzyA6w1/wZXuv\n" +
            "d3CD2LWii43KQGikaj5XCd8v6g/p95+W1Ak06Px0ZiKiq6ffX/MF3PUnXaVOyD3w\n" +
            "ct832K4L6vrzr5o5U5VRT/Mkml5YjbBwXEFuDPF8lKNqO/jeiwZqqtLY+hxzIjtC\n" +
            "ipxgYaCB6+EHjFpuCaMM/8uUgPqEHqFF9ltOim2qGCOyFH5Zf7tEerLfFHhvE+EC\n" +
            "AwEAAaMhMB8wHQYDVR0OBBYEFI6fCq8cTNRLYmgeA+HTM/xopgmuMA0GCSqGSIb3\n" +
            "DQEBCwUAA4IBAQAyM72anSQHyTOjNgKa/14aVgLHkGx7bNhiezp44XDkrb95EYkf\n" +
            "of1o2FiZN9uEKefyeyHwMwlJDEIzIvkb7L+o/DpNtsABlnSMmpI9vMB7f5CqQslB\n" +
            "z7fSzMFrHBSGE4PaD/QLiLyOr9khJtOmZ55ur4U5vL41Sdb6EIxi7CXQ7qFp3xyv\n" +
            "k1kCaf6DYjqSE//hJXTpMRSysj37EiMmOg6G98WmXYx+xeXCIw9TvUtyArhZLltM\n" +
            "hzGD4ib0ikMZdvOPdWq6iwUqjPqGAn88Bu7hVxwYtorIpPNhvqx/FM8QXtRoVrRT\n" +
            "b/4XWzzlm2DI1eYHfOL7sJABOyS8mkTuw+mH\n" +
            "-----END CERTIFICATE-----";

    @Override
    public InputStream[] genCertificateIs(Context context) throws IOException {
        if (TextUtils.isEmpty(BuildConfig.serverCerFile)) {
            /**
             * keytool -printcert -rfc -file jackh_server.cer
             *
             * 使用字符串替代证书.
             */
            return new InputStream[]{new ByteArrayInputStream(CER_SIGN_STR.getBytes())};
        } else {
            /**
             * 通过keytool命令生成的数字证书.
             */
            return new InputStream[]{context.getAssets().open(BuildConfig.serverCerFile)};
        }
    }

    @Override
    public ClientBksParams getClientBksParams() {
        ClientBksParams params = new ClientBksParams();
        params.clientBksFile = BuildConfig.clientBksFile;
        params.clientBksPass = BuildConfig.clientBksPass;
        return params;
    }
}
