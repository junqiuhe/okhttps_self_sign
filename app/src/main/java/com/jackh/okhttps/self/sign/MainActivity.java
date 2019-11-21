package com.jackh.okhttps.self.sign;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jackh.okhttps.self.sign.utils.OkHttpUtils;
import com.jackh.okhttps.self.sign.webview.WebViewActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String SELF_SIGN_HTTPS_URL = "https://172.16.61.16:8443/";

    private static final String SELF_SIGN_HTTP_URL = "http://172.16.61.16:8080/";

    private static final String TAG = "JackH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.launchHttpBtn).setOnClickListener(v -> {
            WebViewActivity.launchWebView(this, "", SELF_SIGN_HTTP_URL);
        });

        findViewById(R.id.launchHttpsBtn).setOnClickListener(v -> {
            WebViewActivity.launchWebView(this, "", SELF_SIGN_HTTPS_URL);
        });

        /**
         * 百度的证书是经过CA认证过，因此通过WebView显示时，无须做多余的处理.
         */
        findViewById(R.id.launchBaiduBtn).setOnClickListener(v -> {
            WebViewActivity.launchWebView(this, "", "https://www.baidu.com");
        });
    }

    public void getSelfSignServerData(View view){
        RequestBody requestBody = new FormBody.Builder()
                .add("username","jackH")
                .add("password", "123456")
                .build();

        Request request = new Request.Builder()
                .url(SELF_SIGN_HTTPS_URL)
                .post(requestBody)
                .build();

        OkHttpClient client = OkHttpUtils.getInstance(this).getOkHttpClient();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful() && response.body() != null){
                    Log.d(TAG, response.body().string());
                }else{
                    Log.e(TAG, "request network error.");
                }
            }
        });
    }
}