package com.derek.qlcplusremote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;


public class ConnectActivity extends AppCompatActivity {
    private EditText txtIp;
    private EditText txtPort;
    private Switch chbAutoconnect;

    public static final String SHARED_PREFS ="sharedPrefs";
    public static final String ip = "ip";
    public static final String autosave ="autosave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        txtIp = (EditText) findViewById(R.id.txtIp);
        chbAutoconnect = (Switch) findViewById(R.id.chbAutoconnect);
        loadData();
    }
    public void connect(View v){
        saveData();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(ip , txtIp.getText().toString());
        startActivity(i);
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ip, txtIp.getText().toString());
        editor.putBoolean(autosave, chbAutoconnect.isChecked());
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String ip= sharedPreferences.getString(this.ip,"");
        boolean autosave =sharedPreferences.getBoolean(this.autosave,false);
        txtIp.setText(ip);
        chbAutoconnect.setChecked(autosave);
        if (autosave){
            Intent i = new Intent(this, SplashActivity.class);
            i.putExtra(ip , txtIp.getText().toString());
            startActivity(i);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    connect(null);
                }
            }, 1000);
        }
    }

}