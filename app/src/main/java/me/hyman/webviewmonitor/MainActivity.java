package me.hyman.webviewmonitor;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;

import me.hyman.webviewmonitor.webview.MyWebView;
import me.hyman.webviewmonitor.webview.MyWebViewClient;

public class MainActivity extends AppCompatActivity {

    private MyWebView webview;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WebView监控");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        webview = (MyWebView) findViewById(R.id.webview);
        WebSettings setting = webview.getSettings();
        setting.setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());

        String url = "http://www.ifanr.com/";
        webview.loadUrl(url);
    }
}
