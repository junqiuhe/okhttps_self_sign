package com.jackh.okhttps.self.sign.sslutils;

import android.content.Context;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.internal.platform.Platform;

/**
 * Project Name：okhttps_self_sign
 * Created by hejunqiu on 2019/11/21 14:27
 * Description:
 */
public class SSLSocketFactoryUtils {

    public static SSLParams getSSLSocketFactory(Context context,
                                                boolean isTrustAll,
                                                SSLCertificateListener listener) {

        SSLParams sslParams = new SSLParams();
        try {
            X509TrustManager trustManager;
            if (isTrustAll) {
                trustManager = getUnSafeTrustManager();
            } else {
                trustManager = chooseTrustManager(prepareTrustManager(context, listener));
                if (trustManager == null) {
                    trustManager = getUnSafeTrustManager();
                } else {
                    trustManager = new MyTrustManager(trustManager);
                }
            }

            KeyManager[] keyManagers = getKeyManager(context, listener);

            /**
             * 5、利用TrustManager初始化SSLContext上下文
             */
            SSLContext sslContext = Platform.get().getSSLContext();
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, new SecureRandom());

            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = trustManager;

        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
        return sslParams;
    }

    private static KeyManager[] getKeyManager(Context context, SSLCertificateListener listener) {
        if(listener == null || listener.getClientBksParams() == null){
            return null;
        }

        SSLCertificateListener.ClientBksParams params = listener.getClientBksParams();

        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(context.getAssets().open(params.clientBksFile),
                    params.clientBksPass.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, params.clientBksPass.toCharArray());

            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static X509TrustManager getUnSafeTrustManager() {
        return new SSLTrustAllManager();
    }

    private static TrustManager[] prepareTrustManager(Context context, SSLCertificateListener listener) {
        if(listener == null){
            return null;
        }

        try {

            /**
             * 1、构造CertificateFactory对象，通过它的generateCertificate(is)方法获得certificate
             *
             *      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
             *      Certificate certificate = certificateFactory.generateCertificate(is) //is 证书输入流
             *
             * 2、将certificate放入到KeyStore.
             *      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
             *      keyStore.load(null)
             *      keyStore.setCertificateEntry(certificateAlias, certificate)
             *
             * 3、利用KeyStore初始化TrustManagerFactory
             *
             *      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
             *      trustManagerFactory.init(keyStore);
             *
             * 4、获取TrustManager.
             *
             *      trustManagerFactory.getTrustManagers();
             */
            InputStream[] certificates = listener.genCertificateIs(context);
            if(certificates == null || certificates.length == 0){
                return null;
            }

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {

                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                certificate.close();
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            return trustManagerFactory.getTrustManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        if (trustManagers == null) {
            return null;
        }
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private static class SSLTrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(trustManagerFactory.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (Exception ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;
    }
}
