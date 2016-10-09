package me.hyman.webviewmonitor.webview;

import android.webkit.JavascriptInterface;

import me.hyman.webviewmonitor.util.Logger;

/**
 * Created by chengbin on 2016/10/9.
 */
public class JSObject {

    @JavascriptInterface
    public void sendResource(String msg) {
        handleResource(msg);
    }

    @JavascriptInterface
    public void sendError(String msg) {
        handleError(msg);
    }

    public void handleError(String msg) {

        Logger.i("====================== come in handleError");
        Logger.i(msg);
    }

    public void handleResource(String msg) {

        Logger.i("====================== come in handleResource");
        Logger.i(msg);
    }

}
