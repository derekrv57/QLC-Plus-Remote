package com.derek.qlcplusremote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
        progressDialog.setMessage("Connecting to "+ip+".\nPlease wait...");
        progressDialog.setCancelable(false);
        progressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
                MainActivity.this.finish();
                qlcWebView.stopLoading();
            }
        });
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
                    Toast.makeText(MainActivity.this, error.getDescription().toString().replace("net::ERR","").replace("_"," "), Toast.LENGTH_LONG).show();
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
        if(message.toString().equals("net::ERR_CONNECTION_REFUSED")){
            qlcWebView.setVisibility(View.INVISIBLE);
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(message.toString().replace("net::ERR","").replace("_"," "));
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       System.exit(0);
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.derek.qlcplusremote");
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        AlertDialog alert1 = builder1.create();
        alert1.show();
    }

}