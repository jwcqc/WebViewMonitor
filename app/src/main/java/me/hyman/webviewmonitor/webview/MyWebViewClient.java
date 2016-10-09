package me.hyman.webviewmonitor.webview;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by chengbin on 2016/10/9.
 */
public class MyWebViewClient extends WebViewClient {

    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        String injectJs = "";

        if(!injectJs.equals("")) {
            String msg = "javascript:" +
                    "   (function() { " +
                    "       var script=document.createElement('script');  " +
                    "       script.setAttribute('type','text/javascript');  " +
                    "       script.setAttribute('src', '" + injectJs + "'); " +
                    "       document.head.appendChild(script); " +
                    "       script.onload = function() {" +
                    "           startWebViewMonitor();" +
                    "       }; " +
                    "    }" +
                    "    )();";

            view.loadUrl(msg);
        }
    }
}
