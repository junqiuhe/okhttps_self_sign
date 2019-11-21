package com.jackh.okhttps.self.sign.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jackh.okhttps.self.sign.R;


/**
 * Project Name：okhttps_self_sign
 * Created by hejunqiu on 2019/11/21 14:12
 * Description:
 */
public class WebViewActivity extends AppCompatActivity implements WebViewFragment.WebViewListener {

    private final String TAG_WEB_VIEW = "tag_web_view";

    private static final String TITLE = "params_title";
    static final String URL = "params_url";

    private String mTitle;
    private String mUrl;

    public static void launchWebView(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(URL, url);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        mTitle = intent.getStringExtra(TITLE);
        mUrl = intent.getStringExtra(URL);

        setTitle(mTitle);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_WEB_VIEW);
        if(fragment == null){
            fragment = WebViewFragment.newInstance(mUrl);
        }
        if(!fragment.isAdded()){
            FragmentTransaction ts = getSupportFragmentManager().beginTransaction();
            ts.replace(R.id.web_view_container, fragment, TAG_WEB_VIEW);
            ts.commitAllowingStateLoss();
        }
    }

    @Override
    public void updateTitle(String title) {
        if(!TextUtils.isEmpty(mTitle)){
            return;
        }
        setTitle(title);
    }
}
