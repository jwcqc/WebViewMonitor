package me.hyman.webviewmonitor.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by chengbin on 2016/10/9.
 */
public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
        super.addJavascriptInterface(new JSObject(), "myObj");
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.addJavascriptInterface(new JSObject(), "myObj");
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.addJavascriptInterface(new JSObject(), "myObj");
    }

}
