package com.allyn.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private MyWebView mWebview;
    private ContentLoadingProgressBar mProSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProSchedule = findViewById(R.id.pro_schedule);
        mWebview = findViewById(R.id.webview);
        mWebview.loadUrl("http://www.baidu.com");
        //帮助WebView处理各种通知、请求事件
        mWebview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProSchedule.setVisibility(View.VISIBLE);
                //开始
                /**
                 * 网页重定向时会执行多次
                 */
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                //网页加载成功

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
                //网址处理
                /**
                 * 可对指定网址进行拦截
                 */
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //网页加载失败
                /**
                 * 此回调中可进行自定义错误页面，
                 * 遇到错误时示例代码:view.loadUrl("file://android_asset/error.html");
                 */
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProSchedule.setVisibility(View.GONE);
                //网页加载完成
            }
        });

        //辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //网页加载进度
                if (newProgress < 100) {
                    mProSchedule.setProgress(newProgress);
                }
            }
        });

    }

    //后退
    public void clickReturn(View view) {
        if (mWebview.canGoBack()) {
            mWebview.goBack();
        }
    }

    //前进
    public void clickAhead(View view) {
        if (mWebview.canGoForward()) {
            mWebview.goForward();
        }
    }

    //刷新
    public void clickReload(View view) {
        mWebview.reload();
    }

    //暂停
    public void clickStop(View view) {
        mWebview.stopLoading();
    }
}
