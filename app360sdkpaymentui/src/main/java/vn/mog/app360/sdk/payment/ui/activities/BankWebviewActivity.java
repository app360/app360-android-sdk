package vn.mog.app360.sdk.payment.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import vn.mog.app360.sdk.payment.ui.widget.MyWebViewClient;
import vn.mog.app360.sdk.payment.utils.Const;
import vn.mog.app360.sdk.payment.ui.R;

public class BankWebviewActivity extends Activity {
    private WebView webView;
    private String link = "";
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_mwork_bank_weview);

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient(this));
        webView.setWebChromeClient(new WebChromeClient());
        webView.setHorizontalScrollbarOverlay(false);
        webView.setVerticalScrollbarOverlay(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setAnimationCacheEnabled(false);
        webView.setDrawingCacheEnabled(false);
        webView.setDrawingCacheBackgroundColor(getResources().getColor(android.R.color.background_light));
        webView.setWillNotCacheDrawing(true);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setSaveEnabled(true);
        webView.setInitialScale(10);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setLightTouchEnabled(true);
        settings.setSupportMultipleWindows(false);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath());
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setLoadsImagesAutomatically(true);

        this.imageBack = (ImageView) findViewById(R.id.com_mwork_app_icon);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            link = intent.getStringExtra(Const.BANK_URL_BUNDLE_KEY);
            webView.loadUrl(link);
        }
    }
}
