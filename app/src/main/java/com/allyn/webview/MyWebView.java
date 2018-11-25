package com.allyn.webview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by allyn on 2018/11/18 0018.
 */

public class MyWebView extends WebView {

    private onSelectItemListener mOnSelectItemListener;
    private int touchX = 0, touchY = 0;

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        longClick();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        WebSettings mSettings = getSettings();
        // 支持获取手势焦点
        requestFocusFromTouch();
        setHorizontalFadingEdgeEnabled(true);
        setVerticalFadingEdgeEnabled(false);
        setVerticalScrollBarEnabled(false);
        // 支持JS
        mSettings.setJavaScriptEnabled(true);
        mSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mSettings.setBuiltInZoomControls(true);
        mSettings.setDisplayZoomControls(true);
        mSettings.setLoadWithOverviewMode(true);
        // 支持插件
        mSettings.setPluginState(WebSettings.PluginState.ON);
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 自适应屏幕
        mSettings.setUseWideViewPort(true);
        mSettings.setLoadWithOverviewMode(true);
        // 支持缩放
        mSettings.setSupportZoom(false);//就是这个属性把我搞惨了，
        // 隐藏原声缩放控件
        mSettings.setDisplayZoomControls(false);
        // 支持内容重新布局
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mSettings.supportMultipleWindows();
        mSettings.setSupportMultipleWindows(true);
        // 设置缓存模式
        mSettings.setDomStorageEnabled(true);
        mSettings.setDatabaseEnabled(true);
        mSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mSettings.setAppCacheEnabled(true);
        mSettings.setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        // 设置可访问文件
        mSettings.setAllowFileAccess(true);
        mSettings.setNeedInitialFocus(true);
        mSettings.setBlockNetworkImage(false);
        // 支持自定加载图片
        if (Build.VERSION.SDK_INT >= 19) {
            mSettings.setLoadsImagesAutomatically(true);
        } else {
            mSettings.setLoadsImagesAutomatically(false);
        }
        mSettings.setNeedInitialFocus(true);
        // 设定编码格式
        mSettings.setDefaultTextEncodingName("UTF-8");
    }

    private void longClick() {
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HitTestResult result = getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                String extra = result.getExtra();
                switch (type) {
                    case HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case HitTestResult.GEO_TYPE: // 　地图类型
                        break;
                    case HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        if (mOnSelectItemListener != null && extra != null && URLUtil.isValidUrl(extra)) {
                            mOnSelectItemListener.onLinkSelected(touchX, touchY, result.getType(), extra);
                        }
                        return true;
                    case HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                    case HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        if (mOnSelectItemListener != null && extra != null && URLUtil.isValidUrl(extra)) {
                            mOnSelectItemListener.onImgSelected(touchX, touchY, result.getType(), extra);
                        }
                        return true;
                    case HitTestResult.UNKNOWN_TYPE: //未知
                        break;
                    case HitTestResult.EDIT_TEXT_TYPE://文字
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        touchX = (int) event.getX();
        touchY = (int) event.getY();
        return super.onInterceptTouchEvent(event);
    }

    public void setOnSelectItemListener(onSelectItemListener onSelectItemListener) {
        mOnSelectItemListener = onSelectItemListener;
    }

    public interface onSelectItemListener {
        void onImgSelected(int x, int y, int type, String extra);

        void onLinkSelected(int x, int y, int type, String extra);
    }
}
