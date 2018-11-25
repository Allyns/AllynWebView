package com.allyn.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyWebView mWebView;
    private ContentLoadingProgressBar mProSchedule;
    private FrameLayout mFrameLayout;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private ValueCallback<Uri[]> uploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = findViewById(R.id.frameLayout);
        mProSchedule = findViewById(R.id.pro_schedule);
        mWebView = findViewById(R.id.webview);
        mWebView.loadUrl("https://m.baidu.com/");
        setClient();

    }

    private void setClient() {
        //帮助WebView处理各种通知、请求事件
        mWebView.setWebViewClient(new WebViewClient() {

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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //网址处理
                if (url == null) return false;
                //外链判断处理
                try {
                    if (url.startsWith("weixin://")
                            || url.startsWith("alipays://")
                            || url.startsWith("mailto://")
                            || url.startsWith("tel://")
                            || url.startsWith("baiduhaokan://")
                            || url.startsWith("baiduboxapp://")
                            || url.startsWith("tbopen://")
                            || url.startsWith("youku://")
                            || url.startsWith("dianping://")
                        //其他自定义的scheme
                            ) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }
                /**
                 * 可对指定网址进行拦截
                 */
                view.loadUrl(url);
                return true;
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
        mWebView.setWebChromeClient(new WebChromeClient() {
            //视图View
            private View mCustomView;
            // 一个回调接口使用的主机应用程序通知当前页面的自定义视图已被撤职
            private CustomViewCallback mCustomViewCallback;

            //网页加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100) {
                    mProSchedule.setProgress(newProgress);
                }
            }

            //网页进入全屏模式监听
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                //赋值，关闭时需要调用
                mCustomView = view;
                // 将video放到FrameLayout中
                mFrameLayout.addView(mCustomView);
                //  退出全屏模式时释放需要调用，这里先赋值
                mCustomViewCallback = callback;
                // 设置webView隐藏
                mWebView.setVisibility(View.GONE);
                //切换至横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            //网页退出全屏模式监听
            @Override
            public void onHideCustomView() {
                //显示竖屏时候的webview
                mWebView.setVisibility(View.VISIBLE);
                if (mCustomView == null) {
                    return;
                }
                //隐藏
                mCustomView.setVisibility(View.GONE);
                //从当前视图中移除
                mFrameLayout.removeView(mCustomView);
                //释放自定义视图
                mCustomViewCallback.onCustomViewHidden();
                mCustomView = null;
                //切换至竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                super.onHideCustomView();
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessage = filePathCallback;
                //网页文件上传回调
                //这里打开图库
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_CHOOSE);
                return true;
            }
        });

        mWebView.setOnSelectItemListener(new MyWebView.onSelectItemListener() {
            @Override
            public void onImgSelected(int x, int y, int type, String extra) {
                String[] menus = new String[]{"保存图片", "预览图片", "复制图片链接", "分享图片"};
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(menus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case 0:
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        Toast.makeText(MainActivity.this, "复制图片链接点击了", Toast.LENGTH_LONG).show();
                                        break;
                                    case 3:
                                        break;
                                }
                            }
                        }).show();
            }

            @Override
            public void onLinkSelected(int x, int y, int type, String extra) {
                String[] menus = new String[]{"复制链接地址", "新窗口打开"};
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(menus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case 0:
                                        break;
                                    case 1:
                                        Toast.makeText(MainActivity.this, "新窗口打开点击了", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }

    //后退
    public void clickReturn(View view) {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    //前进
    public void clickAhead(View view) {
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    //刷新
    public void clickReload(View view) {
        mWebView.reload();
    }

    //暂停
    public void clickStop(View view) {
        mWebView.stopLoading();
    }

    //屏幕旋转监听
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            //竖屏方向
            case Configuration.ORIENTATION_LANDSCAPE:
                //设置全屏
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            //横屏方向
            case Configuration.ORIENTATION_PORTRAIT:
                //关闭全屏
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //网页上传图片回调
        if (requestCode == REQUEST_CODE_CHOOSE) {
            //图片选择后返回图标，通过uploadMessage将图片传给网页
            if (uploadMessage != null) {
                onActivityResultAboveL(resultCode, data);
            }
        }
    }

    //处理网页回调
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int resultCode, Intent intent) {
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessage.onReceiveValue(results);
        uploadMessage = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        } else {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }
}
