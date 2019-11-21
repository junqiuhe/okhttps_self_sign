package com.jackh.okhttps.self.sign.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jackh.okhttps.self.sign.R;
import com.jackh.okhttps.self.sign.widget.CustomStateView;

/**
 * Project Name：okhttps_self_sign
 * Created by hejunqiu on 2019/11/21 15:47
 * Description:
 */
public class WebViewFragment extends Fragment {

    private String mUrl;

    private CustomStateView stateView;
    private WebView webView;

    static WebViewFragment newInstance(String url){
        Bundle params = new Bundle();
        params.putString(WebViewActivity.URL, url);

        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(params);
        return webViewFragment;
    }

    private WebViewListener mWebViewListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof WebViewListener){
            mWebViewListener = (WebViewListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_web_view, container, false);
        initView(itemView);
        return itemView;
    }

    private void initView(View itemView) {
        mUrl = getArguments().getString(WebViewActivity.URL, "https://www.baidu.com");

        stateView = itemView.findViewById(R.id.state_view);

        webView = itemView.findViewById(R.id.web_view);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if(mWebViewListener != null){
                    mWebViewListener.updateTitle(title);
                }
            }
        });
        webView.setWebViewClient(new CustomWebViewClient(getContext(), mUrl, new Listener() {
            @Override
            public void onLoading() {
                stateView.showLoading();
            }

            @Override
            public void onFailure() {
                stateView.showError();
            }

            @Override
            public void onSuccess() {
                stateView.showContent();
            }
        }));

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setSavePassword(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        settings.setTextZoom(100);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportMultipleWindows(false);
        // 是否阻塞加载网络图片  协议http or https
        settings.setBlockNetworkImage(false);
        // 允许加载本地文件html  file协议
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            settings.setAllowFileAccessFromFileURLs(false);
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            settings.setAllowUniversalAccessFromFileURLs(false);
        }
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setNeedInitialFocus(true);
        settings.setDefaultTextEncodingName("utf-8"); //设置编码格式
        settings.setDefaultFontSize(16);
        settings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        settings.setGeolocationEnabled(true);

        //
//        val dir = AgentWebConfig.getCachePath(webView.getContext())
//        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
//        mWebSettings.setGeolocationDatabasePath(dir)
//        mWebSettings.setDatabasePath(dir)
//        mWebSettings.setAppCachePath(dir)

        //缓存文件最大值
        settings.setAppCacheMaxSize(java.lang.Long.MAX_VALUE);
        settings.setUserAgentString(settings.getUserAgentString() + ";zfb_app");

        webView.loadUrl(mUrl);
        stateView.setOnRetryBtnClickListener(view -> {
            webView.loadUrl(mUrl);
            return null;
        });
    }

    private static class CustomWebViewClient extends WebViewClient {

        private Context context;
        private String mOriginUrl;

        private Handler mHandler;

        private Listener mListener;

        CustomWebViewClient(Context context, String originUrl, Listener listener) {
            this.context = context;
            this.mOriginUrl = originUrl;

            this.mListener = listener;
            this.mHandler = new Handler(context.getMainLooper());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (request != null && request.getUrl() != null && !TextUtils.isEmpty(request.getUrl().getScheme())) {
                    String scheme = request.getUrl().getScheme();
                    if (scheme.startsWith("https") || scheme.startsWith("http")) {
                        view.loadUrl(request.getUrl().toString());
                    } else {
                        if (scheme.startsWith("intent")) {
                            handleInnerUrl(request.getUrl().toString());
                        }
                    }
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mHandler.post(() -> {
                if (mListener != null) {
                    mListener.onLoading();
                }
            });
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mHandler.post(() -> {
                if (mListener != null) {
                    mListener.onSuccess();
                }
            });
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (request.isForMainFrame()) {
                    mHandler.post(() -> {
                        if (mListener != null) {
                            mListener.onFailure();
                        }
                    });
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mOriginUrl.toUpperCase().equals(failingUrl.toUpperCase())) {
                mHandler.post(() -> {
                    if (mListener != null) {
                        mListener.onFailure();
                    }
                });
            }
        }

        private void handleInnerUrl(String url) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private interface Listener {
        void onLoading();

        void onFailure();

        void onSuccess();
    }

    public interface WebViewListener{
        void updateTitle(String title);
    }
}
