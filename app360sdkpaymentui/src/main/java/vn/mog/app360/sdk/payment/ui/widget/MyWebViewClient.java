package vn.mog.app360.sdk.payment.ui.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
    private ProgressDialog progressDialog;

    public MyWebViewClient(Context context) {
        super();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Đang tải...");
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        progressDialog.show();
    }

    @Override
    public void onPageFinished(WebView view, final String url) {
        progressDialog.dismiss();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        progressDialog.dismiss();
        view.loadUrl("");
    }
}
