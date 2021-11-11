package com.derek.qlcplusremote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView qlcWebView;
    private ProgressDialog progressDialog;
    private boolean errorLoad=false;
    boolean loaded=false;
    String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        ip = i.getStringExtra(ConnectActivity.ip);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qlcWebView = (WebView) findViewById(R.id.webview);
        qlcWebView.getSettings().setJavaScriptEnabled(true);
        qlcWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting please wait...");
        progressDialog.show();
        qlcWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            public void onLoadResource(WebView view, String url) {

            }
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (!errorLoad) {
                    qlcWebView.setVisibility(View.VISIBLE);
                }
                loaded=true;
            }
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                if (loaded){
                    reloadAlert(error.getDescription().toString());
                }
                else{
                    errorLoad=true;
                    finish();
                    Toast.makeText(MainActivity.this, error.getDescription(), Toast.LENGTH_LONG).show();
                }
            }

        });
        qlcWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

            }

            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (message.equals("QLC+ connection lost!")){
                    reloadAlert(message);
                }
                else{
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        qlcWebView.loadUrl("http://"+ip+":9999/");
    }

    public void onBackPressed(){
        finish();
    }

    public void reloadAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Reconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        qlcWebView.reload();
                        progressDialog = new ProgressDialog( MainActivity.this);
                        progressDialog.setMessage("Connecting please wait...");
                        progressDialog.show();
                    }
                });
        builder1.setNegativeButton(
                "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert1 = builder1.create();
        alert1.show();
    }

}